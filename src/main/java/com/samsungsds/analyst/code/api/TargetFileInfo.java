package com.samsungsds.analyst.code.api;

import java.util.ArrayList;
import java.util.List;

public class TargetFileInfo {
	private String packageName;
	private boolean includeSubPackage = true;
	private List<String> fileList = new ArrayList<>();
	
	public TargetFileInfo(String packageName) {
		this.packageName = packageName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public boolean isIncludeSubPackage() {
		return includeSubPackage;
	}
	
	public void setIncludeSubPackage(boolean includeSubPackage) {
		this.includeSubPackage = includeSubPackage;
	}
	
	public void addFile(String file) {
		fileList.add(file);
	}
	
	public String[] getFiles() {
		return fileList.toArray(new String[0]);
	}
}