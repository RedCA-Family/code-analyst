package com.samsungsds.analyst.code.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import com.samsungsds.analyst.code.main.result.OutputFileFormat;
import com.samsungsds.analyst.code.util.FindFileUtils;

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
	private String ruleSetFileForSonar = "";

	private String output = "";
	private OutputFileFormat format = OutputFileFormat.TEXT;

	private String timeout = "120"; // second

	private MeasurementMode mode = MeasurementMode.DefaultMode;
	private String classForCCMeasurement = "";

	private String includes = "";
	private String excludes = "";

	private IndividualMode individualMode = new IndividualMode();

	private String analysisMode = Constants.DEFAULT_ANALYSIS_MODE;

	private String errorMessage = "";

	private String instanceKey = "";

	private boolean detailAnalysis = false;

	private boolean seperatedOutput = false;

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
		options.addOption("sonar", true, "specify SonarQube issue ruleset(exclude filter) xml file." +
				"\nex:" +
				"\n<SonarIssueFilter>" +
				"\n    <Exclude key=\"common-java:DuplicatedBlocks\"/>" +
				"\n</SonarIssueFilter>");

		options.addOption("o", "output", true, "specify result output file. (default : \"result-[yyyyMMddHHmmss].[out|json]\")");
		options.addOption("f", "format", true, "specify result output file format(json, text, none). (default : text)");
		options.addOption("v", "version", false, "display version info.");
		options.addOption("t", "timeout", true, "specify internal ws timeout. (default : 120 sec.)");

		options.addOption("c", "complexity", true, "specify class name(glob pattern) to be measured. (Cyclomatic Complexity Measurement mode)");

		options.addOption("include", true, "specify include pattern(Ant-style) with comma separated. (eg: com/sds/**/*.java)");
		options.addOption("exclude", true, "specify exclude pattern(Ant-style) with comma separated. (eg: com/sds/**/*VO.java)");

		options.addOption("m", "mode", true, "specify analysis items with comma separated. (code-size, duplication, complexity, sonarjava, pmd, findbugs, findsecbugs, webresource, dependency, unusedcode)");

		options.addOption("a", "analysis", false, "detailed analysis mode. (required more memory. If OOM exception occured, use JVM '-Xmx' option like '-Xmx1024m')");

		options.addOption("r", "rerun", true, "specify previous output file to rerun with same options. "
				+ "('project', 'src', 'binary', 'encoding', 'java', 'pmd', 'findbugs', 'include', 'exclude', 'mode', 'analysis', and 'seperated')");

		options.addOption("seperated", false, "specify seperated output mode.");

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

			if (cmd.hasOption("sonar")) {
				ruleSetFileForSonar = cmd.getOptionValue("sonar");
			}

			if (cmd.hasOption("o")) {
				output = cmd.getOptionValue("o");
			}

			if (cmd.hasOption("f")) {
				String formatValue = cmd.getOptionValue("f");

				if (formatValue.equalsIgnoreCase("text") || formatValue.equalsIgnoreCase("json") || formatValue.equalsIgnoreCase("none")) {
					format = OutputFileFormat.valueOf(formatValue.toUpperCase());
				} else {
					errorMessage = "Option Error : 'format' option's value has to be 'json', 'text' or 'none'";
					System.out.println(errorMessage);
					help();
					return false;
				}
			}

			if (cmd.hasOption("t")) {
				timeout = cmd.getOptionValue("t");
			}

			if (cmd.hasOption("c")) {
				mode = MeasurementMode.ComplexityMode;
				classForCCMeasurement = cmd.getOptionValue("c");
			}

			if (cmd.hasOption("include")) {
				includes = cmd.getOptionValue("include");
			}

			if (cmd.hasOption("exclude")) {
				excludes = cmd.getOptionValue("exclude");
			}

			if (cmd.hasOption("m")) {
				if (cmd.hasOption("c")) {
					errorMessage = "Option Error : 'mode' option and 'complexity' can not be specified together";
					System.out.println(errorMessage);
					help();
					return false;
				}
				String analysisModeValue = cmd.getOptionValue("m");

				if (!settingAnalysisMode(analysisModeValue)) {
					return false;
				}
			} else {
				individualMode.setAll();
			}

			if (cmd.hasOption("a")) {
				setDetailAnalysis(true);
			}

			if (cmd.hasOption("seperated")) {
				setSeperatedOutput(true);
			}

			if (cmd.hasOption("r")) {
				getOptionsFromOutFile(cmd.getOptionValue("r"));
			}

			return true;
		} catch (ParseException pe) {
			errorMessage = "Failed to parse command line";
			LOGGER.error(errorMessage, pe);
			help();
			return false;
		}
	}

	private boolean settingAnalysisMode(String analysisModeValue) {
		String[] modes = analysisModeValue.split(FindFileUtils.COMMA_SPLITTER);

		try {
			parseIndividualMode(modes);
		} catch (IllegalArgumentException iae) {
			errorMessage = "Option Error : " + iae.getMessage();
			System.out.println(errorMessage);
			help();
			return false;
		}

		analysisMode = analysisModeValue;

		return true;
	}

	private void getOptionsFromOutFile(String outputFile) {
		Wini ini;
		try {
			ini = new Wini(new File(outputFile));
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

		projectBaseDir = getCheckedString(ini, "Project", "Target");
		src = getCheckedString(ini, "Project", "Source");
		binary = getCheckedString(ini, "Project", "Binary");
		encoding = getCheckedString(ini, "Project", "Encoding");
		javaVersion = getCheckedString(ini, "Project", "JavaVersion");
		ruleSetFileForPMD = getCheckedString(ini, "Project", "PMD", true);
		ruleSetFileForFindBugs = getCheckedString(ini, "Project", "FindBugs", true);
		ruleSetFileForSonar = getCheckedString(ini, "Project", "Sonar", true);
		includes = getCheckedString(ini, "Project", "includes", true);
		excludes = getCheckedString(ini, "Project", "excludes", true);

		String mode = getCheckedString(ini, "Project", "mode");
		if (!mode.equals(Constants.DEFAULT_ANALYSIS_MODE)) {
			settingAnalysisMode(mode);
		}

		String analysis = getCheckedString(ini, "Project", "detailAnalysis", true);

		if (analysis.equals("true")) {
			detailAnalysis = true;
		}

		String seperated = getCheckedString(ini, "Project", "seperatedOutput", true);

		if (seperated.equals("true")) {
			seperatedOutput = true;
		}

		LOGGER.info("Rerun with following options");
		LOGGER.info(" - project : {}", projectBaseDir);
		LOGGER.info(" - src : {}", src);
		LOGGER.info(" - binary : {}", binary);
		LOGGER.info(" - encoding : {}", encoding);
		LOGGER.info(" - java : {}", javaVersion);
		if (!ruleSetFileForPMD.equals("")) {
			LOGGER.info(" - pmd : {}", ruleSetFileForPMD);
		}
		if (!ruleSetFileForFindBugs.equals("")) {
			LOGGER.info(" - findbugs : {}", ruleSetFileForFindBugs);
		}
		if (!ruleSetFileForSonar.equals("")) {
			LOGGER.info(" - sonar : {}", ruleSetFileForSonar);
		}
		if (!includes.equals("")) {
			LOGGER.info(" - include : {}", includes);
		}
		if (!excludes.equals("")) {
			LOGGER.info(" - exclude : {}", excludes);
		}
		if (!mode.equals(Constants.DEFAULT_ANALYSIS_MODE)) {
			LOGGER.info(" - mode : {}", mode);
		}
		if (analysis.equals("true")) {
			LOGGER.info(" - detailAnalysis = true");
		}
		if (seperated.equals("true")) {
			LOGGER.info(" - seperatedOutput = true");
		}
	}

	private String getCheckedString(Wini ini, String sectionName, String optionName) {
		return getCheckedString(ini, sectionName, optionName, false);
	}

	private String getCheckedString(Wini ini, String sectionName, String optionName, boolean canBeNull) {
		String value = ini.get(sectionName, optionName);

		if (value == null) {
			if (canBeNull) {
				return "";
			} else {
				throw new IllegalArgumentException("[" + sectionName + "]'s " + optionName + " value null!");
			}
		}

		return value.trim();
	}

	private void parseIndividualMode(String[] modes) {
		for (String mode : modes) {
			if (mode.equalsIgnoreCase("code-size") || mode.equalsIgnoreCase("codesize")) {
				individualMode.setCodeSize(true);
			} else if (mode.equalsIgnoreCase("duplication")) {
				individualMode.setDuplication(true);
			} else if (mode.equalsIgnoreCase("complexity")) {
				individualMode.setComplexity(true);
			} else if (mode.equalsIgnoreCase("sonarjava")) {
				individualMode.setSonarJava(true);
			} else if (mode.equalsIgnoreCase("pmd")) {
				individualMode.setPmd(true);
			} else if (mode.equalsIgnoreCase("findbugs")) {
				individualMode.setFindBugs(true);
			} else if (mode.equalsIgnoreCase("findsecbugs")) {
				individualMode.setFindSecBugs(true);
			} else if (mode.equalsIgnoreCase("webresource")) {
				individualMode.setWebResource(true);
			} else if (mode.equalsIgnoreCase("dependency")) {
				individualMode.setDependency(true);
			} else if (mode.equalsIgnoreCase("unusedcode")) {
				individualMode.setUnusedCode(true);
			} else {
				throw new IllegalArgumentException(
						"'mode' option can only have 'code-size', 'duplication', 'complexity', 'sonarjava', 'pmd', 'findbugs', 'findsecbugs', 'webresource', 'dependency', and 'unusedcode'");
			}
		}
	}

	private void help() {
		HelpFormatter formatter = new HelpFormatter();

		formatter.setWidth(120);
		formatter.setOptionComparator(null); // no order

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

	public String getRuleSetFileForFindBugs() {
		return ruleSetFileForFindBugs;
	}

	public String getRuleSetFileForSonar() {
		return ruleSetFileForSonar;
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

	public OutputFileFormat getFormat() {
		return format;
	}

	public String getIncludes() {
		return includes;
	}

	public String getExcludes() {
		return excludes;
	}

	public IndividualMode getIndividualMode() {
		return individualMode;
	}

	public String getAnalysisMode() {
		return analysisMode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getInstanceKey() {
		if ("".equals(instanceKey)) {
			LOGGER.warn("No instance key!!!");
		}
		return instanceKey;
	}

	public void setInstanceKey(String instanceKey) {
		this.instanceKey = instanceKey;
	}

	public boolean isDetailAnalysis() {
		return detailAnalysis;
	}

	public void setDetailAnalysis(boolean detailAnalysis) {
		this.detailAnalysis = detailAnalysis;
	}

	public boolean isSeperatedOutput() {
		return seperatedOutput;
	}

	public void setSeperatedOutput(boolean seperatedOutput) {
		this.seperatedOutput = seperatedOutput;
	}
}
