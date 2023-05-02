/*
 *  Copyright (c) 2023, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.cli.cmd;

import io.ballerina.cli.BLauncherCmd;
import io.ballerina.cli.launcher.BallerinaCliCommands;
import io.ballerina.cli.utils.PrintUtils;
import io.ballerina.projects.BalToolsManifest;
import io.ballerina.projects.BalToolsToml;
import io.ballerina.projects.JvmTarget;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.SemanticVersion;
import io.ballerina.projects.internal.BalToolsManifestBuilder;
import io.ballerina.projects.util.ProjectConstants;
import io.ballerina.projects.util.ProjectUtils;
import org.ballerinalang.central.client.exceptions.CentralClientException;
import org.ballerinalang.central.client.exceptions.PackageAlreadyExistsException;
import org.wso2.ballerinalang.compiler.util.Names;
import org.wso2.ballerinalang.util.RepoUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static io.ballerina.cli.cmd.Constants.TOOL_COMMAND;
import static io.ballerina.cli.launcher.LauncherUtils.createLauncherException;
import static io.ballerina.projects.util.ProjectConstants.BAL_TOOLS_TOML;
import static io.ballerina.projects.util.ProjectConstants.HOME_REPO_DEFAULT_DIRNAME;
import static io.ballerina.projects.util.ProjectUtils.validatePackageName;
import static java.nio.file.Files.createDirectories;
import static org.wso2.ballerinalang.programfile.ProgramFileConstants.ANY_PLATFORM;
import static org.wso2.ballerinalang.programfile.ProgramFileConstants.SUPPORTED_PLATFORMS;

/**
 * This class represents the "bal tool" command.
 *
 * @since 2201.6.0
 */
@CommandLine.Command(name = TOOL_COMMAND, description = "Ballerina tool command")
public
class ToolCommand implements BLauncherCmd {
    // TODO: Remove TOOL_ORG_NAME and use what is there in the dir structure once pulled. Can be ballerina or ballerinax
    private static final String TOOL_ORG_NAME = "ballerina";
    private static final String PULL_COMMAND = "pull";
    private static final String UPDATE_COMMAND = "update";
    private static final String LIST_COMMAND = "list";
    private static final String SEARCH_COMMAND = "search";
    private static final String UNINSTALL_COMMAND = "uninstall";

    private final boolean exitWhenFinish;
    private final PrintStream errStream;

    Path balToolsTomlPath = Path.of(
            System.getProperty(CommandUtil.USER_HOME), HOME_REPO_DEFAULT_DIRNAME, BAL_TOOLS_TOML);

    @CommandLine.Parameters(description = "Command name")
    private List<String> argList;

    @CommandLine.Option(names = {"--help", "-h", "?"}, usageHelp = true)
    private boolean helpFlag;

    private String toolId;
    private String version;

    public ToolCommand() {
        this.errStream = System.err;
        this.exitWhenFinish = true;
    }

    public ToolCommand(PrintStream errStream, boolean exitWhenFinish) {
        this.errStream = errStream;
        this.exitWhenFinish = exitWhenFinish;
    }

