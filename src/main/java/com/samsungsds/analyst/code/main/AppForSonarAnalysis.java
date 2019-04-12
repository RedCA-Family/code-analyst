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

import com.google.gson.Gson;
import com.samsungsds.analyst.code.api.ProgressEvent;
import com.samsungsds.analyst.code.main.delay.DelayWork;
import com.samsungsds.analyst.code.main.nodejs.NodeRuntime;
import com.samsungsds.analyst.code.main.nodejs.NodeRuntimeException;
import com.samsungsds.analyst.code.sonar.SonarAnalysis;
import com.samsungsds.analyst.code.sonar.SonarAnalysisLauncher;
import com.samsungsds.analyst.code.sonar.filter.SonarIssueFilter;
import com.samsungsds.analyst.code.sonar.server.JettySurrogateSonarServer;
import com.samsungsds.analyst.code.sonar.server.SurrogateSonarServer;
import com.samsungsds.analyst.code.util.FindFileUtils;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonarsource.scanner.api.ScanProperties;
import org.sonarsource.scanner.api.ScannerProperties;
import org.sonarsource.scanner.api.internal.InternalProperties;

import java.io.File;
import java.nio.file.Paths;

public class AppForSonarAnalysis implements DelayWork {
    private static final Logger LOGGER = LogManager.getLogger(AppForSonarAnalysis.class);

    private static final String SONAR_VERBOSE = "sonar.verbose";
    private static final String SONAR_TEMP = ".ca";

    private CliParser cli;
    private ObserverManager observerManager;

    public AppForSonarAnalysis(CliParser cli, ObserverManager observerManager) {
        this.cli = cli;
        this.observerManager = observerManager;
    }

