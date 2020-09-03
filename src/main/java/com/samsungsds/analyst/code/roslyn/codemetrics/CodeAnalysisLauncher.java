package com.samsungsds.analyst.code.roslyn.codemetrics;

import com.google.common.io.Files;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.pmd.ComplexityAnalysis;
import com.samsungsds.analyst.code.roslyn.codemetrics.result.ComplexityAction;
import com.samsungsds.analyst.code.roslyn.codemetrics.result.RoslynCodeMetricsHandler;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sonar.api.utils.ZipUtils;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public class CodeAnalysisLauncher implements ComplexityAnalysis {
    private static final Logger LOGGER = LogManager.getLogger(CodeAnalysisLauncher.class);

    private static final String MS_CODE_ANALYSIS_METRICS = "/statics/MS.CodeAnalysis.Metrics-3.3.0.zip";
    private static final String EXECUTABLE_PROGRAM = "Metrics.exe";

    private final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();;
    private String solutionDirectory = null;

    @Override
    public void addOption(String option, String value) {
        if (option.equals("-dir")) {
            solutionDirectory = value;
        }
    }

    @Override
    public void run(String instanceKey) {
        checkPlatform();

        String executable = installCodeAnalysisAndGetPath();
        String solutionFilePath = getSolutionFilePath();
        File result = getResultFile();

        executeRoslynCodeAnalysis(executable, solutionFilePath, result);

        parseResult(instanceKey, result);
    }

    private void parseResult(String instanceKey, File result) {
        ComplexityAction complexityAction = new ComplexityAction();

        RoslynCodeMetricsHandler resultHandler;
        try {
            saxParserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            SAXParser saxParser = saxParserFactory.newSAXParser();
            resultHandler = new RoslynCodeMetricsHandler(solutionDirectory);

            // Add Actions
            resultHandler.addAdditionAction(complexityAction);

            saxParser.parse(result, resultHandler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error("XML Parser Exception", e);
            throw new IllegalArgumentException(e);
        }

        // List<MetricsResult> list = resultHandler.getResultList();
        MeasuredResult.getInstance(instanceKey).putComplexityList(complexityAction.getList());
    }

    private void executeRoslynCodeAnalysis(String executable, String solutionFilePath, File result) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(executable, "/solution:" + solutionFilePath, "/out:" + result.getPath());

        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            Process proc = builder.start();
            int errCode = proc.waitFor();
            if (errCode != 0) {
                throw new RuntimeException("Roslyn Code Analysis Error");
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (InterruptedException ex) {
            throw new UncheckedExecutionException(ex);
        }
    }

    private String getSolutionFilePath() {
        if (solutionDirectory == null) {
            throw new IllegalStateException("Solution Directory option not found");
        }

        return solutionDirectory + File.separator + findSolutionFile(solutionDirectory);
    }

    private File getResultFile() {
        File result = null;
        try {
            result = File.createTempFile("roslyn", ".xml");
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        result.deleteOnExit();

        return result;
    }

    private void checkPlatform() {
        if (!SystemUtils.IS_OS_WINDOWS) {
            throw new IllegalStateException("Complexity measurement for C# only supported on Windows Platform");
        }
    }

    private String installCodeAnalysisAndGetPath() {
        try {
            File zipFile = IOAndFileUtils.saveResourceFile(MS_CODE_ANALYSIS_METRICS, "roslyn", ".zip");

            File dir = Files.createTempDir();
            ZipUtils.unzip(zipFile, dir);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> FileUtils.deleteQuietly(dir)));

            return dir + File.separator + EXECUTABLE_PROGRAM;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private String findSolutionFile(String directory) {
        File dir = new File(directory);

        File[] list = dir.listFiles((sub, name) -> {
            return name.endsWith(".sln");
        });

        if (list.length == 0) {
            throw new IllegalStateException("Solution(*.sln) not found in " + directory);
        }

        return list[0].getName();
    }
}
