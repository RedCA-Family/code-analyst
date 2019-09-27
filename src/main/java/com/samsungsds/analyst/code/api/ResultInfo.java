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

public class ResultInfo {
	private String outputFile;
	private String duplicationFile;
	private String complexityFile;
	private String pmdFile;
	private String findBugsFile;
	private String findSecBugsFile;
	private String sonarJavaFile;
	private String sonarOtherFile;
	private String webResourceFile;
	private String ckMetricsFile;
	private String checkStyleFile;

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

	public String getSonarJsFile() {
		return sonarOtherFile;
	}

	public void setSonarOtherFile(String sonarJsFile) {
		this.sonarOtherFile = sonarJsFile;
	}

	public String getSonarCSharpFile() {
	    return sonarOtherFile;
    }

    public String getSonarPythonFile() {
	    return sonarOtherFile;
    }

	public String getWebResourceFile() {
		return webResourceFile;
	}

	public void setWebResourceFile(String webResourceFile) {
		this.webResourceFile = webResourceFile;
	}

	public String getCkMetricsFile() {
		return ckMetricsFile;
	}

	public void setCkMetricsFile(String ckMetricsFile) {
		this.ckMetricsFile = ckMetricsFile;
	}

    public String getCheckStyleFile() {
        return checkStyleFile;
    }

    public void setCheckStyleFile(String checkStyleFile) {
        this.checkStyleFile = checkStyleFile;
    }
}
