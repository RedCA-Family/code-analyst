package com.samsungsds.analyst.code.main;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonarsource.scanner.api.ScanProperties;
import org.sonarsource.scanner.api.ScannerProperties;
import org.sonarsource.scanner.api.internal.InternalProperties;

import com.samsungsds.analyst.code.api.AnalysisMode;
import com.samsungsds.analyst.code.api.AnalysisProgress;
import com.samsungsds.analyst.code.api.ProgressEvent;
import com.samsungsds.analyst.code.api.ProgressObserver;
import com.samsungsds.analyst.code.api.impl.AnalysisProgressMonitor;
import com.samsungsds.analyst.code.findbugs.FindBugsAnalysis;
import com.samsungsds.analyst.code.findbugs.FindBugsAnalysisLauncher;
import com.samsungsds.analyst.code.findbugs.FindSecBugsAnalysisLauncher;
import com.samsungsds.analyst.code.jdepend.JDependAnalysis;
import com.samsungsds.analyst.code.jdepend.JDependAnalysisLauncher;
import com.samsungsds.analyst.code.main.result.OutputFileFormat;
import com.samsungsds.analyst.code.pmd.ComplexityAnalysis;
import com.samsungsds.analyst.code.pmd.ComplexityAnalysisLauncher;
import com.samsungsds.analyst.code.pmd.PmdAnalysis;
import com.samsungsds.analyst.code.pmd.PmdAnalysisLauncher;
import com.samsungsds.analyst.code.sonar.SonarAnalysis;
import com.samsungsds.analyst.code.sonar.SonarAnalysisLauncher;
import com.samsungsds.analyst.code.sonar.server.JettySurrogateSonarServer;
import com.samsungsds.analyst.code.sonar.server.SurrogateSonarServer;
import com.samsungsds.analyst.code.technicaldebt.TechnicalDebtAnalysis;
import com.samsungsds.analyst.code.technicaldebt.TechnicalDebtAnalysisLauncher;
import com.samsungsds.analyst.code.unusedcode.UnusedCodeAnalysis;
import com.samsungsds.analyst.code.unusedcode.UnusedCodeAnalysisLauncher;
import com.samsungsds.analyst.code.util.FindFileUtils;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import com.samsungsds.analyst.code.util.PackageUtils;

public class App {
	private static final Logger LOGGER = LogManager.getLogger(App.class);
	
	private static final String SONAR_VERBOSE = "sonar.verbose";
	
	private List<ProgressObserver> observerList = new ArrayList<>();
	
	private AnalysisProgressMonitor progressMonitor;
	
	private boolean parsingError = false;
	private String parsingErrorMessage = "";
	
