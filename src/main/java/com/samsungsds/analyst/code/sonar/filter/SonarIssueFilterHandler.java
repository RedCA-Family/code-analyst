package com.samsungsds.analyst.code.sonar.filter;

import org.apache.commons.lang3.StringUtils;
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
        }
    }

    public int getExcludedRules() {
        return excludedRules;
    }
}
