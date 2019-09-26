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
package com.samsungsds.analyst.code.sonar.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SonarIssueFilter {
    private static final Logger LOGGER = LogManager.getLogger(SonarIssueFilter.class);
    private final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();;

    private int excludedRules = 0;
    private int excludedJavaRules = 0;
    private int excludedJSRules = 0;
    private int excludeCSharpRules = 0;
    private int excludePythonRules = 0;

    public Set<String> parse(String ruleSetFileForSonar) {
        Set<String> filters = new HashSet<>();

        SonarIssueFilterHandler filterHandler;
        try {
            saxParserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            SAXParser saxParser = saxParserFactory.newSAXParser();
            filterHandler = new SonarIssueFilterHandler(filters);
            saxParser.parse(ruleSetFileForSonar, filterHandler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error("XML Parser Exception", e);
            throw new IllegalArgumentException(e);
        }

        excludedRules = filterHandler.getExcludedRules();
        excludedJavaRules = filterHandler.getExcludedJavaRules();
        excludedJSRules = filterHandler.getExcludedJSRules();
        excludeCSharpRules = filterHandler.getExcludedCSharpRules();
        excludePythonRules = filterHandler.getExcludedPythonRules();

        return filters;
    }

    /**
     * @deprecated use getExcludedJavaRules(), getExcludedJSRules(), getExcludedCSharpRules() or getExcludedPythonRules()
     */
    @Deprecated
    public int getExcludedRules() {
        return excludedRules;
    }

    public int getExcludedJavaRules() {
        return excludedJavaRules;
    }

    public int getExcludedJSRules() {
        return excludedJSRules;
    }

    public int getExcludeCSharpRules() {
        return excludeCSharpRules;
    }

    public int getExcludePythonRules() {
        return excludePythonRules;
    }
}
