package com.samsungsds.analyst.code.api.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.api.AnalysisMode;
import com.samsungsds.analyst.code.api.AnalysisProgress;
import com.samsungsds.analyst.code.api.ArgumentInfo;
import com.samsungsds.analyst.code.api.CodeAnalyst;
import com.samsungsds.analyst.code.api.ProgressObserver;
import com.samsungsds.analyst.code.api.TargetFileInfo;
import com.samsungsds.analyst.code.main.App;
import com.samsungsds.analyst.code.main.CliParser;

public class CodeAnalystImpl implements CodeAnalyst {
	private static final Logger LOGGER = LogManager.getLogger(CodeAnalystImpl.class);
	
	private List<ProgressObserver> observerList = new ArrayList<>();
	
	private String outputFilePath;

	@Override
	public void addProgressObserver(ProgressObserver observer) {
		observerList.add(observer);
	}

	@Override
	public void deleteProgressObserver(ProgressObserver observer) {
		observerList.remove(observer);
	}

	@Override
	public String analyze(String where, ArgumentInfo argument, TargetFileInfo targetFile) {
		
		checkDirectoryAndArgument(where, argument);
		
		String[] arguments = getArguments(where, argument, targetFile);

		System.out.println("* Arguments : " + getArgumentsString(arguments));
		
		CliParser cli = new CliParser(arguments);
		
		cli.setInstanceKey(getUniqueId());
		
		LOGGER.info("Instance Key : {}", cli.getInstanceKey());
    	
    	App app = new App();
    	
    	for (ProgressObserver observer : observerList) {
    		app.addProgressObserver(observer);
    	}
    	
    	try {
    		app.process(cli);
    	} catch (Throwable ex) {
    		LOGGER.error("Error", ex);
    		throw ex;
    	} finally {
    		app.cleanup(cli.getInstanceKey());
    	}
    	
    	if (app.hasParsingError()) {
    		throw new RuntimeException(app.getParsingErrorMessage());
    	} else {
    		return outputFilePath;
    	}
	}

	private String getUniqueId() {
		return UUID.randomUUID().toString().toUpperCase();
	}

	private String getArgumentsString(String[] arguments) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < arguments.length; i++) {
			if (i == 0) {
				builder.append(checkSpaceOrAsterisk(arguments[i]));
			} else {
				builder.append(" ").append(checkSpaceOrAsterisk(arguments[i]));
			}
		}
		
		return builder.toString();
	}

	private String checkSpaceOrAsterisk(String string) {
		if (string.contains(" ") || string.contains("*")) {
			return "\"" + string + "\"";
		}
		
		return string;
	}

	private String[] getArguments(String where, ArgumentInfo argument, TargetFileInfo targetFile) {
		List<String> argumentList = new ArrayList<>();
		
		argumentList.add("--project");
		argumentList.add(argument.getProject());
		
		argumentList.add("--src");
		argumentList.add(argument.getSrc());
		
		argumentList.add("--binary");
		argumentList.add(argument.getBinary());
		
		if (argument.isDebug()) {
			argumentList.add("--debug");
		}
		
		argumentList.add("--encoding");
		argumentList.add(argument.getEncoding());
		
		argumentList.add("--java");
		argumentList.add(argument.getJavaVersion());
		
		if (isValidated(argument.getPmdRuleFile())) {
			argumentList.add("-pmd");
			argumentList.add(argument.getPmdRuleFile());
		}
		
		if (isValidated(argument.getFindBugsRuleFile())) {
			argumentList.add("-findbugs");
			argumentList.add(argument.getFindBugsRuleFile());
		}
		
		argumentList.add("--output");
		argumentList.add(outputFilePath = getOutputFile(where, "json"));
		
		argumentList.add("--format");
		argumentList.add("json");
		
		argumentList.add("--timeout");
		argumentList.add(Integer.toString(argument.getTimeout()));
		
		argumentList.add("-include");
		argumentList.add(getIncludeString(targetFile));
		 
		
		if (isValidated(argument.getExclude())) {
			argumentList.add("-exclude");
			argumentList.add(argument.getExclude());
		}
		
		argumentList.add("--mode");
		argumentList.add(getModeParameter(argument.getMode()));
		
		return argumentList.toArray(new String[0]);
	}
	
	private String getIncludeString(TargetFileInfo targetFile) {
		StringBuilder builder = new StringBuilder();
		
		for (String file : targetFile.getFiles()) {
			if (builder.length() != 0) {
				builder.append(",");
			}
			builder.append(file.replaceAll("\\.java", "").replaceAll("\\.", "/") + ".java");
		}
		
		System.out.println("* Target file patterns : " + builder.toString());
		
		return builder.toString();
	}

	private void checkDirectoryAndArgument(String where, ArgumentInfo argument) {
		File dir = new File(where);
		
		if (!dir.exists() || !dir.isDirectory()) {
			throw new IllegalArgumentException("Check target directory : " + where);
		} 
		
		if (isNotValidated(argument.getProject())) {
			throw new IllegalArgumentException("Project directory is needed...");
		}
		
		if (isNotValidated(argument.getSrc())) {
			throw new IllegalArgumentException("Source directory is needed...");
		}
		
		if (isNotValidated(argument.getBinary())) {
			throw new IllegalArgumentException("Binary directory is needed...");
		}
		
		if (argument.getMode() == null) {
			throw new IllegalArgumentException("Analysis Mode is needed...");
		}
	}
	
	private String getOutputFile(String where, String ext) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "result-" + format.format(new Date()) + "." + ext;
		
		File directory = new File(where);
		
		File resultFile = new File(directory, fileName);
		
		try {
			return resultFile.getCanonicalPath();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	private String getModeParameter(AnalysisMode mode) {
		StringBuilder parameter = new StringBuilder();
		
		if (mode.isCodeSize()) {
			addAnalysisItem(parameter, "code-size");
		}
		
		if (mode.isDuplication()) {
			addAnalysisItem(parameter, "duplication");
		}
		
		if (mode.isComplexity()) {
			addAnalysisItem(parameter, "complexity");
		}
		
		if (mode.isPmd()) {
			addAnalysisItem(parameter, "pmd");
		}
		
		if (mode.isFindBugs()) {
			addAnalysisItem(parameter, "findbugs");
		}
		
		if (mode.isFindSecBugs()) {
			addAnalysisItem(parameter, "findsecbugs");
		}
		
		if (mode.isDependency()) {
			addAnalysisItem(parameter, "dependency");
		}
		
		return parameter.toString();
	}

	private void addAnalysisItem(StringBuilder parameter, String modeString) {
		if (parameter.length() != 0) {
			parameter.append(",");
		}
		parameter.append(modeString);
	}

	protected void notifyObservers(AnalysisProgress progress) {
		for (ProgressObserver observer : observerList) {
			observer.informProgress(progress);
		}
	}
	
	private boolean isNotValidated(String str) {
		if (str == null || str.trim().equals("")) {
			return true;
		}
		
		return false;
	}
	
	private boolean isValidated(String str) {
		if (str != null && !str.trim().equals("")) {
			return true;
		}
		
		return false;
	}
}
