package com.samsungsds.analyst.code.checkstyle;

public interface CheckStyleAnalysis {
    void setProjectBaseDir(String projectBaseDir);

    void setSourceDirectories(String srcDirectories);

    void addOption(String option, String value);

    void run(String instanceKey);
}