	public void process(CliParser cli) {
		if (cli.parse()) {
			
			SystemInfo.print();
			
			MeasuredResult.getInstance(cli.getInstanceKey()).initialize(cli.isDetailAnalysis(), cli.isSeperatedOutput());
			if (cli.isDetailAnalysis()) {
				LOGGER.info("Detail Analysis mode...");
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
    		
    		processFilterString(cli);
    		
    		if (cli.isDebug()) {
    			LOGGER.info("Debugging enabled");
    		} else {
    			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    			Configuration conf = ctx.getConfiguration();
    			conf.getLoggerConfig("com.samsungsds.analyst.code").setLevel(Level.INFO);
    			conf.getLoggerConfig("org.sonar").setLevel(Level.INFO);
    			ctx.updateLoggers(conf);
    		}
    		
    		if (cli.getMode() == MeasurementMode.ComplexityMode) {
    			AnalysisMode analysisMode = new AnalysisMode();
    			
    			analysisMode.setCodeSize(true);
    			analysisMode.setComplexity(true);
    			
				progressMonitor = new AnalysisProgressMonitor(analysisMode);	
    		} else {
    			progressMonitor = new AnalysisProgressMonitor(cli.getIndividualMode());
    		}
    		
    		if (progressMonitor != null) {
    			notifyObservers(progressMonitor.getNextAnalysisProgress(ProgressEvent.PREPARE_COMPLETE));
    		}
     		
    		if (cli.getMode() == MeasurementMode.ComplexityMode) {
    			LOGGER.info("Code Size Analysis start...");
        		
    			runCodeSizeAalysis(cli);
    			
    			LOGGER.info("Complexity Analysis start...");
        		
    			runComplexity(cli);
    			
    		} else {
    			
    			if (cli.getIndividualMode().isCodeSize() && cli.getIndividualMode().isDuplication()) {
        			LOGGER.info("Code Size & Duplication Analysis start...");
        		
        			runCodeSizeAalysis(cli);
        		} else if (cli.getIndividualMode().isCodeSize()) {
        			LOGGER.info("Code Size Analysis start...");
            		
        			runCodeSizeAalysis(cli);
        		} else if (cli.getIndividualMode().isDuplication()) {
        			LOGGER.info("Duplication Analysis start...");
            		
        			runCodeSizeAalysis(cli);
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
	
	private void runCodeSizeAalysis(CliParser cli) {
		LOGGER.info("Surrogate Sonar Server starting...");
		SurrogateSonarServer server = new JettySurrogateSonarServer();
		int port = server.startAndReturnPort();
			
		LOGGER.info("Sonar Scanner starting...");
		SonarAnalysis sonar = new SonarAnalysisLauncher(cli.getSrc());
		
		if (cli.isDebug()) {
			sonar.addProperty(SONAR_VERBOSE, "true");
		}
		
		sonar.addProperty(ScannerProperties.HOST_URL, "http://localhost:" + port);
		
		sonar.addProperty(InternalProperties.SCANNER_APP, "SonarQubeScanner");
		// sonar-scanner-api 버전업(2.8 -> 2.10) 후 api버전기능 삭제로 주석처리함
		/*sonar.addProperty(InternalProperties.SCANNER_APP_VERSION, ScannerApiVersion.version());
		
		LOGGER.debug("Sonar Scanner Version : {}", ScannerApiVersion.version());*/
		
		sonar.addProperty(ScanProperties.PROJECT_SOURCE_ENCODING, cli.getEncoding());
		
		//sonar.addProperty(CoreProperties.ANALYSIS_MODE, CoreProperties.ANALYSIS_MODE_PREVIEW);
		sonar.addProperty(CoreProperties.ANALYSIS_MODE, CoreProperties.ANALYSIS_MODE_PUBLISH);
		
		sonar.addProperty(CoreProperties.PROJECT_KEY_PROPERTY, "local");
		
		sonar.addProperty("sonar.projectBaseDir", cli.getProjectBaseDir());
		sonar.addProperty("sonar.java.binaries", cli.getBinary());
		sonar.addProperty(ProjectDefinition.SOURCES_PROPERTY, cli.getSrc());
		sonar.addProperty("sonar.java.source", cli.getJavaVersion());
		
		// BatchWSClient timeout
		sonar.addProperty("sonar.ws.timeout", cli.getTimeout());
		
		if (!cli.getLibrary().equals("")) {
			sonar.addProperty("sonar.java.libraries", cli.getLibrary());
		}
		
		sonar.addProperty("sonar.scanAllFiles", "true");
		
		if (!cli.getIncludes().equals("")) {
			sonar.addProperty("sonar.inclusions", MeasuredResult.getInstance(cli.getInstanceKey()).getIncludes());
		}
		
		if (!cli.getExcludes().equals("")) {
			sonar.addProperty("sonar.exclusions", MeasuredResult.getInstance(cli.getInstanceKey()).getExcludes());
		}
		
		sonar.run(cli.getInstanceKey());

		if (progressMonitor != null) {
			notifyObservers(progressMonitor.getNextAnalysisProgress(ProgressEvent.CODE_SIZE_COMPLETE));
		}
		
		LOGGER.info("Surrogate Sonar Server stoping...");
		server.stop();
		
		if (progressMonitor != null) {
			notifyObservers(progressMonitor.getNextAnalysisProgress(ProgressEvent.DUPLICATION_COMPLETE));
		}
	}

	private void runComplexity(CliParser cli) {
		ComplexityAnalysis pmdComplexity = new ComplexityAnalysisLauncher();
		
		String dir = cli.getProjectBaseDir() + File.separator + cli.getSrc();
		if (cli.getMode() == MeasurementMode.ComplexityMode) {
			try {
				dir = FindFileUtils.getDirectoryWithFilenamePattern(dir, cli.getClassForCCMeasurement());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
			pmdComplexity.addOption("-dir", dir);
			
		} else {
			pmdComplexity.addOption("-dir", dir);
		}
		
		if (cli.isDebug()) {
			pmdComplexity.addOption("-debug", "");
		}
		
		pmdComplexity.addOption("-encoding", cli.getEncoding());
		pmdComplexity.addOption("-version", cli.getJavaVersion());
		pmdComplexity.addOption("-language", "java");
		
		pmdComplexity.run(cli.getInstanceKey());
		
		if (progressMonitor != null) {
			notifyObservers(progressMonitor.getNextAnalysisProgress(ProgressEvent.COMPLEXITY_COMPLETE));
		}
	}
	
	private void runPmd(CliParser cli) {
		PmdAnalysis pmdViolation = new PmdAnalysisLauncher();
		
		pmdViolation.addOption("-dir", cli.getProjectBaseDir() + File.separator + cli.getSrc());
		
		if (cli.isDebug()) {
			pmdViolation.addOption("-debug", "");
		}
		
		pmdViolation.addOption("-encoding", cli.getEncoding());
		pmdViolation.addOption("-version", cli.getJavaVersion());
		pmdViolation.addOption("-language", "java");
		
		if (cli.getRuleSetFileForPMD() != null && !cli.getRuleSetFileForPMD().equals("")) {
			pmdViolation.addOption("-rulesets", cli.getRuleSetFileForPMD());
		}
		
		pmdViolation.run(cli.getInstanceKey());
		
		if (progressMonitor != null) {
			notifyObservers(progressMonitor.getNextAnalysisProgress(ProgressEvent.PMD_COMPLETE));
		}
	}
	
	private void runFindBugs(CliParser cli) {
		FindBugsAnalysis findBugsViolation = new FindBugsAnalysisLauncher();
		
		findBugsViolation.setTarget(cli.getProjectBaseDir() + File.separator + cli.getBinary());
		
		if (cli.isDebug()) {
			System.setProperty("findbugs.debug", "true");
		}
		
		if (cli.getRuleSetFileForFindBugs() != null && !cli.getRuleSetFileForFindBugs().equals("")) {
			findBugsViolation.addOption("-include", cli.getRuleSetFileForFindBugs());
		}
		
		findBugsViolation.run(cli.getInstanceKey());
		
		if (progressMonitor != null) {
			notifyObservers(progressMonitor.getNextAnalysisProgress(ProgressEvent.FINDBUGS_COMPLETE));
		}
	}
	
	private void runFindSecBugs(CliParser cli) {
		FindBugsAnalysis findBugsViolation = new FindSecBugsAnalysisLauncher();
		
		findBugsViolation.setTarget(cli.getProjectBaseDir() + File.separator + cli.getBinary());
		
		if (cli.isDebug()) {
			System.setProperty("findbugs.debug", "true");
		}
		
		findBugsViolation.run(cli.getInstanceKey());
		
		if (progressMonitor != null) {
			notifyObservers(progressMonitor.getNextAnalysisProgress(ProgressEvent.FINDSECBUGS_COMPLETE));
		}
	}
	
	private void runJDepend(CliParser cli) {
		JDependAnalysis jdepend = new JDependAnalysisLauncher();
		
		jdepend.setTarget(cli.getProjectBaseDir() + File.separator + cli.getBinary());
		
		List<String> packageList = PackageUtils.getProjectPackages(cli.getProjectBaseDir() + File.separator + cli.getBinary());
		
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
		
		if (progressMonitor != null) {
			notifyObservers(progressMonitor.getNextAnalysisProgress(ProgressEvent.DEPENDENCY_COMPLETE));
		}
	}

	private void runUnusedCode(CliParser cli) {
		UnusedCodeAnalysis unusedCodeViolation = new UnusedCodeAnalysisLauncher();
		
		unusedCodeViolation.setTargetBinary(cli.getProjectBaseDir() + File.separator + cli.getBinary());
		unusedCodeViolation.setTargetSrc(cli.getProjectBaseDir() + File.separator + cli.getSrc());
		
		unusedCodeViolation.run(cli.getInstanceKey());
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
			
			ResultProcessor.saveResultOutputFile(outputFile, cli, MeasuredResult.getInstance(cli.getInstanceKey()));
		}
		
		ResultProcessor.printSummary(MeasuredResult.getInstance(cli.getInstanceKey()));
	}
	
	public void cleanup(String instanceKey) {
		MeasuredResult.getInstance(instanceKey).clear();
		
		IOAndFileUtils.deleteDirectory(new File(".sonar"));
		
		if (progressMonitor != null) {
			notifyObservers(progressMonitor.getNextAnalysisProgress(ProgressEvent.FINAL_COMPLETE));
		}
		
		MeasuredResult.removeInstance(instanceKey);
	}
	
	public void addProgressObserver(ProgressObserver observer) {
		observerList.add(observer);
	}

	public void removeProgressObserver(ProgressObserver observer) {
		observerList.remove(observer);
	}
	
	protected void notifyObservers(AnalysisProgress progress) {
		for (ProgressObserver observer : observerList) {
			observer.informProgress(progress);
		}
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
