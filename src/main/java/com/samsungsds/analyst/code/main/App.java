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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.samsungsds.analyst.code.ckmetrics.CkMetricsAnalysis;
import com.samsungsds.analyst.code.ckmetrics.CkMetricsAnalysisLauncher;
import com.samsungsds.analyst.code.main.delay.DelayWork;
import com.samsungsds.analyst.code.main.subject.TargetFile;
import com.samsungsds.analyst.code.main.subject.TargetManager;
import com.samsungsds.analyst.code.pmd.*;
import com.samsungsds.analyst.code.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.api.ProgressEvent;
import com.samsungsds.analyst.code.api.ProgressObserver;
import com.samsungsds.analyst.code.findbugs.FindBugsAnalysis;
import com.samsungsds.analyst.code.findbugs.FindBugsAnalysisLauncher;
import com.samsungsds.analyst.code.findbugs.FindSecBugsAnalysisLauncher;
import com.samsungsds.analyst.code.jdepend.JDependAnalysis;
import com.samsungsds.analyst.code.jdepend.JDependAnalysisLauncher;
import com.samsungsds.analyst.code.main.result.OutputFileFormat;
import com.samsungsds.analyst.code.technicaldebt.TechnicalDebtAnalysis;
import com.samsungsds.analyst.code.technicaldebt.TechnicalDebtAnalysisLauncher;
import com.samsungsds.analyst.code.unusedcode.UnusedCodeAnalysis;
import com.samsungsds.analyst.code.unusedcode.UnusedCodeAnalysisLauncher;

public class App {
	private static final Logger LOGGER = LogManager.getLogger(App.class);

	private ObserverManager observerManager = new ObserverManager();

	private List<DelayWork> delayWorkList = new ArrayList<>();

	private boolean parsingError = false;
	private String parsingErrorMessage = "";

	public void process(CliParser cli) {
		SystemInfo.print();

		if (cli.parse()) {

			if (cli.getMode() == MeasurementMode.DefaultMode) {
				LOGGER.info("Mode : {}", cli.getIndividualMode());
			}

			MeasuredResult.getInstance(cli.getInstanceKey()).initialize(cli.isDetailAnalysis(), cli.isSeperatedOutput(), cli.getIndividualMode());
			if (cli.isDetailAnalysis()) {
				LOGGER.info("Detail Analysis mode...");
			}

			if (cli.isSaveCatalog()) {
				MeasuredResult.getInstance(cli.getInstanceKey()).setSaveCatalog(true);
            } else {
				MeasuredResult.getInstance(cli.getInstanceKey()).setSaveCatalog(false);
			}

			if (!cli.getAnalysisMode().equals(Constants.DEFAULT_ANALYSIS_MODE)) {
				MeasuredResult.getInstance(cli.getInstanceKey()).setIndividualModeString(cli.getAnalysisMode());
			}

			MeasuredResult.getInstance(cli.getInstanceKey()).setIndividualMode(cli.getIndividualMode());

			File project = new File(cli.getProjectBaseDir());

			try {
				LOGGER.info("Project Directory : {}", project.getCanonicalPath());
				MeasuredResult.getInstance(cli.getInstanceKey()).setProjectDirectory(project.getCanonicalPath());
			} catch (IOException ex) {
				LOGGER.error("Project Directory Error : {}", cli.getProjectBaseDir());
				return;
			}

			MeasuredResult.getInstance(cli.getInstanceKey()).setProjectInfo(cli);

			MeasuredResult.getInstance(cli.getInstanceKey()).setMode(cli.getMode());

			MeasuredResult.getInstance(cli.getInstanceKey()).setTokenBased(cli.isTokenBased());
			MeasuredResult.getInstance(cli.getInstanceKey()).setMinimumTokens(cli.getMinimumTokens());

			processFilterString(cli);

			if (cli.isDebug()) {
				LOGGER.info("Debugging enabled");
				LogUtils.setDebugLevel();
			} else {
				LogUtils.unsetDebugLevel();
			}

			observerManager.setUpProgressMonitor(cli);

			observerManager.notifyObservers(ProgressEvent.PREPARE_COMPLETE);

			if (cli.getMode() == MeasurementMode.ComplexityMode) {
				LOGGER.info("Code Size Analysis start...");

				runSonarAnalysis(cli);

				LOGGER.info("Complexity Analysis start...");

				runComplexity(cli);

			} else {

				TargetManager targetManager = TargetManager.getInstance(cli.getInstanceKey());

				try {
					List<TargetFile> targetFileList = targetManager.getTargetFileList(project.getCanonicalPath(), cli.getSrc(), cli.getBinary(),
							MeasuredResult.getInstance(cli.getInstanceKey()));

					LOGGER.info("Target File Count From Target Manager : {}", targetFileList.size());
				} catch (IOException e) {
					LOGGER.error("Project Directory Error : {}", cli.getProjectBaseDir());
					return;
				}

				if (cli.getIndividualMode().isCodeSize() || cli.getIndividualMode().isDuplication() || cli.getIndividualMode().isSonarJava() || cli.getIndividualMode().isWebResources()) {
					List<String> sonarAnalysisModeList = new ArrayList<>();
					if (cli.getIndividualMode().isCodeSize()) {
						sonarAnalysisModeList.add("Code Size");
					}
					if (cli.getIndividualMode().isDuplication()) {
						sonarAnalysisModeList.add("Duplication");
					}
					if (cli.getIndividualMode().isSonarJava()) {
						sonarAnalysisModeList.add("Sonar Java");
					}
					if (cli.getIndividualMode().isJavascript()) {
						sonarAnalysisModeList.add("JavaScript");
					}
					if (cli.getIndividualMode().isCss()) {
						sonarAnalysisModeList.add("CSS");
					}
					if (cli.getIndividualMode().isHtml()) {
						sonarAnalysisModeList.add("HTML");
					}
					String sonarAnalysisMode = StringUtils.join(sonarAnalysisModeList, " & ");
					LOGGER.info(sonarAnalysisMode + " Analysis start...");

					runSonarAnalysis(cli);
				}

				if (cli.getIndividualMode().isDuplication() && cli.isTokenBased()) {
					runPmdCpd(cli);
				}

				if (cli.getIndividualMode().isComplexity()) {
					LOGGER.info("Complexity Analysis start...");

					runComplexity(cli);
				}

				if (cli.getIndividualMode().isPmd()) {
					LOGGER.info("PMD Analysis start...");

					runPmd(cli);
				}

				if (cli.getIndividualMode().isFindBugs()) {
					LOGGER.info("FindBugs Analysis start...");

					runFindBugs(cli);
				}

				if (cli.getIndividualMode().isFindSecBugs()) {
					LOGGER.info("FindSecBugs Analysis start...");

					runFindSecBugs(cli);
				}

				if (cli.getIndividualMode().isDependency()) {
					LOGGER.info("JDepend Analysis start...");

					runJDepend(cli);
				}

				if (cli.getIndividualMode().isUnusedCode()) {
					LOGGER.info("UnusedCode Analysis start...");

					runUnusedCode(cli);
				}

				if (cli.getIndividualMode().isCkMetrics()) {
					LOGGER.info("CK Metrics Analysis start...");

					runCkMetrics(cli);
				}
			}

			LOGGER.info("TechnicalDebt Analysis start...");
			runTechnicalDebt(cli);

			LOGGER.info("Code Analysis ended");

			processResult(cli);
		} else {
			parsingError = true;
			parsingErrorMessage = cli.getErrorMessage();
		}
	}