    @Override
    public String getName() {
        return BallerinaCliCommands.TOOL;
    }

    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("  bal tool\n");
    }

    @Override
    public void printUsage(StringBuilder out) {
        out.append("  bal tool\n");
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {
    }

    @Override
    public void execute() {
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(TOOL_COMMAND);
            errStream.println(commandUsageInfo);
            return;
        }

        if (argList == null || argList.isEmpty()) {
            CommandUtil.printError(this.errStream, "no sub-command given", "bal tool <sub-command> [args]", false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        String command = argList.get(0);
        switch (command) {
            case PULL_COMMAND:
                handlePullCommand();
                break;
            case UPDATE_COMMAND:
                handleUpdateCommand();
                break;
            case LIST_COMMAND:
                handleListCommand();
                break;
            case SEARCH_COMMAND:
                handleSearchCommand();
                break;
            case UNINSTALL_COMMAND:
                handleUninstallCommand();
                break;
            default:
                CommandUtil.printError(this.errStream, "invalid sub-command given", "bal tool <sub-command> [args]",
                        false);
                CommandUtil.exitError(this.exitWhenFinish);
                break;
        }
    }

    private void handlePullCommand() {
        if (argList.size() < 2) {
            CommandUtil.printError(this.errStream, "no tool id given", "bal tool pull <tool-id>[:<version>]", false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }
        if (argList.size() > 2) {
            CommandUtil.printError(
                    this.errStream, "too many arguments", "bal tool pull <tool-id>[:<version>]", false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        String toolIdAndVersion = argList.get(1);
        String[] toolInfo = toolIdAndVersion.split(":");
        if (toolInfo.length == 2) {
            toolId = toolInfo[0];
            version = toolInfo[1];
        } else if (toolInfo.length == 1) {
            toolId = toolIdAndVersion;
            version = Names.EMPTY.getValue();
        } else {
            CommandUtil.printError(errStream, "invalid tool id",
                    "bal tool pull <tool-id>[:<version>]", false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        if (!validatePackageName(toolId)) {
            CommandUtil.printError(errStream, "invalid tool id",
                    "bal tool pull <tool-id>[:<version>]", false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        if (!Names.EMPTY.getValue().equals(version)) {
            try {
                SemanticVersion.from(version);
            } catch (ProjectException e) {
                CommandUtil.printError(errStream, "invalid tool version. " + e.getMessage(),
                        "bal tool pull <tool-id>[:<version>]", false);
                CommandUtil.exitError(this.exitWhenFinish);
                return;
            }
        }
        Path packagePathInBalaCache = ProjectUtils.createAndGetHomeReposPath()
                .resolve(ProjectConstants.REPOSITORIES_DIR).resolve(ProjectConstants.CENTRAL_REPOSITORY_CACHE_NAME)
                .resolve(ProjectConstants.BALA_DIR_NAME)
                .resolve(TOOL_ORG_NAME).resolve(toolId);
        // create directory path in bala cache
        try {
            createDirectories(packagePathInBalaCache);
        } catch (IOException e) {
            CommandUtil.exitError(this.exitWhenFinish);
            throw createLauncherException(
                    "unexpected error occurred while creating tool repository in bala cache: " + e.getMessage());
        }
        if (isToolLocallyAvailable(toolIdAndVersion)) {
            CommandUtil.printError(this.errStream, "tool is already pulled", null, false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        // TODO: remove the hard coded org name once we have an API for tool pulling.
        for (String supportedPlatform : SUPPORTED_PLATFORMS) {
            try {
                boolean hasCompilationErrors = mockPullToolFromCentral(supportedPlatform, packagePathInBalaCache);
//                boolean hasCompilationErrors = pullToolFromCentral(supportedPlatform, packagePathInBalaCache);
                if (hasCompilationErrors) {
                    CommandUtil.printError(this.errStream, "compilation contains errors", null, false);
                    CommandUtil.exitError(this.exitWhenFinish);
                    return;
                }
                String toolPathInCentralCache = getToolPathInCentralCache(TOOL_ORG_NAME, toolId, version);
                if (toolPathInCentralCache == null) {
                    CommandUtil.printError(this.errStream, "tool jar not found", null, false);
                    CommandUtil.exitError(this.exitWhenFinish);
                    return;
                }
                updateBalToolsTomlFile(toolId, version, toolPathInCentralCache);
                errStream.println(toolIdAndVersion + " pulled successfully");
            } catch (PackageAlreadyExistsException e) {
                errStream.println(e.getMessage());
                CommandUtil.exitError(this.exitWhenFinish);
            } catch (CentralClientException e) {
                errStream.println("unexpected error occurred while pulling tool:" + e.getMessage());
                CommandUtil.exitError(this.exitWhenFinish);
            }
        }
    }

    private void handleUpdateCommand() {
        // Change this when update command is supported.
        CommandUtil.printError(this.errStream, "invalid sub-command given", "bal tool <sub-command> [args]",
                false);
        CommandUtil.exitError(this.exitWhenFinish);
    }

    private void handleListCommand() {
        if (argList.size() > 1) {
            CommandUtil.printError(
                    this.errStream, "too many arguments", "bal tool list", false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }
        List<BalToolsManifest.Tool> tools = listBalToolsTomlFile();
        if (tools.isEmpty()) {
            errStream.println("no tools found locally");
            return;
        }
        PrintUtils.printLocalTools(tools, RepoUtils.getTerminalWidth());
    }

    private void handleSearchCommand() {
        // TODO: make a central API call and return the results.
        //  bal tool search <search-term>
    }

    private void handleUninstallCommand() {
        if (argList.size() < 2) {
            CommandUtil.printError(this.errStream, "no tool id given",
                    "bal tool uninstall <tool-id>:[<version>]", false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }
        if (argList.size() > 2) {
            CommandUtil.printError(
                    this.errStream, "too many arguments", "bal tool uninstall <tool-id>:[<version>]", false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        String toolIdAndVersion = argList.get(1);
        String[] toolInfo = toolIdAndVersion.split(":");
        if (toolInfo.length == 2) {
            toolId = toolInfo[0];
            version = toolInfo[1];
        } else if (toolInfo.length == 1) {
            toolId = toolIdAndVersion;
            version = Names.EMPTY.getValue();
        } else {
            CommandUtil.printError(errStream, "invalid tool id",
                    "bal tool uninstall <tool-id>:[<version>]", false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        if (!validatePackageName(toolId)) {
            CommandUtil.printError(errStream, "invalid tool id",
                    "bal tool uninstall <tool-id>:[<version>]", false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        if (!Names.EMPTY.getValue().equals(version)) {
            try {
                SemanticVersion.from(version);
            } catch (ProjectException e) {
                CommandUtil.printError(errStream, "invalid tool version. " + e.getMessage(),
                        "bal tool pull <tool-id>[:<version>]", false);
                CommandUtil.exitError(this.exitWhenFinish);
                return;
            }
        }

        BalToolsToml balToolsToml = BalToolsToml.from(balToolsTomlPath);
        BalToolsManifest balToolsManifest = BalToolsManifestBuilder.from(balToolsToml).build();
        boolean uninstallSuccess;
        if (Names.EMPTY.getValue().equals(version)) {
            uninstallSuccess = uninstallAllToolVersions(balToolsManifest);
        } else {
            uninstallSuccess = uninstallSpecificToolVersion(balToolsManifest);
        }
        if (uninstallSuccess) {
            balToolsToml.modify(balToolsManifest);
            errStream.println(toolIdAndVersion + " uninstalled successfully");
        }
    }

//    private boolean pullToolFromCentral(String supportedPlatform, Path packagePathInBalaCache)
//            throws CentralClientException {
//        Settings settings;
//        try {
//            settings = RepoUtils.readSettings();
//            // Ignore Settings.toml diagnostics in the pull command
//        } catch (SettingsTomlException e) {
//            // Ignore 'Settings.toml' parsing errors and return empty Settings object
//            settings = Settings.from();
//        }
//        CentralAPIClient client = new CentralAPIClient(RepoUtils.getRemoteRepoURL(),
//                initializeProxy(settings.getProxy()),
//                getAccessTokenOfCLI(settings));
//        // TODO: replace with the correct API call once we have an API for tool pulling.
//        client.pullPackage(TOOL_ORG_NAME, toolId, version, packagePathInBalaCache, supportedPlatform,
//                RepoUtils.getBallerinaVersion(), false);
//        if (version.equals(Names.EMPTY.getValue())) {
//            List<String> versions = client.getPackageVersions(TOOL_ORG_NAME, toolId, supportedPlatform,
//                    RepoUtils.getBallerinaVersion());
//            version = CommandUtil.getLatestVersion(versions);
//        }
//        return CommandUtil.pullDependencyPackages(TOOL_ORG_NAME, toolId, version);
//    }

    // TODO: remove once the pull tool API is available.
    private boolean mockPullToolFromCentral(String supportedPlatform, Path packagePathInBalaCache)
            throws CentralClientException {
        if (version.equals(Names.EMPTY.getValue())) {
            version = "1.0.0";
        }
        // do nothing
        return false;
    }

    private String getToolPathInCentralCache(String orgName, String toolName, String version) {
        Path versionPath = ProjectUtils.createAndGetHomeReposPath()
                .resolve(ProjectConstants.REPOSITORIES_DIR).resolve(ProjectConstants.CENTRAL_REPOSITORY_CACHE_NAME)
                .resolve(ProjectConstants.BALA_DIR_NAME).resolve(orgName).resolve(toolName).resolve(version);
        File anyPlatformDir = versionPath.resolve(ANY_PLATFORM).toFile();
        File java11PlatformDir = versionPath.resolve(JvmTarget.JAVA_11.code()).toFile();

        if (anyPlatformDir.exists() && anyPlatformDir.isDirectory()) {
            return anyPlatformDir.toString();
        }
        if (java11PlatformDir.exists() && java11PlatformDir.isDirectory()) {
            return java11PlatformDir.toString();
        }
        return null;
    }

    private void updateBalToolsTomlFile(String toolId, String version, String toolPathInCentralCache) {
        BalToolsToml balToolsToml = BalToolsToml.from(balToolsTomlPath);
        BalToolsManifest balToolsManifest = BalToolsManifestBuilder.from(balToolsToml)
                .addTool(toolId, version, toolPathInCentralCache)
                .build();
        balToolsToml.modify(balToolsManifest);
    }

    private List<BalToolsManifest.Tool> listBalToolsTomlFile() {
        BalToolsToml balToolsToml = BalToolsToml.from(balToolsTomlPath);
        BalToolsManifest balToolsManifest = BalToolsManifestBuilder.from(balToolsToml).build();
        return new ArrayList<>(balToolsManifest.tools().values());
    }

    private boolean isToolLocallyAvailable(String toolIdAndVersion) {
        BalToolsToml balToolsToml = BalToolsToml.from(balToolsTomlPath);
        BalToolsManifest balToolsManifest = BalToolsManifestBuilder.from(balToolsToml).build();
        return balToolsManifest.tools().containsKey(toolIdAndVersion);
    }

    private boolean uninstallAllToolVersions(BalToolsManifest balToolsManifest) {
        boolean foundTools = false;

        Iterator<BalToolsManifest.Tool> iter = balToolsManifest.tools().values().iterator();
        while (iter.hasNext()) {
            BalToolsManifest.Tool tool = iter.next();
            if (tool.id().equals(toolId)) {
                String mapId = tool.id() + ":" + tool.version();
                boolean isDeleted = deletePackageCentralCache(tool.path());
                if (!isDeleted) {
                    CommandUtil.printError(
                            errStream, "failed to delete the tool jar for the tool " + mapId, null, false);
                    CommandUtil.exitError(this.exitWhenFinish);
                    return false;
                }
                iter.remove();
                foundTools = true;
            }
        }
        if (!foundTools) {
            CommandUtil.printError(errStream, "tool " + toolId + " does not exist locally", null, false);
            CommandUtil.exitError(this.exitWhenFinish);
            return false;
        }
        return true;
    }

    private boolean uninstallSpecificToolVersion(BalToolsManifest balToolsManifest) {
        String mapId = toolId + ":" + version;
        // if version is specified remove only the given version.
        if (balToolsManifest.tools().containsKey(mapId)) {
            boolean isDeleted = deletePackageCentralCache(balToolsManifest.tools().get(mapId).path());
            if (!isDeleted) {
                CommandUtil.printError(errStream, "failed to delete the tool jar for the tool " + mapId,
                        null, false);
                CommandUtil.exitError(this.exitWhenFinish);
                return false;
            }
            balToolsManifest.removeTool(mapId);
        } else {
            CommandUtil.printError(errStream, "tool " + mapId + " does not exist locally", null, false);
            CommandUtil.exitError(this.exitWhenFinish);
            return false;
        }
        return true;
    }

    private boolean deletePackageCentralCache(String platformPath) {
        Optional<Path> versionDir = Optional.ofNullable(Path.of(platformPath).getParent());
        return versionDir.filter(ProjectUtils::deleteDirectory).isPresent();
    }
}
