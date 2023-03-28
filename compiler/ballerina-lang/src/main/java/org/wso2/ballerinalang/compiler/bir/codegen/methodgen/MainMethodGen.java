/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ballerinalang.compiler.bir.codegen.methodgen;

import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.model.types.RecordType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmCastGen;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmCodeGenUtil;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmTypeGen;
import org.wso2.ballerinalang.compiler.bir.codegen.internal.AsyncDataCollector;
import org.wso2.ballerinalang.compiler.bir.codegen.internal.BIRVarToJVMIndexMap;
import org.wso2.ballerinalang.compiler.bir.model.BIRNode;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolTable;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.CLI_SPEC;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.COMPATIBILITY_CHECKER;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.CONFIGURATION_CLASS_NAME;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.CONFIGURE_INIT;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.CURRENT_MODULE_VAR_NAME;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.FUTURE_VALUE;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.HANDLE_ALL_THROWABLE_METHOD;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.HANDLE_RETURNED_ERROR_METHOD;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.HANDLE_THROWABLE_METHOD;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.JAVA_RUNTIME;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.JVM_INIT_METHOD;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.LAMBDA_PREFIX;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.LAUNCH_UTILS;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.MAIN_METHOD;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.MODULE_INIT_CLASS_NAME;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.MODULE_STOP_METHOD;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.OBJECT;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.OPERAND;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.OPTION;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.PANIC_FIELD;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.PATH;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.RUNTIME_UTILS;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.SCHEDULER;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.SCHEDULER_START_METHOD;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.STACK;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.STRAND;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.STRAND_CLASS;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.STRING_VALUE;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.TEST_ARGUMENTS;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.TEST_EXECUTION_STATE;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.THROWABLE;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.TOML_DETAILS;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.ADD_SHUTDOWN_HOOK;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.GET_MAIN_ARGS;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.GET_MODULE;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.GET_OBJECT;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.GET_RUNTIME;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.GET_RUNTIME_REGISTRY_CLASS;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.GET_STRAND;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.GET_TEST_CONFIG_PATH;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.GET_THROWABLE;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.GET_TOML_DETAILS;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.HANDLE_ERROR_RETURN;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.HANDLE_THROWABLE;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.INIT_CLI_SPEC;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.INIT_CONFIG;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.INIT_OPERAND;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.INIT_OPTION;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.INIT_RUNTIME_REGISTRY;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.INIT_TEST_ARGS;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.MAIN_METHOD_SIGNATURE;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.METHOD_STRING_PARAM;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.STACK_FRAMES;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.VOID_METHOD_DESC;

/**
 * Generates Jvm byte code for the main method.
 *
 * @since 2.0.0
 */
public class MainMethodGen {

    public static final String INIT_FUTURE_VAR = "initFutureVar";
    public static final String START_FUTURE_VAR = "startFutureVar";
    public static final String MAIN_FUTURE_VAR = "mainFutureVar";
    public static final String SCHEDULER_VAR = "schedulerVar";
    public static final String CONFIG_VAR = "configVar";
    public static final String LAMBDA_MAIN_METHOD = "$lambda$main$";
    private final SymbolTable symbolTable;
    private final BIRVarToJVMIndexMap indexMap;
    private final JvmTypeGen jvmTypeGen;
    private final JvmCastGen jvmCastGen;
    private final AsyncDataCollector asyncDataCollector;

    public MainMethodGen(SymbolTable symbolTable, JvmTypeGen jvmTypeGen,
                         JvmCastGen jvmCastGen, AsyncDataCollector asyncDataCollector) {
        this.symbolTable = symbolTable;
        // add main string[] args param first
        indexMap = new BIRVarToJVMIndexMap(1);
        this.jvmTypeGen = jvmTypeGen;
        this.jvmCastGen = jvmCastGen;
        this.asyncDataCollector = asyncDataCollector;
    }

