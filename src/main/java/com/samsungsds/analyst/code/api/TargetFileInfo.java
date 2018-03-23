package com.samsungsds.analyst.code.api;

import com.samsungsds.analyst.code.main.filter.FilePathAbstractFilter;

import java.util.ArrayList;
import java.util.List;

public class TargetFileInfo {
	private boolean includeSubPackage = true;
	private List<String> fileList = new ArrayList<>();

	public boolean isIncludeSubPackage() {
		return includeSubPackage;
	}

	public void setIncludeSubPackage(boolean includeSubPackage) {
		this.includeSubPackage = includeSubPackage;
	}

	public void addPackage(String packageName) {
        if (!packageName.equals("")) {
            packageName += "/";
        }

		if (includeSubPackage) {
			fileList.add(FilePathAbstractFilter.FIXED_PREFIX + packageName + "**/*.java");
		} else {
			fileList.add(FilePathAbstractFilter.FIXED_PREFIX + packageName + "*.java");
		}
	}

	public void addFile(String packageName, String file) {
        if (!packageName.equals("")) {
            packageName += "/";
        }
		fileList.add(FilePathAbstractFilter.FIXED_PREFIX + "**/" + packageName + file);
	}

	public String[] getFiles() {
		return fileList.toArray(new String[0]);
	}
}