package com.samsungsds.analyst.code.checkstyle;

import com.puppycrawl.tools.checkstyle.Main;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.util.FindFileUtils;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import com.samsungsds.analyst.code.util.SystemExitDisabler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CheckStyleAnalysisLauncher implements CheckStyleAnalysis {
    private static final Logger LOGGER = LogManager.getLogger(CheckStyleAnalysisLauncher.class);

    private static final String CHECKSTYLE_RULESET_FILE = "/statics/checkstyle-java-checks-200903.xml";

    private List<String> arg = new ArrayList<>();
    private String projectBaseDir = null;
    private String sourceDirectories = null;
    private File reportFile = null;

    @Override
    public void setProjectBaseDir(String directory) {
        this.projectBaseDir = directory;
    }

    @Override
    public void setSourceDirectories(String directories) {
        this.sourceDirectories = directories;
    }

    @Override
    public void addOption(String option, String value) {
        arg.add(option);

        if (value != null && !value.equals("")) {
            arg.add(value);
        }
    }

    @Override
    public void run(String instanceKey) {
        // format : xml
        addOption("-f", "xml");

        if (!arg.contains("-c")) {
            addOption("-c", IOAndFileUtils.saveResourceFile(CHECKSTYLE_RULESET_FILE, "checkstyle", ".xml").toString());
        }

        try {
            reportFile = File.createTempFile("checkstyle", ".xml");
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        reportFile.deleteOnExit();

        addOption("-o", reportFile.toString());

        LOGGER.debug("CheckStyle Result File : {}", reportFile.toString());

        String[] sources = FindFileUtils.getFullDirectories(projectBaseDir, sourceDirectories);

        for (String src : sources) {
            List<String> arguments = new ArrayList<>(arg);
            arguments.add(src);

            LOGGER.debug("Forbid System Exit Call");
            SystemExitDisabler.forbidSystemExitCall();
            try {
                Main.main(arguments.toArray(new String[0]));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (SystemExitDisabler.ExitTrappedException ex) {
                LOGGER.info("System Exit Called... (forbid that call)");
                throw new RuntimeException("System Exit Called when CheckStyle running");
            } finally {
                LOGGER.debug("Enable System Exit Call");
                SystemExitDisabler.enableSystemExitCall();
            }

            List<CheckStyleResult> resultList = parseXML(reportFile, instanceKey);

            MeasuredResult.getInstance(instanceKey).putCheckStyleList(resultList);
        }
    }

    private List<CheckStyleResult> parseXML(File reportFile, String instanceKey) {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        List<CheckStyleResult> list = new ArrayList<>(100);

        CheckStyleResultHandler handler;

        try(InputStream stream = new FileInputStream(reportFile)) {
            saxParserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            SAXParser saxParser = saxParserFactory.newSAXParser();
            handler = new CheckStyleResultHandler(list, instanceKey);
            saxParser.parse(stream, handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error("XML Parser Exception", e);
            throw new IllegalArgumentException(e);
        }

        return list;
    }
}