    public void generateMainMethod(BIRNode.BIRFunction userMainFunc, BIRNode.BIRFunction testExecuteFunc,
                                   ClassWriter cw, BIRNode.BIRPackage pkg, String initClass,
                                   boolean serviceEPAvailable) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + ACC_STATIC, MAIN_METHOD, MAIN_METHOD_SIGNATURE,
                null, null);
        mv.visitCode();
        Label tryCatchStart = new Label();
        Label tryCatchEnd = new Label();
        Label tryCatchHandle = new Label();
        mv.visitTryCatchBlock(tryCatchStart, tryCatchEnd, tryCatchHandle, THROWABLE);
        mv.visitLabel(tryCatchStart);

        // check for java compatibility
        generateJavaCompatibilityCheck(mv);

        invokeConfigInit(mv, pkg.packageID);
        // start all listeners and TRAP signal handler
        startListenersAndSignalHandler(mv, serviceEPAvailable);

        genInitScheduler(mv);
        // register a shutdown hook to call package stop() method.
        genShutdownHook(mv, initClass);

        boolean hasInitFunction = MethodGenUtils.hasInitFunction(pkg);
        generateExecuteFunctionCall(initClass, mv, userMainFunc, testExecuteFunc);
        if (hasInitFunction) {
            setListenerFound(mv, serviceEPAvailable);
        }
        stopListeners(mv, serviceEPAvailable);
        if (!serviceEPAvailable && testExecuteFunc == null) {
            JvmCodeGenUtil.generateExitRuntime(mv);
        }

        if (testExecuteFunc != null) {
            generateModuleStopCall(initClass, mv);
        }
        mv.visitLabel(tryCatchEnd);
        mv.visitInsn(RETURN);
        mv.visitLabel(tryCatchHandle);
        mv.visitMethodInsn(INVOKESTATIC, RUNTIME_UTILS, HANDLE_ALL_THROWABLE_METHOD,
                           HANDLE_THROWABLE, false);
        mv.visitInsn(RETURN);
        JvmCodeGenUtil.visitMaxStackForMethod(mv, "main", initClass);
        mv.visitEnd();
    }

    private void generateExecuteFunctionCall(String initClass, MethodVisitor mv,
                                             BIRNode.BIRFunction userMainFunc,
                                             BIRNode.BIRFunction testExecuteFunc) {
        mv.visitVarInsn(ALOAD, indexMap.get(SCHEDULER_VAR));
        if (userMainFunc != null) {
            loadCLIArgsForMain(mv, userMainFunc.parameters, userMainFunc.annotAttachments);
        } else if (testExecuteFunc != null) {
            loadCLIArgsForTestExecute(mv);
        } else {
            mv.visitIntInsn(BIPUSH, 1);
            mv.visitTypeInsn(ANEWARRAY, OBJECT);
        }
        // invoke the module execute method
        genSubmitToScheduler(initClass, mv, LAMBDA_PREFIX + JvmConstants.MODULE_EXECUTE_METHOD + "$",
                JvmConstants.MAIN_METHOD, MainMethodGen.INIT_FUTURE_VAR, testExecuteFunc != null);
        genReturn(mv, indexMap, MainMethodGen.INIT_FUTURE_VAR);
    }

    private void generateModuleStopCall(String initClass, MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, indexMap.get(SCHEDULER_VAR));
        mv.visitMethodInsn(INVOKEVIRTUAL, SCHEDULER, "getRuntimeRegistry", GET_RUNTIME_REGISTRY_CLASS, false);
        mv.visitMethodInsn(INVOKESTATIC, initClass, MODULE_STOP_METHOD, INIT_RUNTIME_REGISTRY, false);
    }

    private void startScheduler(int schedulerVarIndex, MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, schedulerVarIndex);
        mv.visitMethodInsn(INVOKEVIRTUAL, SCHEDULER, SCHEDULER_START_METHOD, VOID_METHOD_DESC, false);
    }

    private void invokeConfigInit(MethodVisitor mv, PackageID packageID) {
        String configClass = JvmCodeGenUtil.getModuleLevelClassName(packageID, CONFIGURATION_CLASS_NAME);
        mv.visitVarInsn(ALOAD, 0);
        if (!packageID.isTestPkg) {
            mv.visitMethodInsn(INVOKESTATIC, LAUNCH_UTILS, "getConfigurationDetails", GET_TOML_DETAILS,
                    false);
        } else {
            String initClass = JvmCodeGenUtil.getModuleLevelClassName(packageID, MODULE_INIT_CLASS_NAME);
            mv.visitFieldInsn(GETSTATIC, initClass, CURRENT_MODULE_VAR_NAME, GET_MODULE);
            mv.visitLdcInsn(packageID.pkgName.toString());
            mv.visitLdcInsn(packageID.sourceRoot);
            mv.visitMethodInsn(INVOKESTATIC, LAUNCH_UTILS, "getTestConfigPaths", GET_TEST_CONFIG_PATH,
                    false);
        }
        int configDetailsIndex = indexMap.addIfNotExists(CONFIG_VAR, symbolTable.anyType);

        mv.visitVarInsn(ASTORE, configDetailsIndex);

        mv.visitVarInsn(ALOAD, configDetailsIndex);
        mv.visitFieldInsn(GETFIELD, TOML_DETAILS, "paths", "[L" + PATH + ";");
        mv.visitVarInsn(ALOAD, configDetailsIndex);
        mv.visitFieldInsn(GETFIELD, TOML_DETAILS, "configContent", "L" + STRING_VALUE + ";");
        mv.visitMethodInsn(INVOKESTATIC, configClass, CONFIGURE_INIT, INIT_CONFIG, false);
    }

    private void generateJavaCompatibilityCheck(MethodVisitor mv) {
        mv.visitLdcInsn(getJavaVersion());
        mv.visitMethodInsn(INVOKESTATIC, COMPATIBILITY_CHECKER, "verifyJavaCompatibility",
                           METHOD_STRING_PARAM, false);
    }

    private String getJavaVersion() {
        String versionProperty = "java.version";
        String javaVersion = System.getProperty(versionProperty);
        return Objects.requireNonNullElse(javaVersion, "");
    }

    private void startListenersAndSignalHandler(MethodVisitor mv, boolean isServiceEPAvailable) {
        mv.visitLdcInsn(isServiceEPAvailable);
        mv.visitMethodInsn(INVOKESTATIC, LAUNCH_UTILS, "startListenersAndSignalHandler", "(Z)V", false);
    }

    private void genShutdownHook(MethodVisitor mv, String initClass) {
        String shutdownClassName = initClass + "$SignalListener";
        mv.visitMethodInsn(INVOKESTATIC, JAVA_RUNTIME, "getRuntime",
                           GET_RUNTIME, false);
        mv.visitTypeInsn(NEW, shutdownClassName);
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, indexMap.get(SCHEDULER_VAR));
        mv.visitMethodInsn(INVOKEVIRTUAL, SCHEDULER, "getRuntimeRegistry",
                GET_RUNTIME_REGISTRY_CLASS, false);
        mv.visitMethodInsn(INVOKESPECIAL, shutdownClassName, JVM_INIT_METHOD,
                INIT_RUNTIME_REGISTRY, false);
        mv.visitMethodInsn(INVOKEVIRTUAL, JAVA_RUNTIME, "addShutdownHook", ADD_SHUTDOWN_HOOK, false);
    }

    private void genInitScheduler(MethodVisitor mv) {
        mv.visitTypeInsn(NEW , SCHEDULER);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitMethodInsn(INVOKESPECIAL , SCHEDULER , JVM_INIT_METHOD, "(Z)V", false);
        int schedulerVarIndex = indexMap.addIfNotExists(SCHEDULER_VAR, symbolTable.anyType);
        mv.visitVarInsn(ASTORE, schedulerVarIndex);
    }

    private void setListenerFound(MethodVisitor mv, boolean serviceEPAvailable) {
        // need to set immortal=true and start the scheduler again
        if (serviceEPAvailable) {
            int schedulerVarIndex = indexMap.get(SCHEDULER_VAR);
            mv.visitVarInsn(ALOAD, schedulerVarIndex);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL , SCHEDULER, "setListenerDeclarationFound", "(Z)V", false);
            startScheduler(schedulerVarIndex, mv);
        }
    }

    private void storeFuture(BIRVarToJVMIndexMap indexMap, MethodVisitor mv, String futureVar) {
        int mainFutureVarIndex = indexMap.addIfNotExists(futureVar, symbolTable.anyType);
        mv.visitVarInsn(ASTORE, mainFutureVarIndex);
        mv.visitVarInsn(ALOAD, mainFutureVarIndex);
    }

    private void loadCLIArgsForMain(MethodVisitor mv, List<BIRNode.BIRFunctionParameter> params,
                                    List<BIRNode.BIRAnnotationAttachment> annotAttachments) {
        mv.visitTypeInsn(NEW , CLI_SPEC);
        mv.visitInsn(DUP);
        // get defaultable arg names from function annotation
        List<String> defaultableNames = getDefaultableNames(annotAttachments);
        // create function info array
        createFunctionInfoArray(mv, params, defaultableNames);
        // load string[] that got parsed into java main
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL , CLI_SPEC, JVM_INIT_METHOD, INIT_CLI_SPEC, false);
        mv.visitMethodInsn(INVOKEVIRTUAL , CLI_SPEC, "getMainArgs", GET_MAIN_ARGS, false);
    }

    private void loadCLIArgsForTestExecute(MethodVisitor mv) {
        mv.visitTypeInsn(NEW , TEST_ARGUMENTS);
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL , TEST_ARGUMENTS, JVM_INIT_METHOD, INIT_TEST_ARGS, false);
        mv.visitMethodInsn(INVOKEVIRTUAL , TEST_ARGUMENTS, "getArgValues", GET_MAIN_ARGS, false);
    }

    private void createFunctionInfoArray(MethodVisitor mv, List<BIRNode.BIRFunctionParameter> params,
                                         List<String> defaultableNames) {
        int size = params.size();
        if (!params.isEmpty() &&
                JvmCodeGenUtil.getReferredType(params.get(size - 1).type) instanceof RecordType) {
            BIRNode.BIRFunctionParameter param = params.get(size - 1);
            createOption(mv, param, size - 1);
            size--;
        } else if (params.size() >= 2
                && JvmCodeGenUtil.getReferredType(params.get(size - 2).type) instanceof RecordType) {
            BIRNode.BIRFunctionParameter param = params.get(size - 2);
            createOption(mv, param, size - 2);
            size--;
        } else {
            mv.visitInsn(ACONST_NULL);
        }
        mv.visitIntInsn(BIPUSH, size);
        mv.visitTypeInsn(ANEWARRAY , OPERAND);
        int defaultableIndex = 0;
        int arrIndex = 0;
        for (BIRNode.BIRFunctionParameter birFunctionParameter : params) {
            if (birFunctionParameter != null
                    && JvmCodeGenUtil.getReferredType(birFunctionParameter.type) instanceof RecordType) {
                defaultableIndex++;
                continue;
            }
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, arrIndex++);
            mv.visitTypeInsn(NEW , OPERAND);
            mv.visitInsn(DUP);
            if (birFunctionParameter != null) {
                if (birFunctionParameter.hasDefaultExpr) {
                    mv.visitInsn(ICONST_1);
                } else {
                    mv.visitInsn(ICONST_0);
                }
                if (!defaultableNames.isEmpty()) {
                    mv.visitLdcInsn(defaultableNames.get(defaultableIndex++));
                }
                jvmTypeGen.loadType(mv, birFunctionParameter.type);
            }
            mv.visitMethodInsn(INVOKESPECIAL , OPERAND , JVM_INIT_METHOD, INIT_OPERAND, false);
            mv.visitInsn(AASTORE);
        }
    }

    private void createOption(MethodVisitor mv, BIRNode.BIRFunctionParameter param, int location) {
        mv.visitTypeInsn(NEW , OPTION);
        mv.visitInsn(DUP);
        jvmTypeGen.loadType(mv, param.type);
        mv.visitIntInsn(BIPUSH, location);
        mv.visitMethodInsn(INVOKESPECIAL , OPTION , JVM_INIT_METHOD, INIT_OPTION, false);
    }

    private List<String> getDefaultableNames(List<BIRNode.BIRAnnotationAttachment> annotAttachments) {
        List<String> defaultableNames = new ArrayList<>();
        int defaultableIndex = 0;
        for (BIRNode.BIRAnnotationAttachment attachment : annotAttachments) {
            if (attachment != null && attachment.annotTagRef.value.equals(JvmConstants.DEFAULTABLE_ARGS_ANOT_NAME)) {
                Map<String, BIRNode.ConstValue> annotFieldMap =
                        (Map<String, BIRNode.ConstValue>)
                                ((BIRNode.BIRConstAnnotationAttachment) attachment).annotValue.value;

                BIRNode.ConstValue[] annotArrayValue =
                        (BIRNode.ConstValue[]) annotFieldMap.get(JvmConstants.DEFAULTABLE_ARGS_ANOT_FIELD).value;
                for (BIRNode.ConstValue entryOptional : annotArrayValue) {
                    defaultableNames.add(defaultableIndex, (String) entryOptional.value);
                    defaultableIndex += 1;
                }
                break;
            }
        }
        return defaultableNames;
    }

    private void genReturn(MethodVisitor mv, BIRVarToJVMIndexMap indexMap, String futureVar) {
        // store future value
        mv.visitVarInsn(ALOAD, indexMap.get(futureVar));
        mv.visitFieldInsn(GETFIELD , FUTURE_VALUE, "result", GET_OBJECT);

        mv.visitMethodInsn(INVOKESTATIC , RUNTIME_UTILS , HANDLE_RETURNED_ERROR_METHOD,
                           HANDLE_ERROR_RETURN, false);
    }

    private void genSubmitToScheduler(String initClass, MethodVisitor mv, String lambdaName,
                                      String funcName, String futureVar, boolean isTestFunction) {
        JvmCodeGenUtil.createFunctionPointer(mv, initClass, lambdaName);

        // no parent strand
        mv.visitInsn(ACONST_NULL);

        BType anyType = symbolTable.anyType;
        jvmTypeGen.loadType(mv, anyType);
        MethodGenUtils.submitToScheduler(mv, initClass, funcName, asyncDataCollector);
        storeFuture(indexMap, mv, futureVar);
        mv.visitFieldInsn(GETFIELD , FUTURE_VALUE , STRAND,
                         GET_STRAND);
        mv.visitTypeInsn(NEW, STACK);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, STACK, JVM_INIT_METHOD, VOID_METHOD_DESC, false);
        mv.visitFieldInsn(PUTFIELD, STRAND_CLASS, MethodGenUtils.FRAMES, STACK_FRAMES);

        startScheduler(indexMap.get(SCHEDULER_VAR), mv);
        handleErrorFromFutureValue(mv, futureVar, initClass, isTestFunction);
    }

    private void stopListeners(MethodVisitor mv, boolean isServiceEPAvailable) {
        mv.visitLdcInsn(isServiceEPAvailable);
        mv.visitMethodInsn(INVOKESTATIC , LAUNCH_UTILS, "stopListeners", "(Z)V", false);
    }

    private void handleErrorFromFutureValue(MethodVisitor mv, String futureVar, String initClass,
                                            boolean isTestFunction) {
        mv.visitVarInsn(ALOAD, indexMap.get(futureVar));
        mv.visitInsn(DUP);
        mv.visitFieldInsn(GETFIELD , FUTURE_VALUE , PANIC_FIELD,
                          GET_THROWABLE);

        // handle any runtime errors
        Label labelIf = new Label();
        mv.visitJumpInsn(IFNULL, labelIf);
        if (!isTestFunction) {
            mv.visitFieldInsn(GETFIELD , FUTURE_VALUE , PANIC_FIELD, GET_THROWABLE);
            mv.visitMethodInsn(INVOKESTATIC, RUNTIME_UTILS, HANDLE_THROWABLE_METHOD, HANDLE_THROWABLE, false);
            mv.visitInsn(RETURN);
        } else {
            mv.visitInsn(ICONST_1);
            mv.visitFieldInsn(PUTSTATIC, initClass, TEST_EXECUTION_STATE, "I");
        }
        mv.visitLabel(labelIf);
    }

}
