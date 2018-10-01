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
package com.samsungsds.analyst.code.api.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.samsungsds.analyst.code.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.main.App;
import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

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
		return analyze(where, argument, targetFile, false);
	}
	
	public String analyze(String where, ArgumentInfo argument, AbstractFileInfo targetFile, boolean withSeperatedOutput) {
		checkDirectoryAndArgument(where, argument, targetFile.isWebResourceAnalysis());
		
		String[] arguments = getArguments(where, argument, targetFile, withSeperatedOutput);

		System.out.println("* Arguments : " + getArgumentsString(arguments));
		
		if (withSeperatedOutput) {
			System.out.println(" - with seperated output option");
		}
		
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

	@Override
	public ResultInfo analyzeWithSeparatedResult(String where, ArgumentInfo argument, TargetFileInfo targetFile) {
		String resultFile = analyze(where, argument, targetFile, true);

		ResultInfo info = new ResultInfo();
		info.setOutputFile(resultFile);

		String fileWithoutExt = IOAndFileUtils.getFilenameWithoutExt(new File(resultFile));

		info.setDuplicationFile(fileWithoutExt + "-duplication.json");
		info.setComplexityFile(fileWithoutExt + "-complexity.json");
		info.setPmdFile(fileWithoutExt + "-pmd.json");
		info.setFindBugsFile(fileWithoutExt + "-findbugs.json");
		info.setFindSecBugsFile(fileWithoutExt + "-findsecbugs.json");
		info.setSonarJavaFile(fileWithoutExt + "-sonarjava.json");
		info.setWebResourceFile(fileWithoutExt + "-webresource.json");

		return info;
	}

	@Override
	public String analyzeWebResource(String where, WebArgumentInfo webArgument, WebTargetFileInfo webTargetFile, boolean includeCssAndHtml) {
		ArgumentInfo argument = getArgumentInfo(webArgument);

		argument.setMode(settingAnalysisMode(includeCssAndHtml));

		return analyze(where, argument, webTargetFile, false);
	}

	private ArgumentInfo getArgumentInfo(WebArgumentInfo webArgument) {
		ArgumentInfo argument = new ArgumentInfo();

		argument.setProject(webArgument.getProject());
		argument.setSrc("");
		argument.setBinary("");
		argument.setDebug(webArgument.isDebug());
		argument.setEncoding(webArgument.getEncoding());
		argument.setSonarRuleFile(webArgument.getSonarRuleFile());
		argument.setTimeout(webArgument.getTimeout());
		argument.setExclude(webArgument.getExclude());
		argument.setWebapp(webArgument.getWebapp());
		argument.setDetailAnalysis(webArgument.isDetailAnalysis());
		argument.setSaveCatalog(webArgument.isSaveCatalog());

		return argument;
	}

	private AnalysisMode settingAnalysisMode(boolean includeCssAndHtml) {
		AnalysisMode mode = new AnalysisMode();
		mode.setCodeSize(false);
		mode.setDuplication(false);
		mode.setComplexity(false);
		mode.setSonarJava(false);
		mode.setPmd(false);
		mode.setFindBugs(false);
		mode.setFindSecBugs(false);
		mode.setJavascript(true);
		if (includeCssAndHtml) {
			mode.setCss(true);
			mode.setHtml(true);
		} else {
			mode.setCss(false);
			mode.setHtml(false);
		}
		mode.setDependency(false);
		mode.setUnusedCode(false);

		return mode;
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

	private String[] getArguments(String where, ArgumentInfo argument, AbstractFileInfo targetFile, boolean withSeperatedOutput) {
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

		if (isValidated(argument.getSonarRuleFile())) {
			argumentList.add("-sonar");
			argumentList.add(argument.getSonarRuleFile());
		}
		
		argumentList.add("--output");
		argumentList.add(outputFilePath = getOutputFile(where, "json"));
		
		argumentList.add("--format");
		argumentList.add("json");
		
		argumentList.add("--timeout");
		argumentList.add(Integer.toString(argument.getTimeout()));

		String includeString;
		if (isValidated(argument.getInclude())) {
			includeString = String.join(",", argument.getInclude(), getIncludeString(targetFile));
		} else {
			includeString = getIncludeString(targetFile);
		}

		if (!includeString.equals("")) {
			argumentList.add("-include");
			argumentList.add(includeString);
		}
		
		if (isValidated(argument.getExclude())) {
			argumentList.add("-exclude");
			argumentList.add(argument.getExclude());
		}
		
		argumentList.add("--mode");
		argumentList.add(getModeParameter(argument.getMode(), isValidated(argument.getWebapp())));

		if (isValidated(argument.getWebapp())) {
			argumentList.add("--webapp");
			argumentList.add(argument.getWebapp());
		}
		
		if (withSeperatedOutput) {
			argumentList.add("-seperated");
		}

		if (argument.isDetailAnalysis()) {
			argumentList.add("--analysis");
		}

		if (argument.isSaveCatalog()) {
			argumentList.add("-catalog");
		}
		
		return argumentList.toArray(new String[0]);
	}
	
	private String getIncludeString(AbstractFileInfo targetFile) {
		StringBuilder builder = new StringBuilder();
		
		for (String file : targetFile.getFiles()) {
			if (builder.length() != 0) {
				builder.append(",");
			}
			if (targetFile.isPackageExpression()) {
				builder.append(file.replaceAll("\\.java", "").replaceAll("\\.", "/")).append(".java");
			} else {
				builder.append(file);
			}
		}
		
		System.out.println("* Target file patterns : " + builder.toString());
		
		return builder.toString();
	}

	private void checkDirectoryAndArgument(String where, ArgumentInfo argument, boolean isWebResourceAnalysis) {
		File dir = new File(where);
		
		if (!dir.exists() || !dir.isDirectory()) {
			throw new IllegalArgumentException("Check target directory : " + where);
		} 
		
		if (isNotValidated(argument.getProject())) {
			throw new IllegalArgumentException("Project directory is needed...");
		}

		if (isWebResourceAnalysis) {
			if (isNotValidated(argument.getWebapp())) {
				throw new IllegalArgumentException("webapp directory is needed...");
			}
		} else {
			if (isNotValidated(argument.getSrc())) {
				throw new IllegalArgumentException("Source directory is needed...");
			}

			if (isNotValidated(argument.getBinary())) {
				throw new IllegalArgumentException("Binary directory is needed...");
			}
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
	
	private String getModeParameter(AnalysisMode mode, boolean hasWebappAugument) {
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
		
		if (mode.isSonarJava()) {
			addAnalysisItem(parameter, "sonarjava");
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

		if (hasWebappAugument) {
			if (mode.isJavascript()) {
				addAnalysisItem(parameter, "javascript");
			}

			if (mode.isCss()) {
				addAnalysisItem(parameter, "css");
			}

			if (mode.isHtml()) {
				addAnalysisItem(parameter, "html");
			}
		}
		
		if (mode.isDependency()) {
			addAnalysisItem(parameter, "dependency");
		}
		
		if (mode.isUnusedCode()) {
			addAnalysisItem(parameter, "unusedcode");
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
