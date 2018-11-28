package com.samsungsds.analyst.code.ckmetrics;

public interface CkMetricsAnalysis {
    void setTarget(String directory);

    void run(String instanceKey);
}