	private void processFilterString(CliParser cli) {
		if (!"".equals(cli.getIncludes())) {
			LOGGER.info("Include patterns : {}", cli.getIncludes());

			MeasuredResult.getInstance(cli.getInstanceKey()).setIncludeFilters(cli.getIncludes());
		}

		if (!"".equals(cli.getExcludes())) {
			LOGGER.info("Exclude patterns : {}", cli.getExcludes());

			MeasuredResult.getInstance(cli.getInstanceKey()).setExcludeFilters(cli.getExcludes());
		}
	}

	private void runSonarAnalysis(CliParser cli) {
		AppForSonarAnalysis delegator = new AppForSonarAnalysis(cli, observerManager);

		delayWorkList.add(delegator);	// clean up

		delegator.runSonarAnalysis();
	}

	private void runPmdCpd(CliParser cli) {
		PmdCpd cpd = new PmdCpdLauncher();

		cpd.addOption("--encoding", cli.getEncoding());
		cpd.addOption("--format", "csv");
		cpd.addOption("-failOnViolation", "false");

		cpd.addOption("--minimum-tokens", Integer.toString(cli.getMinimumTokens()));

		String dirs = FindFileUtils.getMultiDirectoriesWithComma(cli.getProjectBaseDir(), cli.getSrc());
		cpd.addOption("--files", dirs);

		cpd.run(cli.getInstanceKey());
	}

