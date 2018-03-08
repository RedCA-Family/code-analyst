package com.samsungsds.analyst.code.api;

public class ResultInfo {
	private String outputFile;
	private String duplicationFile;
	private String complexityFile;
	private String pmdFile;
	private String findBugsFile;
	private String findSecBugsFile;
	private String sonarJavaFile;
	private String webResourceFile;
	
	public String getOutputFile() {
		return outputFile;
	}
	
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	
	public String getDuplicationFile() {
		return duplicationFile;
	}
	
	public void setDuplicationFile(String duplicationFile) {
		this.duplicationFile = duplicationFile;
	}
	
	public String getComplexityFile() {
		return complexityFile;
	}
	
	public void setComplexityFile(String complexityFile) {
		this.complexityFile = complexityFile;
	}
	
	public String getPmdFile() {
		return pmdFile;
	}
	
	public void setPmdFile(String pmdFile) {
		this.pmdFile = pmdFile;
	}
	
	public String getFindBugsFile() {
		return findBugsFile;
	}
	
	public void setFindBugsFile(String findBugsFile) {
		this.findBugsFile = findBugsFile;
	}
	
	public String getFindSecBugsFile() {
		return findSecBugsFile;
	}
	
	public void setFindSecBugsFile(String findSecBugsFile) {
		this.findSecBugsFile = findSecBugsFile;
	}

	public String getSonarJavaFile() {
		return sonarJavaFile;
	}

	public void setSonarJavaFile(String sonarJavaFile) {
		this.sonarJavaFile = sonarJavaFile;
	}

	public String getWebResourceFile() {
		return webResourceFile;
	}

	public void setWebResourceFile(String webResourceFile) {
		this.webResourceFile = webResourceFile;
	}
}
