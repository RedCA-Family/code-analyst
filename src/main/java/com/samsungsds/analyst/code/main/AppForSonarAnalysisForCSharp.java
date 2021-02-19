/*
Copyright 2018 Samsung SDS

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.samsungsds.analyst.code.main;

import com.google.common.io.Files;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.gson.Gson;
import com.samsungsds.analyst.code.api.ProgressEvent;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sonar.api.utils.ZipUtils;
import org.sonarsource.scanner.api.ScannerProperties;
import org.sonarsource.scanner.api.internal.InternalProperties;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AppForSonarAnalysisForCSharp extends AppForSonarAnalysis {
    private static final Logger LOGGER = LogManager.getLogger(AppForSonarAnalysisForCSharp.class);

    private String scannerDirectory = null;

    private enum MSBuildEnv {
        DOT_NET_FRAMEWORK, DOT_NET_CORE
    }

    private enum MSBuildType {
        BEGIN, END
    }

    public AppForSonarAnalysisForCSharp(CliParser cli, ObserverManager observerManager) {
        super(cli, observerManager);
    }

    protected void runSonarAnalysis() {
        // check MSBuild for C#
        MSBuildEnv env = checkMSBuildEnv();

        // setup SonarScanner for MSBuild
        scannerDirectory = settingSonarScannerForMSBuildAndGetPath(env);

        // start server
        int port = startServerAndGetPort();

        // setup sonar scanner
        List<String> arguments = getSonarScannerParameter(port);

        // process sonar rule filter
        processSonarRuleFilter();

        // setting Sonar Process Event Checker
        SonarProgressEventChecker sonarProgressChecker = settingAndGetSonarProgressEventChecker();

        // start sonar scanner
        try {
            cleanWorkingDirectory(cli.getProjectBaseDir());
            runScannerForMSBuild(env, scannerDirectory, arguments, MSBuildType.BEGIN, cli.getProjectBaseDir());
            runMSBuild(env, cli.getProjectBaseDir());
            runScannerForMSBuild(env, scannerDirectory, arguments, MSBuildType.END, cli.getProjectBaseDir());
        } finally {
            LOGGER.info("Surrogate Sonar Server stopping...");
            server.stop();

            if (sonarProgressChecker != null) {
                sonarProgressChecker.stop();
            }

            observerManager.notifyObservers(ProgressEvent.SONAR_ALL_COMPLETE);
        }
    }

    private void cleanWorkingDirectory(String projectBaseDir) {
        IOAndFileUtils.deleteDirectory(new File(projectBaseDir, ".sonarqube"));
    }

    private void runScannerForMSBuild(MSBuildEnv env, String scannerDirectory, List<String> arguments, MSBuildType type, String workingDirectory) {
        ProcessBuilder builder;

        List<String> command = new ArrayList<>();
        if (env == MSBuildEnv.DOT_NET_FRAMEWORK) {
            command.add(scannerDirectory + "SonarScanner.MSBuild.exe");
        } else {
            command.add("dotnet");
            command.add(scannerDirectory + "SonarScanner.MSBuild.dll");
        }

        if (type == MSBuildType.BEGIN) {
            command.add("begin");
            command.addAll(arguments);
        } else {    // end
            command.add("end");
        }

        runProcessBuilder(command, workingDirectory);
    }

    private void runMSBuild(MSBuildEnv env, String projectBaseDir) {
        List<String> command = new ArrayList<>();
        if (env == MSBuildEnv.DOT_NET_FRAMEWORK) {
            command.add("MSBuild.exe");
            command.add(projectBaseDir);
            command.add("/t:Rebuild");
        } else {
            command.add("dotnet");
            command.add("build");
            command.add(projectBaseDir);
        }

        runProcessBuilder(command, projectBaseDir);
    }

    private void runProcessBuilder(List<String> command, String workingDirectory) {
        ProcessBuilder builder;
        builder = new ProcessBuilder(command);
        builder.directory(new File(workingDirectory));
        builder.environment().put("DOTNET_CLI_UI_LANGUAGE", "en");

        StringBuilder buffer = new StringBuilder();
        for (String s : command) {
            buffer.append(s).append(" ");
        }
        LOGGER.debug("Process : {}", buffer.toString());
        LOGGER.info("Process start...");

        if (!System.getProperty("os.name").startsWith("Windows")) {
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        }
        try {
            Process proc = builder.start();

            if (System.getProperty("os.name").startsWith("Windows")) {
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream(), "euc-kr"));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream(), "euc-kr"));
                String line;
                while ((line = stdInput.readLine()) != null) {
                    System.out.println(line);
                }
                while ((line = stdError.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int errCode = proc.waitFor();
            if (errCode != 0) {
                System.out.println("exitCode = " + errCode);
                throw new RuntimeException("Sonar Scanner for MSBuild Error");
            }
            LOGGER.info("Process end...");
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (InterruptedException ex) {
            throw new UncheckedExecutionException(ex);
        }
    }

    private void addParameter(List<String> list, String key, String value) {
        list.add(String.format("/d:%s=%s", key, value));
    }

    private List<String> getSonarScannerParameter(int port) {
        List<String> arguments = new ArrayList<>();

        arguments.add(String.format("/k:%s", "local:" + cli.getInstanceKey()));

        if (cli.isDebug()) {
            addParameter(arguments, SONAR_VERBOSE, "true");
        }

        // The property 'sonar.working.directory' is automatically set by the SonarScanner for MSBuild and cannot be overridden on the command line.
        //addParameter(arguments, ScannerProperties.WORK_DIR, SONAR_TEMP);

        addParameter(arguments, ScannerProperties.HOST_URL, "http://127.0.0.1:" + port);

        addParameter(arguments, InternalProperties.SCANNER_APP, "SonarQubeScanner");

        Gson gson = new Gson();
        String json = gson.toJson(cli.getIndividualMode());
        addParameter(arguments, InternalProperties.SCANNER_APP_VERSION, "mode=" + json);

        if (!cli.getIncludes().equals("")) {
            addParameter(arguments, "sonar.inclusions", MeasuredResult.getInstance(cli.getInstanceKey()).getIncludes());
        }

        if (!cli.getExcludes().equals("")) {
            addParameter(arguments, "sonar.exclusions", MeasuredResult.getInstance(cli.getInstanceKey()).getExcludes());
        }

        if (!cli.getIndividualMode().isDuplication() || cli.isTokenBased()) {
            LOGGER.info("CPD Exclusions All files");
            addParameter(arguments, "sonar.cpd.exclusions", "**/*");
        }

        return arguments;
    }

    private String settingSonarScannerForMSBuildAndGetPath(MSBuildEnv env) {
        String targetFile = null;

        if (env == MSBuildEnv.DOT_NET_FRAMEWORK) {
            targetFile = "/statics/sonar-scanner-msbuild/sonar-scanner-msbuild-4.10.0.19059-net46.zip";
        } else {    // DOT_NET_CORE
            targetFile = "/statics/sonar-scanner-msbuild/sonar-scanner-msbuild-4.10.0.19059-netcoreapp2.0.zip";
        }

        File zipFile = IOAndFileUtils.saveResourceFile(targetFile,"scanner-msbuild", ".zip");

        File dir = Files.createTempDir();
        try {
            ZipUtils.unzip(zipFile, dir);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        return dir + File.separator;
    }

    private MSBuildEnv checkMSBuildEnv() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("MSBuild.exe");
            return MSBuildEnv.DOT_NET_FRAMEWORK;
        } catch (IOException ignore) {
            // MSBuild.exe not found...
        }

        try {
            Process process = runtime.exec("dotnet");
            return MSBuildEnv.DOT_NET_CORE;
        } catch (IOException ignore) {
            // dotnet not found...
            throw new RuntimeException("MSBuild for .NET Framework or .NET Core not found");
        }
    }

    public void doing() {
        super.doing();

        if (scannerDirectory != null) {
            IOAndFileUtils.deleteDirectory(new File(scannerDirectory));
        }
    }
}