	private void runComplexity(CliParser cli) {
		ComplexityAnalysis pmdComplexity = new ComplexityAnalysisLauncher();

		String dir = FindFileUtils.getMultiDirectoriesWithComma(cli.getProjectBaseDir(), cli.getSrc());
		if (cli.getMode() == MeasurementMode.ComplexityMode) {
			try {
				dir = FindFileUtils.getDirectoryWithFilenamePattern(dir, cli.getClassForCCMeasurement());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
			pmdComplexity.addOption("-dir", dir);

		} else {
			if ("".equals(cli.getIncludes())) {
				pmdComplexity.addOption("-dir", dir);
			} else {
				SourceFileHandler pathHandler = new SourceFileHandler(cli.getProjectBaseDir(), cli.getSrc().split(FindFileUtils.COMMA_SPLITTER));

				pmdComplexity.addOption("-dir", pathHandler.getPathStringWithInclude(cli.getIncludes()));
			}
		}

		if (cli.isDebug()) {
			pmdComplexity.addOption("-debug", "");
		}

		pmdComplexity.addOption("-encoding", cli.getEncoding());
		pmdComplexity.addOption("-version", cli.getJavaVersion());
		pmdComplexity.addOption("-language", "java");

		pmdComplexity.run(cli.getInstanceKey());

		observerManager.notifyObservers(ProgressEvent.COMPLEXITY_COMPLETE);
	}

	private void runPmd(CliParser cli) {
		PmdAnalysis pmdViolation = new PmdAnalysisLauncher();

		String dir = FindFileUtils.getMultiDirectoriesWithComma(cli.getProjectBaseDir(), cli.getSrc());

		if ("".equals(cli.getIncludes())) {
			pmdViolation.addOption("-dir", dir);
		} else {
			SourceFileHandler pathHandler = new SourceFileHandler(cli.getProjectBaseDir(), cli.getSrc().split(FindFileUtils.COMMA_SPLITTER));

			pmdViolation.addOption("-dir", pathHandler.getPathStringWithInclude(cli.getIncludes()));
		}

		if (cli.isDebug()) {
			pmdViolation.addOption("-debug", "");
		}

		pmdViolation.addOption("-encoding", cli.getEncoding());
		pmdViolation.addOption("-version", cli.getJavaVersion());
		pmdViolation.addOption("-language", "java");

		if (cli.getRuleSetFileForPMD() != null && !cli.getRuleSetFileForPMD().equals("")) {
			pmdViolation.addOption("-rulesets", cli.getRuleSetFileForPMD());

			MeasuredResult.getInstance(cli.getInstanceKey()).setPmdRules(XmlElementUtil.getElementCount(cli.getRuleSetFileForPMD(), "rule"));
		} else {
			MeasuredResult.getInstance(cli.getInstanceKey()).setPmdRules(Version.PMD_DEFAULT_RULES);
		}

		pmdViolation.run(cli.getInstanceKey());

		observerManager.notifyObservers(ProgressEvent.PMD_COMPLETE);
	}

	private void runFindBugs(CliParser cli) {
		if (cli.isDebug()) {
			System.setProperty("findbugs.debug", "true");
		}

		String[] binaryDirectories = FindFileUtils.getFullDirectories(cli.getProjectBaseDir(), cli.getBinary());

		boolean isFirstRun = true;

		for (String binary : binaryDirectories) {

			LOGGER.info("FindBugs Target : {}", binary);

			FindBugsAnalysis findBugsViolation = new FindBugsAnalysisLauncher();

			findBugsViolation.setTarget(binary);

			if (cli.getRuleSetFileForFindBugs() != null && !cli.getRuleSetFileForFindBugs().equals("")) {
				findBugsViolation.addOption("-include", cli.getRuleSetFileForFindBugs());

				if (isFirstRun) {
					MeasuredResult.getInstance(cli.getInstanceKey()).setFindBugsRules(XmlElementUtil.getElementCount(cli.getRuleSetFileForFindBugs(), "Match"));
				}
			} else {
				if (isFirstRun) {
					MeasuredResult.getInstance(cli.getInstanceKey()).setFindBugsRules(Version.FINDBUGS_DEFAULT_RULES);
				}
			}

			findBugsViolation.run(cli.getInstanceKey());

			isFirstRun = false;
		}

		observerManager.notifyObservers(ProgressEvent.FINDBUGS_COMPLETE);
	}

	private void runFindSecBugs(CliParser cli) {
		if (cli.isDebug()) {
			System.setProperty("findbugs.debug", "true");
		}

		String[] binaryDirectories = FindFileUtils.getFullDirectories(cli.getProjectBaseDir(), cli.getBinary());

		boolean isFirstRun = true;

		for (String binary : binaryDirectories) {
			FindBugsAnalysis findBugsViolation = new FindSecBugsAnalysisLauncher();

			findBugsViolation.setTarget(binary);

			if (isFirstRun) {
				MeasuredResult.getInstance(cli.getInstanceKey()).setFindSecBugsRules(Version.FINDSECBUGS_DEFAULT_RULES);
			}

			findBugsViolation.run(cli.getInstanceKey());
		}

		observerManager.notifyObservers(ProgressEvent.FINDSECBUGS_COMPLETE);
	}

	private void runJDepend(CliParser cli) {
		JDependAnalysis jdepend = new JDependAnalysisLauncher();

		String dirs = FindFileUtils.getMultiDirectoriesWithComma(cli.getProjectBaseDir(), cli.getBinary());

		jdepend.setTarget(dirs);

		List<String> packageList = new ArrayList<>();

		for (String dir : FindFileUtils.getFullDirectories(cli.getProjectBaseDir(), cli.getBinary())) {
			packageList.addAll(PackageUtils.getProjectPackages(dir));
		}

		LOGGER.debug("Package List");
		for (String packageName : MeasuredResult.getInstance(cli.getInstanceKey()).getPackageList()) {
			LOGGER.debug("- {}", packageName);
		}

		LOGGER.info("Target Package List");
		for (String packageName : packageList) {
			if (packageName.equals("")) {
				LOGGER.warn("The project has class(es) with no package... JDepend can't check with no package class(es)");
				MeasuredResult.getInstance(cli.getInstanceKey()).setWithDefaultPackageClasses(true);
				continue;
			}
			if (MeasuredResult.getInstance(cli.getInstanceKey()).getPackageList().contains(packageName)) {
				LOGGER.info("- {}", packageName);
				jdepend.addIncludePackage(packageName);
			} else {
				LOGGER.info("- {} : skipped...", packageName);
			}
		}

		jdepend.run(cli.getInstanceKey());

		observerManager.notifyObservers(ProgressEvent.DEPENDENCY_COMPLETE);
	}

	private void runUnusedCode(CliParser cli) {
		UnusedCodeAnalysis unusedCodeViolation = new UnusedCodeAnalysisLauncher();
		
		unusedCodeViolation.setProjectBaseDir(cli.getProjectBaseDir());
		unusedCodeViolation.setTargetBinary(cli.getBinary());
		unusedCodeViolation.setTargetSrc(cli.getSrc());

		unusedCodeViolation.run(cli.getInstanceKey());

		observerManager.notifyObservers(ProgressEvent.UNUSED_COMPLETE);
	}

	private void runCkMetrics(CliParser cli) {
		CkMetricsAnalysis ckMetricsAnalysis = new CkMetricsAnalysisLauncher();

		ckMetricsAnalysis.setProjectBaseDir(cli.getProjectBaseDir());
		ckMetricsAnalysis.setSourceDirectories(cli.getSrc());
		ckMetricsAnalysis.setBinaryDirectories(cli.getBinary());

		ckMetricsAnalysis.run(cli.getInstanceKey());

		observerManager.notifyObservers(ProgressEvent.CK_METRICS_COMPLETE);
	}

	private void runTechnicalDebt(CliParser cli) {
		TechnicalDebtAnalysis technicalDebt = new TechnicalDebtAnalysisLauncher();

		technicalDebt.run(cli.getInstanceKey());
	}

	private void processResult(CliParser cli) {
		if (cli.getMode() == MeasurementMode.DefaultMode) {
			File outputFile = null;

			if (cli.getOutput() == null || cli.getOutput().equals("")) {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				String fileName = "result-" + format.format(new Date()) + (cli.getFormat() == OutputFileFormat.JSON ? ".json" : ".out");

				outputFile = new File(fileName);
			} else {
				outputFile = new File(cli.getOutput());
			}

            MeasuredResult.getInstance(cli.getInstanceKey()).calculateElapsedTime();

			ResultProcessor.saveResultOutputFile(outputFile, cli, MeasuredResult.getInstance(cli.getInstanceKey()));
		}

		ResultProcessor.printSummary(MeasuredResult.getInstance(cli.getInstanceKey()));
	}

	public void cleanup(String instanceKey) {
		MeasuredResult.getInstance(instanceKey).clear();

		for (DelayWork work : delayWorkList) {
			work.doing();
		}

		observerManager.notifyObservers(ProgressEvent.FINAL_COMPLETE);

		MeasuredResult.removeInstance(instanceKey);
	}

	public void addProgressObserver(ProgressObserver observer) {
		observerManager.addObserver(observer);
	}

	public void removeProgressObserver(ProgressObserver observer) {
		observerManager.removeObserver(observer);
	}

	public boolean hasParsingError() {
		return parsingError;
	}

	public String getParsingErrorMessage() {
		return parsingErrorMessage;
	}

	public static void main(String[] args) {
		CliParser cli = new CliParser(args);

		String instanceKey = App.class.getName();

		cli.setInstanceKey(instanceKey);

		App app = new App();

		try {
			app.process(cli);
		} catch (Throwable ex) {
			LOGGER.error("Error", ex);
		} finally {
			app.cleanup(instanceKey);
			System.exit(0);
		}
	}
}
