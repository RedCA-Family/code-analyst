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
package com.samsungsds.analyst.code.technicaldebt;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DebtXMLParserHandler extends DefaultHandler {

	private static final Logger LOGGER = LogManager.getLogger(DebtXMLParserHandler.class);

	private static final String RULE_KEY = "rule-key";
	private static final String KEY = "key";
	private static final String VAL = "val";
	private static final String TXT = "txt";
	private static final String REMEDIATION_FACTOR = "remediationFactor";
	private static final String OFFSET = "offset";

	private final Map<String, Double> effortMap;
	private String elementName;
	private String rulesetName;
	private double cost;
	private boolean isOffset;

	public DebtXMLParserHandler(Map<String, Double> effortMap) {
		this.effortMap = effortMap;
	}

	@Override
	public void startDocument() throws SAXException {
		LOGGER.info("Start XML parsing");
	}

	@Override
	public void endDocument() throws SAXException {
		LOGGER.info("End XML parsing. TotalCount: " + effortMap.size());
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		elementName = qName;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String value = new String(ch, start, length);
		if (!StringUtils.isBlank(value)) {
			if (RULE_KEY.equals(elementName)) {
				rulesetName = value;
			} else if (KEY.equals(elementName) && (OFFSET.equals(value) || REMEDIATION_FACTOR.equals(value))) {
				isOffset = true;
			} else if (isOffset && VAL.equals(elementName)) {
				cost = Double.parseDouble(value);
			} else if (isOffset && TXT.equals(elementName)) {
				if (!"h".equals(value)) {
					cost /= 60;
				}
				effortMap.put(rulesetName, Double.valueOf(cost));
				LOGGER.debug("Loaded(ruleset: " + rulesetName + ", cost: " + cost + ")");
				isOffset = false;
			}
		}
	}

}
