package com.samsungsds.analyst.code.main;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CliParser {
	private static final Logger LOGGER = LogManager.getLogger(CliParser.class);
	private static final String APPLICATION_JAR = "Code-Analyst-" + Version.CODE_ANALYST + ".jar";
	
	private String[] args = null;
	private Options options = new Options();
	
	private String projectBaseDir = ".";
	private String src = "src";
	private String binary = "target" + File.separator + "classes";
	private String library = "";
	private boolean debug = false;
	private String encoding = "UTF-8"; 
	private String javaVersion = "1.8";
	
	private String ruleSetFileForPMD = "";
	private String ruleSetFileForFindBugs = "";
	
	private String output = "";
	
	private String timeout = "120";	// second
	
	private MeasurementMode mode = MeasurementMode.DefaultMode;
	private String classForCCMeasurement = "";
	
	public CliParser(String[] args) {
		this.args = args;
		
		options.addOption("h", "help", false, "show help.");
		options.addOption("p", "project", true, "specify project base directory. (default: \".\")");
		options.addOption("s", "src", true, "specify source directory. (default: \"${project}" + File.separator + "src" + "\")");
		options.addOption("b", "binary", true, "specify binary directory. (default: \"${project}" + File.separator + "target" + File.separator + "classes" + "\")");
		options.addOption("l", "library", true, "specify libary directory, jar files contained.");
		options.addOption("d", "debug", false, "debug mode.");
		options.addOption("e", "encoding", true, "encoding of the source code. (default: UTF-8)");
		options.addOption("j", "java", true, "specify java version. (default: 1.8)");
		
		options.addOption("pmd", true, "specify PMD ruleset xml file.");
		options.addOption("findbugs", true, "specify FindBugs ruleset(include filter) xml file.");
		
		options.addOption("o", "output", true, "specify result output file. (default : \"result-[yyyyMMddHHmmss].out\")");
		options.addOption("v", "version", false, "display version info.");
		options.addOption("t", "timeout", true, "specify internal ws timeout. (default : 120 sec.)");
		
		options.addOption("c", "complexity", true, "specify class name(glob pattern) to be measured. (Cyclomatic Complexity Measurement mode)");
	}
	
	public boolean parse() {
		CommandLineParser parser = new DefaultParser();
		
		CommandLine cmd = null;
		
		try {
			cmd = parser.parse(options, args);
			
			if (cmd.hasOption("h")) {
				help();
				return false;
			}
			
			if (cmd.hasOption("v")) {
				Version.printVersionInfo();
				return false;
			}
			
			if (cmd.hasOption("p")) {
				projectBaseDir = cmd.getOptionValue("p");
			}
			
			if (cmd.hasOption("s")) {
				src = cmd.getOptionValue("s");
			}
			
			if (cmd.hasOption("b")) {
				binary = cmd.getOptionValue("b");
			}
			
			if (cmd.hasOption("l")) {
				library = cmd.getOptionValue("l");
			}
			
			if (cmd.hasOption("d")) {
				debug = true;
			}
			
			if (cmd.hasOption("e")) {
				encoding = cmd.getOptionValue("e");
			}
			
			if (cmd.hasOption("j")) {
				javaVersion = cmd.getOptionValue("j");
			}
			
			if (cmd.hasOption("pmd")) {
				ruleSetFileForPMD = cmd.getOptionValue("pmd");
			}
			
			if (cmd.hasOption("findbugs")) {
				ruleSetFileForFindBugs = cmd.getOptionValue("findbugs");
			}
			
			if (cmd.hasOption("o")) {
				output = cmd.getOptionValue("o");
			}
			
			if (cmd.hasOption("t")) {
				timeout = cmd.getOptionValue("t");
			}
			
			if (cmd.hasOption("c")) {
				mode = MeasurementMode.ComplexityMode;
				classForCCMeasurement = cmd.getOptionValue("c");
			}
			
			return true;
		} catch (ParseException pe) {
			LOGGER.error("Failed to parse command line", pe);
			help();
			return false;
		}
	}
	
	private void help() {
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.setWidth(120);
		formatter.setOptionComparator(null);	// no order
		
		formatter.printHelp("java -jar " + APPLICATION_JAR, options);
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getBinary() {
		return binary;
	}

	public void setBinary(String binary) {
		this.binary = binary;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getProjectBaseDir() {
		return projectBaseDir;
	}

	public void setProjectBaseDir(String projectBaseDir) {
		this.projectBaseDir = projectBaseDir;
	}

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getRuleSetFileForPMD() {
		return ruleSetFileForPMD;
	}

	public void setRuleSetFileForPMD(String ruleSetFileForPMD) {
		this.ruleSetFileForPMD = ruleSetFileForPMD;
	}

	public String getRuleSetFileForFindBugs() {
		return ruleSetFileForFindBugs;
	}

	public void setRuleSetFileForFindBugs(String ruleSetFileForFindBugs) {
		this.ruleSetFileForFindBugs = ruleSetFileForFindBugs;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public MeasurementMode getMode() {
		return mode;
	}

	public String getClassForCCMeasurement() {
		return classForCCMeasurement;
	}
}
