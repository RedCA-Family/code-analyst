package com.samsungsds.analyst.code.api;

public interface AbstractFileInfo {
    String[] getFiles();
    boolean isPackageExpression();
    default boolean isWebResourceAnalysis() {
        return false;
    }
}
