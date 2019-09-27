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
package com.samsungsds.analyst.code.api;

public class ArgumentInfo {
	private Language language = Language.JAVA;	// Language ('Java' or 'JavaScript')

	private String project;						// project base directory
	private String src;							// source directory (relative path of project base dir.)
	private String binary;						// binary directory (relative path of project base dir.)
	private boolean debug = false;
	private String encoding = "UTF-8";			// source file encoding
	private String javaVersion = "1.8";			// java source version
	private String pmdRuleFile;					// PMD ruleset xml file (if omitted, SDS Standard Ruleset used)
	private String findBugsRuleFile;			// Findbugs ruleset xml file (if omitted, SDS Standard Ruleset used)
	private String sonarRuleFile;				// SonarQube Issue exclude xml file (if omitted, all SDS Standard Rules included)
    private String checkStyleRuleFile;          // CheckStyle configuration xml file (if omitted, our own standard ruleset used)
	private int timeout = 10 * 60 * 10;			// 100 minutes
	private String include;						// include pattern(Ant-Style) with comma separated. (eg: com/sds/**/*VO.java)
	private String exclude;						// exclude pattern(Ant-style) with comma separated. (eg: com/sds/**/*VO.java)
	private String webapp;						// webapp directory

	private AnalysisMode mode;					// (Java) code-size,duplication,complexity,sonarjava,pmd,findbugs,findsecbugs,javascript,css,html,dependency,unused,ckmetrics
												// (JavaScript) code-size,duplication,complexity,sonarjs
                                                // (CSharp) code-size,duplication,complexity,sonarcsharp
                                                // (Python) code-size,duplication,complexity,sonarpython
	private boolean detailAnalysis = false;		// Detail Analysis mode

	private boolean saveCatalog = false;		// Save target file list

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
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

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getPmdRuleFile() {
		return pmdRuleFile;
	}

	public void setPmdRuleFile(String pmdRuleFile) {
		this.pmdRuleFile = pmdRuleFile;
	}

	public String getFindBugsRuleFile() {
		return findBugsRuleFile;
	}

	public void setFindBugsRuleFile(String findBugsRuleFile) {
		this.findBugsRuleFile = findBugsRuleFile;
	}

	public void setSonarRuleFile(String sonarRuleFile) {
		this.sonarRuleFile = sonarRuleFile;
	}

	public String getSonarRuleFile() {
		return sonarRuleFile;
	}

    public String getCheckStyleRuleFile() {
        return checkStyleRuleFile;
    }

    public void setCheckStyleRuleFile(String checkStyleRuleFile) {
        this.checkStyleRuleFile = checkStyleRuleFile;
    }

    public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getInclude() {
		return include;
	}

	public void setInclude(String include) {
        if (include.startsWith("@")) {
            throw new IllegalArgumentException("'@file' feature not available in API mode");
        }
		this.include = include;
	}

	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
	    if (exclude.startsWith("@")) {
	        throw new IllegalArgumentException("'@file' feature not available in API mode");
        }
		this.exclude = exclude;
	}

	public AnalysisMode getMode() {
		return mode;
	}

	public void setMode(AnalysisMode mode) {
		this.mode = mode;
	}

	public boolean isDetailAnalysis() {
		return detailAnalysis;
	}

	public void setDetailAnalysis(boolean detailAnalysis) {
		this.detailAnalysis = detailAnalysis;
	}

	public boolean isSaveCatalog() {
		return saveCatalog;
	}

	public void setSaveCatalog(boolean saveCatalog) {
		this.saveCatalog = saveCatalog;
	}

	public String getWebapp() {
		return webapp;
	}

	public void setWebapp(String webapp) {
		this.webapp = webapp;
	}

	public void setSrc(String[] srcDirectories) {
		this.src = String.join(",", srcDirectories);
	}

	public void setBinary(String[] binaryDirectories) {
		this.binary = String.join(",", binaryDirectories);
	}
}
