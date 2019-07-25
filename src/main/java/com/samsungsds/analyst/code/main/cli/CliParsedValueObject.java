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
package com.samsungsds.analyst.code.main.cli;

import com.samsungsds.analyst.code.main.Constants;
import com.samsungsds.analyst.code.main.IndividualMode;
import com.samsungsds.analyst.code.main.MeasurementMode;
import com.samsungsds.analyst.code.main.result.OutputFileFormat;

import java.io.File;

public class CliParsedValueObject {
    private String language = "java";
    private String projectBaseDir = ".";
    private String src = "src" + File.separator  + "main" + File.separator  + "java";
    private String binary = "target" + File.separator + "classes";
    private String library = "";
    private boolean debug = false;
    private String encoding = "UTF-8";
    private String javaVersion = "1.8";

    private String ruleSetFileForPMD = "";
    private String ruleSetFileForFindBugs = "";
    private String ruleSetFileForSonar = "";
    private String ruleSetFileForCheckStyle = "";

    private String output = "";
    private OutputFileFormat format = OutputFileFormat.TEXT;

    private String timeout = "6000"; // second (100 minutes)

    private MeasurementMode mode = MeasurementMode.DefaultMode;
    private String classForCCMeasurement = "";

    private String webapp = "";

    private String includes = "";
    private String excludes = "";

    private IndividualMode individualMode = new IndividualMode();

    private String analysisMode = Constants.DEFAULT_ANALYSIS_MODE;

    private String errorMessage = "";

    private String instanceKey = "";

    private boolean detailAnalysis = false;

    private boolean seperatedOutput = false;

    private boolean saveCatalog = false;

    private boolean tokenBased = false;

    private int minimumTokens = 100;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProjectBaseDir() {
        return projectBaseDir;
    }

    public void setProjectBaseDir(String projectBaseDir) {
        this.projectBaseDir = projectBaseDir;
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

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
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

    public String getRuleSetFileForSonar() {
        return ruleSetFileForSonar;
    }

    public void setRuleSetFileForSonar(String ruleSetFileForSonar) {
        this.ruleSetFileForSonar = ruleSetFileForSonar;
    }

    public String getRuleSetFileForCheckStyle() {
        return ruleSetFileForCheckStyle;
    }

    public void setRuleSetFileForCheckStyle(String ruleSetFileForCheckStyle) {
        this.ruleSetFileForCheckStyle = ruleSetFileForCheckStyle;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public OutputFileFormat getFormat() {
        return format;
    }

    public void setFormat(OutputFileFormat format) {
        this.format = format;
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

    public void setMode(MeasurementMode mode) {
        this.mode = mode;
    }

    public String getClassForCCMeasurement() {
        return classForCCMeasurement;
    }

    public void setClassForCCMeasurement(String classForCCMeasurement) {
        this.classForCCMeasurement = classForCCMeasurement;
    }

    public String getWebapp() {
        return webapp;
    }

    public void setWebapp(String webapp) {
        this.webapp = webapp;
    }

    public String getIncludes() {
        return includes;
    }

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    public String getExcludes() {
        return excludes;
    }

    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }

    public IndividualMode getIndividualMode() {
        return individualMode;
    }

    public void setIndividualMode(IndividualMode individualMode) {
        this.individualMode = individualMode;
    }

    public String getAnalysisMode() {
        return analysisMode;
    }

    public void setAnalysisMode(String analysisMode) {
        this.analysisMode = analysisMode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getInstanceKey() {
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

    public boolean isSaveCatalog() {
        return saveCatalog;
    }

    public void setSaveCatalog(boolean saveCatalog) {
        this.saveCatalog = saveCatalog;
    }

    public boolean isTokenBased() {
        return tokenBased;
    }

    public void setTokenBased(boolean tokenBased) {
        this.tokenBased = tokenBased;
    }

    public int getMinimumTokens() {
        return minimumTokens;
    }

    public void setMinimumTokens(int minimumTokens) {
        this.minimumTokens = minimumTokens;
    }
}
