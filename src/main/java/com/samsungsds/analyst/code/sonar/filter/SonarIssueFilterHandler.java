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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Set;

public class SonarIssueFilterHandler extends DefaultHandler {
    private static final Logger LOGGER = LogManager.getLogger(SonarIssueFilterHandler.class);

    private static final String EXCLUDE_ELEMENT = "Exclude";
    private static final String EXCLUDE_KEY_ATTRIBUTE = "key";

    private Set<String> filters;

    private int excludedRules = 0;
    private int excludedJavaRules = 0;
    private int excludedJSRules = 0;
    private int excludedCSharpRules = 0;
    private int excludedPythonRules = 0;

    public SonarIssueFilterHandler(Set<String> filters) {
        this.filters = filters;
    }

    @Override
    public void startDocument() throws SAXException {
        LOGGER.info("Start Sonar Filter XML parsing");
    }

    @Override
    public void endDocument() throws SAXException {
        LOGGER.info("End Sonar Filter XML parsing. Count: {}", filters.size());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase(EXCLUDE_ELEMENT)) {
            String keyValue = attributes.getValue(EXCLUDE_KEY_ATTRIBUTE);

            LOGGER.info("Exclude Key : {}", keyValue);
            filters.add(keyValue);
            excludedRules++;

            if (keyValue.startsWith("squid:") || keyValue.startsWith("common-java:")) {
                excludedJavaRules++;
            } else if (keyValue.startsWith("javascript:") || keyValue.startsWith("common-js:")) {
                excludedJSRules++;
            } else if (keyValue.startsWith("csharpsquid:")) {
                excludedCSharpRules++;
            } else if (keyValue.startsWith("python:")) {
                excludedPythonRules++;
            }
        }
    }

    public int getExcludedRules() {
        return excludedRules;
    }

    public int getExcludedJavaRules() {
        return excludedJavaRules;
    }

    public int getExcludedJSRules() {
        return excludedJSRules;
    }

    public int getExcludedCSharpRules() {
        return excludedCSharpRules;
    }

    public int getExcludedPythonRules() {
        return excludedPythonRules;
    }
}
