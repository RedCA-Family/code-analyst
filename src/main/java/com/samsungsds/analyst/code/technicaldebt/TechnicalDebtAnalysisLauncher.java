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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

public class TechnicalDebtAnalysisLauncher implements TechnicalDebtAnalysis {

	private static final Logger LOGGER = LogManager.getLogger(TechnicalDebtAnalysisLauncher.class);

	private static final double COST_TO_FIX_ONE_BLOCK = 2;
	private static final double COST_TO_FIX_ONE_VIOLATION = 0.37;
	private static final double COST_TO_FIX_ONE_VULNERABILITY_ISSUE = 0.62;
	private static final double COST_TO_SPLIT_A_METHOD = 1;
	private static final double COST_TO_CUT_AN_EDGE_BETWEEN_TWO_FILES = 4;

	private static final String PMD_EFFORT_XML_FILE = "/statics/PmdEffort.xml";
	private static final String FINDBUGS_EFFORT_XML_FILE = "/statics/FindBugsEffort.xml";

	private static Map<String, Double> pmdEffortMap = new HashMap<>();
	private static Map<String, Double> findBugsEffortMap = new HashMap<>();

	private final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	private MeasuredResult measuredResult;
	private double duplicationDebt;
	private double violationDebt;
	private double complexityDebt;
	private double acyclicDependencyDebt;

	@Override
	public void run(String instanceKey) {
		measuredResult = MeasuredResult.getInstance(instanceKey);
		calculateTechnicalDebt();
		measuredResult.setTechnicalDebtResult(new TechnicalDebtResult(duplicationDebt, violationDebt, complexityDebt, acyclicDependencyDebt));
	}

	private void calculateTechnicalDebt() {
		if (measuredResult.getIndividualMode().isDuplication()) {
			calculateDuplicationDebt();
		}
		if (measuredResult.getIndividualMode().isSonarJava() || measuredResult.getIndividualMode().isPmd() ||
				measuredResult.getIndividualMode().isFindBugs() || measuredResult.getIndividualMode().isFindSecBugs() ||
				measuredResult.getIndividualMode().isJavascript() || measuredResult.getIndividualMode().isWebResources() ||
                measuredResult.getIndividualMode().isSonarCSharp() || measuredResult.getIndividualMode().isSonarPython()) {
			calculateViolationDebt();
		}
		if (measuredResult.getIndividualMode().isComplexity()) {
			calculateComplexityDebt();
		}
		if (measuredResult.getIndividualMode().isDependency()) {
			calculateAcyclicDependencyDebt();
		}
	}

	private void calculateDuplicationDebt() {
		duplicationDebt = measuredResult.getDuplicatedBlocks() * COST_TO_FIX_ONE_BLOCK;
		LOGGER.info("Calculated Duplication Debt: " + duplicationDebt);
	}

	private void calculateViolationDebt() {
		effortXMLParse();
		violationDebt += calculateSonarJavaDebt();
		violationDebt += calculatePmdDebt();
		violationDebt += calculateFindBugsDebt();
		violationDebt += calculateFindSecBugsDebt();
		violationDebt += calculateWebResourceDebt();
		LOGGER.info("Calculated Violation Debt: " + violationDebt);
	}

	private void effortXMLParse() {
		DebtXMLParserHandler debtXMLParserHandler;
		try {
			saxParserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			debtXMLParserHandler = new DebtXMLParserHandler(pmdEffortMap);
			saxParser.parse(IOAndFileUtils.saveResourceFile(PMD_EFFORT_XML_FILE, "pmd_effort", ".xml"), debtXMLParserHandler);
			debtXMLParserHandler = new DebtXMLParserHandler(findBugsEffortMap);
			saxParser.parse(IOAndFileUtils.saveResourceFile(FINDBUGS_EFFORT_XML_FILE, "findbugs_effort", ".xml"), debtXMLParserHandler);
		} catch (ParserConfigurationException e) {
			LOGGER.info("Parser Configuration Exception", e);
		} catch (SAXException e) {
			LOGGER.info("SAX Exception", e);
		} catch (IOException e) {
			LOGGER.info("IO Exception", e);
		}
	}

	private double calculateSonarJavaDebt() {
		return measuredResult.getSonarIssueCountAll() * COST_TO_FIX_ONE_VIOLATION;
	}

	private double calculatePmdDebt() {
		double result = 0;
		for (PmdResult pmdResult : measuredResult.getPmdList()) {
			result += pmdEffortMap.get(pmdResult.getRule());
		}
		return result;
	}

	private double calculateFindBugsDebt() {
		double result = 0;
		for (FindBugsResult findBugsResult : measuredResult.getFindBugsList()) {
			Double debt = findBugsEffortMap.get(findBugsResult.getPatternKey());
			if (debt == null) {
				debt = COST_TO_FIX_ONE_VIOLATION;
			}

			result += debt;
		}
		return result;
	}

	private double calculateFindSecBugsDebt() {
		return measuredResult.getFindSecBugsCountAll() * COST_TO_FIX_ONE_VULNERABILITY_ISSUE;
	}

	private double calculateWebResourceDebt() {
		return measuredResult.getWebResourceCountAll() * COST_TO_FIX_ONE_VIOLATION;
	}

	private void calculateComplexityDebt() {
		complexityDebt = measuredResult.getComplexityOver20() * COST_TO_SPLIT_A_METHOD;
		LOGGER.info("Calculated Complexity Debt: " + complexityDebt);
	}

	private void calculateAcyclicDependencyDebt() {
		acyclicDependencyDebt = measuredResult.getAcyclicDependencyCount() * COST_TO_CUT_AN_EDGE_BETWEEN_TWO_FILES;
		LOGGER.info("Calculated AcyclicDependency Debt: " + acyclicDependencyDebt);
	}

}