    void runSonarAnalysis() {
        LOGGER.info("Surrogate Sonar Server starting...");
        SurrogateSonarServer server = new JettySurrogateSonarServer();
        int port = server.startAndReturnPort(cli);

        observerManager.notifyObservers(ProgressEvent.SONAR_START_COMPLETE);

        LOGGER.info("Sonar Scanner starting...");
        String src = cli.getSrc();
        if (cli.getIndividualMode().isWebResources()) {
            if (src.equals("")) {
                src = cli.getWebapp();
            } else if (!"".equals(cli.getWebapp())) {
                src += "," + cli.getWebapp();
            }
        }

        SonarAnalysis sonar = new SonarAnalysisLauncher(MeasuredResult.getInstance(cli.getInstanceKey()).getProjectDirectory(), src);

        if (cli.isDebug()) {
            sonar.addProperty(SONAR_VERBOSE, "true");
        }

        sonar.addProperty(ScannerProperties.WORK_DIR, SONAR_TEMP);

        sonar.addProperty(ScannerProperties.HOST_URL, "http://127.0.0.1:" + port);

        sonar.addProperty(InternalProperties.SCANNER_APP, "SonarQubeScanner");

        Gson gson = new Gson();
        String json = gson.toJson(cli.getIndividualMode());
        sonar.addProperty(InternalProperties.SCANNER_APP_VERSION, "mode=" + json);

        sonar.addProperty(ScanProperties.PROJECT_SOURCE_ENCODING, cli.getEncoding());

        // sonar.addProperty(CoreProperties.ANALYSIS_MODE, CoreProperties.ANALYSIS_MODE_PREVIEW);
        sonar.addProperty(CoreProperties.ANALYSIS_MODE, CoreProperties.ANALYSIS_MODE_PUBLISH);

        sonar.addProperty(CoreProperties.PROJECT_KEY_PROPERTY, "local");

        sonar.addProperty("sonar.projectBaseDir", cli.getProjectBaseDir());

        if (cli.getLanguageType() == App.Language.JAVA) {
            sonar.addProperty("sonar.java.binaries", cli.getBinary());
            sonar.addProperty(ProjectDefinition.SOURCES_PROPERTY, src);
            sonar.addProperty("sonar.java.source", cli.getJavaVersion());
        } else if (cli.getLanguageType() == App.Language.JAVASCRIPT) {
            sonar.addProperty("sonar.language", "js");

            sonar.addProperty(ProjectDefinition.SOURCES_PROPERTY, src);
        }

        // BatchWSClient timeout
        sonar.addProperty("sonar.ws.timeout", cli.getTimeout());

        if (cli.getLanguageType() == App.Language.JAVA && !cli.getLibrary().equals("")) {
            sonar.addProperty("sonar.java.libraries", cli.getLibrary());
        }

        sonar.addProperty("sonar.scanAllFiles", "true");

        if (!cli.getIncludes().equals("")) {
            sonar.addProperty("sonar.inclusions", MeasuredResult.getInstance(cli.getInstanceKey()).getIncludes());
        }

        if (!cli.getExcludes().equals("")) {
            sonar.addProperty("sonar.exclusions", MeasuredResult.getInstance(cli.getInstanceKey()).getExcludes());
        }

        if (!cli.getIndividualMode().isDuplication()) {
            LOGGER.info("CPD Exclusions All files");
            sonar.addProperty("sonar.cpd.exclusions", "**/*");
        }

        // process sonar rule filter
        if (cli.getRuleSetFileForSonar() != null && !cli.getRuleSetFileForSonar().equals("")) {
            SonarIssueFilter filter = new SonarIssueFilter();

            MeasuredResult.getInstance(cli.getInstanceKey()).setSonarIssueFilterSet(filter.parse(cli.getRuleSetFileForSonar()));

            if (cli.getIndividualMode().isSonarJava()) {
                MeasuredResult.getInstance(cli.getInstanceKey()).setSonarJavaRules(Version.SONAR_JAVA_DEFAULT_RULES - filter.getExcludedJavaRules());
            }
            if (cli.getIndividualMode().isJavascript()) {
                MeasuredResult.getInstance(cli.getInstanceKey()).setSonarJSRules(Version.SONAR_JS_DEFAULT_RULES - filter.getExcludedJSRules());
            }
        } else {
            if (cli.getIndividualMode().isSonarJava()) {
                MeasuredResult.getInstance(cli.getInstanceKey()).setSonarJavaRules(Version.SONAR_JAVA_DEFAULT_RULES);
            }
            if (cli.getIndividualMode().isJavascript()) {
                MeasuredResult.getInstance(cli.getInstanceKey()).setSonarJSRules(Version.SONAR_JS_DEFAULT_RULES);
            }
        }

        // Node JS runtime
        if (cli.getIndividualMode().isJavascript()) {
            try {
                sonar.addProperty("sonar.nodejs.executable", NodeRuntime.findNodeRuntimePath(cli.getInstanceKey()));
            } catch (NodeRuntimeException ex) {
                LOGGER.error(ex);
            }
        }

        SonarProgressEventChecker sonarProgressChecker = null;
        if (observerManager.hasProgressMonitor()) {
            int fileCount = 0;

            if (!cli.getSrc().equals("")) {
                String[] srcDirectories = cli.getSrc().split(FindFileUtils.COMMA_SPLITTER);
                for (String srcDir : srcDirectories) {
                    fileCount += IOAndFileUtils.getJavaFileCount(Paths.get(cli.getProjectBaseDir(), srcDir));
                }
            }
            if (cli.getIndividualMode().isJavascript()) {
                fileCount += IOAndFileUtils.getFileCountWithExt(Paths.get(cli.getProjectBaseDir(), cli.getWebapp()), "js");
            }
            if (cli.getIndividualMode().isCss()) {
                fileCount += IOAndFileUtils.getFileCountWithExt(Paths.get(cli.getProjectBaseDir(), cli.getWebapp()), "css", "less", "scss");
            }
            if (cli.getIndividualMode().isHtml()) {
                fileCount += IOAndFileUtils.getFileCountWithExt(Paths.get(cli.getProjectBaseDir(), cli.getWebapp()), "htm", "html", "jsp");
            }
            LOGGER.info("Approximate number of files : {}", fileCount);
            sonarProgressChecker = new SonarProgressEventChecker(cli.getIndividualMode(), observerManager, fileCount);

            sonarProgressChecker.start();
        }

        try {
            sonar.run(cli.getInstanceKey());
        } finally {
            LOGGER.info("Surrogate Sonar Server stopping...");
            server.stop();

            if (sonarProgressChecker != null) {
                sonarProgressChecker.stop();
            }

            observerManager.notifyObservers(ProgressEvent.SONAR_ALL_COMPLETE);
        }
    }

    public void doing() {
        IOAndFileUtils.deleteDirectory(new File(SONAR_TEMP));
    }
}
