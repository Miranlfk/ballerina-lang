/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.compiler.api.impl.types;

import io.ballerina.compiler.api.impl.SymbolFactory;
import io.ballerina.compiler.api.impl.symbols.AbstractTypeSymbol;
import io.ballerina.compiler.api.impl.symbols.BallerinaSymbol;
import io.ballerina.compiler.api.impl.symbols.TypesFactory;
import io.ballerina.compiler.api.symbols.FunctionTypeSymbol;
import io.ballerina.compiler.api.symbols.ParameterKind;
import io.ballerina.compiler.api.symbols.ParameterSymbol;
import io.ballerina.compiler.api.symbols.TypeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolTable;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BInvokableTypeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BVarSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.SymTag;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.Symbols;
import org.wso2.ballerinalang.compiler.semantics.model.types.BInvokableType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.Names;
import org.wso2.ballerinalang.util.Flags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.ballerinalang.model.symbols.SymbolOrigin.COMPILED_SOURCE;

/**
 * The implementation of the methods used to build the Function type descriptor in Types API.
 *
 * @since 2201.2.0
 */
public class BallerinaFunctionTypeBuilder implements TypeBuilder.FUNCTION {

    private final CompilerContext context;
    private final TypesFactory typesFactory;
    private final SymbolTable symTable;
    private List<ParameterSymbol> parameterSymbols = new ArrayList<>();
    private ParameterSymbol restParam;
    private TypeSymbol returnTypeSymbol;

    public PARAMETER_BUILDER PARAM_BUILDER;

    public BallerinaFunctionTypeBuilder(CompilerContext context) {
        this.context = context;
        typesFactory = TypesFactory.getInstance(context);
        symTable = SymbolTable.getInstance(context);
        PARAM_BUILDER = new ParameterBuilder(context);
    }

    @Override
    public TypeBuilder.FUNCTION withParams(ParameterSymbol... parameters) {
        parameterSymbols.clear();
        parameterSymbols.addAll(Arrays.asList(parameters));

        return this;
    }

    @Override
    public TypeBuilder.FUNCTION withRestParam(ParameterSymbol restParam) {
        this.restParam = restParam;

        return this;
    }

    @Override
    public TypeBuilder.FUNCTION withReturnType(TypeSymbol returnType) {
        this.returnTypeSymbol = returnType;

        return this;
    }

    @Override
    public PARAMETER_BUILDER params() {
        return new ParameterBuilder(context);
    }

    @Override
    public FunctionTypeSymbol build() {
        List<BType> paramTypes = getParamTypes(parameterSymbols);
        BType restType = restParam != null ? getBType(restParam.typeDescriptor()) : null;
        BType returnType = getReturnBType(returnTypeSymbol);
        BInvokableTypeSymbol tSymbol = Symbols.createInvokableTypeSymbol(SymTag.FUNCTION_TYPE, 0,
                symTable.rootPkgSymbol.pkgID, symTable.invokableType, symTable.rootPkgNode.symbol.scope.owner,
                symTable.builtinPos, COMPILED_SOURCE);
        tSymbol.returnType = returnType;
        tSymbol.params = getBParamSymbols(parameterSymbols);
        tSymbol.restParam = restParam != null ? (BVarSymbol) ((BallerinaSymbol) restParam).getInternalSymbol() : null;
        BInvokableType bInvokableType = new BInvokableType(paramTypes, restType, returnType, tSymbol);
        FunctionTypeSymbol functionTypeSymbol = (FunctionTypeSymbol) typesFactory.getTypeDescriptor(bInvokableType);
        parameterSymbols.clear();
        restParam = null;
        returnTypeSymbol = null;

        return functionTypeSymbol;
    }

    private List<BVarSymbol> getBParamSymbols(List<ParameterSymbol> parameterSymbols) {
        List<BVarSymbol> params = new ArrayList<>();
        for (ParameterSymbol parameterSymbol : parameterSymbols) {
            params.add((BVarSymbol) ((BallerinaSymbol) parameterSymbol).getInternalSymbol());
        }

        return params;
    }

    private List<BType> getParamTypes(List<ParameterSymbol> parameterSymbols) {
        List<BType> parameterTypes = new ArrayList<>();
        for (ParameterSymbol parameterSymbol : parameterSymbols) {
            parameterTypes.add(getBType(parameterSymbol.typeDescriptor()));
        }

        return parameterTypes;
    }

    private BType getBType(TypeSymbol typeSymbol) {
        if (typeSymbol instanceof AbstractTypeSymbol) {
            return ((AbstractTypeSymbol) typeSymbol).getBType();
        }

        throw new IllegalArgumentException("Invalid type provided");
    }

    private BType getReturnBType(TypeSymbol returnTypeSymbol) {
        if (returnTypeSymbol == null) {
            return null;
        }

        if (returnTypeSymbol instanceof AbstractTypeSymbol
                && (returnTypeSymbol.subtypeOf(typesFactory.getTypeDescriptor(symTable.anyType))
                || returnTypeSymbol.subtypeOf(typesFactory.getTypeDescriptor(symTable.nilType)))) {
            return ((AbstractTypeSymbol) returnTypeSymbol).getBType();
        }

        throw new IllegalArgumentException("Invalid return type provided");
    }

    /**
     * The implementation of the methods used to build a Parameter of a function type descriptor.
     *
     * @since 2.0.0
     */
    public class ParameterBuilder implements PARAMETER_BUILDER {

        private final SymbolFactory symbolFactory;
        private String name;
        private TypeSymbol type;
        private ParameterKind kind;

        private ParameterBuilder(CompilerContext context) {
            symbolFactory = SymbolFactory.getInstance(context);
        }

        @Override
        public PARAMETER_BUILDER withName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public PARAMETER_BUILDER withType(TypeSymbol type) {
            this.type = type;
            return this;
        }

        @Override
        public PARAMETER_BUILDER withKind(ParameterKind kind) {
            this.kind = kind;
            return this;
        }

        @Override
        public ParameterSymbol build() {
            if (name == null) {
                throw new IllegalArgumentException("Parameter name can not be null");
            }

            long flags = Flags.REQUIRED_PARAM;
            if (kind == ParameterKind.DEFAULTABLE) {
                flags = Flags.DEFAULTABLE_PARAM;
            } else if (kind == ParameterKind.REST) {
                flags = Flags.REST_PARAM;
            } else if (kind == ParameterKind.INCLUDED_RECORD) {
                flags = Flags.INCLUDED;
            }

            BVarSymbol bVarSymbol = new BVarSymbol(flags, Names.fromString(name),
                    Names.fromString(name), symTable.rootPkgSymbol.pkgID, getBType(type),
                    symTable.rootPkgSymbol.owner, symTable.builtinPos, symTable.rootPkgSymbol.origin);

            return (ParameterSymbol) symbolFactory.getBCompiledSymbol(bVarSymbol, name);
        }

        private BType getBType(TypeSymbol typeSymbol) {
            if (typeSymbol instanceof AbstractTypeSymbol) {
                    return ((AbstractTypeSymbol) typeSymbol).getBType();
            }

            throw new IllegalArgumentException("Invalid type provided");
        }
    }
}
