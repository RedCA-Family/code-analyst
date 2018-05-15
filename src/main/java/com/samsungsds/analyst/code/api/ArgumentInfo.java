package com.samsungsds.analyst.code.api;

public class ArgumentInfo {
	private String project;					// project base directory
	private String src;						// source directory (relative path of project base dir.)
	private String binary;					// binary directory (relative path of project base dir.)
	private boolean debug = false;			
	private String encoding = "UTF-8";		// source file encoding
	private String javaVersion = "1.8";		// java source version
	private String pmdRuleFile;				// PMD ruleset xml file (if omitted, SDS Standard Ruleset used)
	private String findBugsRuleFile;		// Findbugs ruleset xml file (if omitted, SDS Standard Ruleset used)
	private String sonarRuleFile;			// SonarQube Issue exclude xml file (if omitted, all SDS Standard Rules included)
	private int timeout = 10 * 60 * 10;		// 100 minutes
	private String exclude;					// exclude pattern(Ant-style) with comma separated. (eg: com/sds/**/*VO.java)
	private String webapp;					// webapp directory
	
	private AnalysisMode mode;				// code-size,duplication,complexity,sonarjava,pmd,findbugs,findsecbugs,javascript,css,html,dependency,unused

	private boolean detailAnalysis = false;	// Detail Analysis mode

	private boolean saveCatalog = false;	// Save target file list

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

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
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
}
