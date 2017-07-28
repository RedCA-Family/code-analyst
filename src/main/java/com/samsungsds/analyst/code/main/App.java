package com.samsungsds.analyst.code.main;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import org.sonarsource.scanner.api.ScannerApiVersion;
import org.sonarsource.scanner.api.ScannerProperties;
import org.sonarsource.scanner.api.internal.InternalProperties;

import com.samsungsds.analyst.code.findbugs.FindBugsAnalysis;
import com.samsungsds.analyst.code.findbugs.FindBugsAnalysisLauncher;
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
import com.samsungsds.analyst.code.util.FindFileUtils;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import com.samsungsds.analyst.code.util.PackageUtils;

public class App {
	private static final Logger LOGGER = LogManager.getLogger(App.class);
	
	private static final String SONAR_VERBOSE = "sonar.verbose";
	
	protected void process(CliParser cli) {
		if (cli.parse()) {
			
			SystemInfo.print();
    		
    		File project = new File(cli.getProjectBaseDir());
    		
    		try {
				LOGGER.info("Project Directory : {}", project.getCanonicalPath());
				MeasuredResult.getInstance().setProjectDirectory(project.getCanonicalPath());
			} catch (IOException ex) {
				LOGGER.error("Project Directory Error : {}", cli.getProjectBaseDir());
				return;
			}
    		
    		MeasuredResult.getInstance().setMode(cli.getMode());
    		
    		processFilterString(cli);
     		
    		LOGGER.info("Code Size Analysis start...");
    		
    		runCodeSizeAalysis(cli);
    		
    		LOGGER.info("Complexity Analysis start...");
    		
    		runComplexity(cli);
    		
    		if (cli.getMode() == MeasurementMode.DefaultMode) {
	    		LOGGER.info("PMD Analysis start...");
	    		
	    		runPmd(cli);
    		}
    		
    		if (cli.getMode() == MeasurementMode.DefaultMode) {
	    		LOGGER.info("FindBugs Analysis start...");
	    		
	    		runFindBugs(cli);
    		}
	    	
    		if (cli.getMode() == MeasurementMode.DefaultMode) {
	    		LOGGER.info("JDepend Analysis start...");
	    		
	    		runJDepend(cli);
    		}
	    		
	    	LOGGER.info("Code Analysis ended");    		
	    		
	    	processResult(cli);
    	}  
	}

	private void processFilterString(CliParser cli) {
		if (!"".equals(cli.getIncludes())) {
			LOGGER.info("Include patterns : {}", cli.getIncludes());
			
			MeasuredResult.getInstance().setIncludeFilters(cli.getIncludes());
		}
		
		if (!"".equals(cli.getExcludes())) {
			LOGGER.info("Exclude patterns : {}", cli.getExcludes());
			
			MeasuredResult.getInstance().setExcludeFilters(cli.getExcludes());
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
		} else {
			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			Configuration conf = ctx.getConfiguration();
			conf.getLoggerConfig("com.samsungsds.analyst.code").setLevel(Level.INFO);
			conf.getLoggerConfig("org.sonar").setLevel(Level.INFO);
			ctx.updateLoggers(conf);
		}
		
		sonar.addProperty(ScannerProperties.HOST_URL, "http://localhost:" + port);
		
		sonar.addProperty(InternalProperties.SCANNER_APP, "SonarQubeScanner");
		sonar.addProperty(InternalProperties.SCANNER_APP_VERSION, ScannerApiVersion.version());
		
		LOGGER.debug("Sonar Scanner Version : {}", ScannerApiVersion.version());
		
		sonar.addProperty(ScanProperties.PROJECT_SOURCE_ENCODING, cli.getEncoding());
		
		//sonar.addProperty(CoreProperties.ANALYSIS_MODE, CoreProperties.ANALYSIS_MODE_PREVIEW);
		sonar.addProperty(CoreProperties.ANALYSIS_MODE, CoreProperties.ANALYSIS_MODE_PUBLISH);
		
		sonar.addProperty(CoreProperties.PROJECT_KEY_PROPERTY, "local");
		
		sonar.addProperty("sonar.projectBaseDir", cli.getProjectBaseDir());
		sonar.addProperty(ProjectDefinition.SOURCES_PROPERTY, cli.getSrc());
		sonar.addProperty("sonar.java.source", cli.getJavaVersion());
		
		// BatchWSClient timeout
		sonar.addProperty("sonar.ws.timeout", cli.getTimeout());
		
		if (!cli.getLibrary().equals("")) {
			sonar.addProperty("sonar.java.libraries", cli.getLibrary());
		}
		
		sonar.addProperty("sonar.scanAllFiles", "true");
		
		if (!cli.getIncludes().equals("")) {
			sonar.addProperty("sonar.inclusions", cli.getIncludes());
		}
		
		if (!cli.getExcludes().equals("")) {
			sonar.addProperty("sonar.exclusions", cli.getExcludes());
		}
		
		sonar.run();
		
		LOGGER.info("Surrogate Sonar Server stoping...");
		server.stop();
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
		
		pmdComplexity.run();
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
		
		pmdViolation.run();
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
		
		findBugsViolation.run();
	}
	
	private void runJDepend(CliParser cli) {
		JDependAnalysis jdepend = new JDependAnalysisLauncher();
		
		jdepend.setTarget(cli.getProjectBaseDir() + File.separator + cli.getBinary());
		
		List<String> packageList = PackageUtils.getProjectPackages(cli.getProjectBaseDir() + File.separator + cli.getBinary());
		
		LOGGER.debug("Package List");
		for (String packageName : MeasuredResult.getInstance().getPackageList()) {
			LOGGER.debug("- {}", packageName);
		}
		
		LOGGER.debug("Target Package List");
		for (String packageName : packageList) {
			if (MeasuredResult.getInstance().getPackageList().contains(packageName)) {
				LOGGER.debug("- {}", packageName);
				jdepend.addIncludePackage(packageName);
			} else {
				LOGGER.debug("- {} : skipped...", packageName);
			}
		}
		
		jdepend.run();
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
			
			ResultProcessor.saveResultOutputFile(outputFile, cli, MeasuredResult.getInstance());
		}
		
		ResultProcessor.printSummary(MeasuredResult.getInstance());
	}
		
    public static void main(String[] args) {
    	CliParser cli = new CliParser(args);
    	
    	App app = new App();
    	
    	try {
    		app.process(cli);
    	} catch (Throwable ex) {
    		LOGGER.error("Error", ex);
    	} finally {
    		IOAndFileUtils.deleteDirectory(new File(".sonar"));
    		System.exit(0);
    	}
    }
}
