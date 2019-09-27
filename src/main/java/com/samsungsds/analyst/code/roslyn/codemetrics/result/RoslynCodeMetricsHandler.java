package com.samsungsds.analyst.code.roslyn.codemetrics.result;

import com.samsungsds.analyst.code.util.FindFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoslynCodeMetricsHandler extends DefaultHandler {
    private static final Logger LOGGER = LogManager.getLogger(RoslynCodeMetricsHandler.class);

    private final String solutionDirectory;
    private List<AdditionalAction> additionalActionList = new ArrayList<>();
    private List<MetricsResult> list = new ArrayList<>();

    public RoslynCodeMetricsHandler(String solutionDirectory) {
        this.solutionDirectory = solutionDirectory;
    }

    private enum MetricsTarget {
        Project, Namespace, Class, Field, Property, Method, NA
    }

    private MetricsTarget currentTarget = MetricsTarget.NA;
    private String projectName = "";
    private String namespaceName = "";
    private String className = "";
    private String fieldName = "";
    private String propertyName = "";
    private String methodName = "";
    private String file = "";
    private int line = 0;

    private int maintainabilityIndex = 0;
    private int cyclomaticComplexity = 0;
    private int classCoupling = 0;
    private int linesOfCode = 0;

    @Override
    public void startDocument() throws SAXException {
        LOGGER.info("Start Roslyn Code Metrics XML parsing");
    }

    @Override
    public void endDocument() throws SAXException {
        LOGGER.info("End Roslyn Code Metrics XML parsing. Count: {}", list.size());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("Metric")) {
            settingMetricValues(attributes);

            return;
        }

        if (qName.equals("Assembly")) {
            String name = attributes.getValue("Name");
            projectName = name.split(FindFileUtils.COMMA_SPLITTER)[0];

            currentTarget = MetricsTarget.Project;
        } else if (qName.equals("Namespace")) {
            namespaceName = attributes.getValue("Name");

            currentTarget = MetricsTarget.Namespace;
        } else if (qName.equals("NamedType")) {
            className = attributes.getValue("Name");

            currentTarget = MetricsTarget.Class;
        } else if (qName.equals("Field")) {
            fieldName = attributes.getValue("Name");
            file = attributes.getValue("File");
            line = Integer.parseInt(attributes.getValue("Line"));

            currentTarget = MetricsTarget.Field;
        } else if (qName.equals("Property")) {
            propertyName = attributes.getValue("Name");
            file = attributes.getValue("File");
            line = Integer.parseInt(attributes.getValue("Line"));

            currentTarget = MetricsTarget.Property;
        } else if (qName.equals("Method")) {
            methodName = attributes.getValue("Name");
            file = attributes.getValue("File");
            line = Integer.parseInt(attributes.getValue("Line"));

            currentTarget = MetricsTarget.Method;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("Metrics")) {

            String type = "";
            String name = "";
            if (currentTarget == MetricsTarget.Field) {
                type = "field";
                name = fieldName;
            } else if (currentTarget == MetricsTarget.Property) {
                type = "property";
                name = propertyName;
            } else if (currentTarget == MetricsTarget.Method) {
                type = "method";
                name = methodName;
            } else {
                return;
            }

            MetricsResult result = new MetricsResult(projectName, namespaceName, className, type, name,
                file.replace(solutionDirectory + File.separator, ""), line);
            result.setMetricsValues(maintainabilityIndex, cyclomaticComplexity, classCoupling, linesOfCode);

            list.add(result);

            if (!additionalActionList.isEmpty()) {
                for (AdditionalAction action : additionalActionList) {
                    action.doAction(result);
                }
            }

            maintainabilityIndex = 0;
            cyclomaticComplexity = 0;
            classCoupling = 0;
            linesOfCode = 0;

            return;
        }

        if (qName.equals("Assembly") || qName.equals("Namespace") || qName.equals("NamedType")) {
            currentTarget = MetricsTarget.NA;
        } else if (qName.equals("Field") || qName.equals("Property") || qName.equals("Method")) {
            file = "";
            line = 0;

            currentTarget = MetricsTarget.NA;
        }
    }

    private void settingMetricValues(Attributes attributes) {
        String name = attributes.getValue("Name");

        if (name.equals("MaintainabilityIndex")) {
            maintainabilityIndex = Integer.parseInt(attributes.getValue("Value"));
        } else if (name.equals("CyclomaticComplexity")) {
            cyclomaticComplexity = Integer.parseInt(attributes.getValue("Value"));
        } else if (name.equals("ClassCoupling")) {
            classCoupling = Integer.parseInt(attributes.getValue("Value"));
        } else if (name.equals("LinesOfCode")) {
            linesOfCode = Integer.parseInt(attributes.getValue("Value"));
        }
    }

    public void addAdditionAction(AdditionalAction action) {
        additionalActionList.add(action);
    }

    public void removeAdditionAction(AdditionalAction action) {
        additionalActionList.remove(action);
    }

    public List<MetricsResult> getResultList() {
        return list;
    }
}
