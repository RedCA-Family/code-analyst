package com.samsungsds.analyst.code.api;

import com.samsungsds.analyst.code.main.filter.FilePathAbstractFilter;

import java.util.ArrayList;
import java.util.List;

public class WebTargetFileInfo implements AbstractFileInfo {
    private boolean includeSubDirectory = true;
    private List<String> fileList = new ArrayList<>();

    public boolean isIncludeSubDirectory() {
        return includeSubDirectory;
    }

    public void setIncludeSubDirectory(boolean includeSubDirectory) {
        this.includeSubDirectory = includeSubDirectory;
    }

    public void addDirectory(String directory) {
        if (!directory.equals("")) {
            directory += "/";
        }

        if (includeSubDirectory) {
            fileList.add(FilePathAbstractFilter.FIXED_PREFIX + directory + "**/*.*");
        } else {
            fileList.add(FilePathAbstractFilter.FIXED_PREFIX + directory + "*.*");
        }
    }

    public void addFile(String filePath) {
        fileList.add(FilePathAbstractFilter.FIXED_PREFIX + filePath);
    }

    @Override
    public String[] getFiles() {
        return fileList.toArray(new String[0]);
    }

    @Override
    public boolean isPackageExpression() {
        return false;
    }

    @Override
    public boolean isWebResourceAnalysis() {
        return true;
    }
}
