package com.samsungsds.analyst.code.checkstyle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class CheckStyleResultHandler extends DefaultHandler {
    private static final Logger LOGGER = LogManager.getLogger(CheckStyleResultHandler.class);

    private List<CheckStyleResult> list;
    private String instanceKey;

    private String filename;

    public CheckStyleResultHandler(List<CheckStyleResult> list, String instanceKey) {
        this.list = list;
        this.instanceKey = instanceKey;
    }

    @Override
    public void startDocument() throws SAXException {
        LOGGER.info("Start CheckStyle Result XML parsing");
    }

    @Override
    public void endDocument() throws SAXException {
        LOGGER.info("End CheckStyle Result XML parsing. Count: {}", list.size());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("file")) {
            filename = attributes.getValue("name");
        } else if (qName.equalsIgnoreCase("error")) {
            String line = attributes.getValue("line");
            String severity = attributes.getValue("severity");
            String message = attributes.getValue("message");
            String checker = attributes.getValue("source");

            CheckStyleResult result = new CheckStyleResult(filename, line, severity, message, checker, instanceKey);

            list.add(result);
        }
    }
}
