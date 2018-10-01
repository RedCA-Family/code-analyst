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

public class WebArgumentInfo {
    private String project;					// project base directory
    private boolean debug = false;
    private String encoding = "UTF-8";		// source file encoding
    private String sonarRuleFile;			// SonarQube Issue exclude xml file (if omitted, all SDS Standard Rules included)
    private int timeout = 10 * 60 * 10;		// 100 minutes
    private String exclude;					// exclude pattern(Ant-style) with comma separated. (eg: com/sds/**/*VO.java)
    private String webapp;					// webapp directory

    private boolean detailAnalysis = false;	// Detail Analysis mode

    private boolean saveCatalog = false;	// Save target file list

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
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

    public String getSonarRuleFile() {
        return sonarRuleFile;
    }

    public void setSonarRuleFile(String sonarRuleFile) {
        this.sonarRuleFile = sonarRuleFile;
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

    public String getWebapp() {
        return webapp;
    }

    public void setWebapp(String webapp) {
        this.webapp = webapp;
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
}
