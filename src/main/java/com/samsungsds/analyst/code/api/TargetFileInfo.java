package com.samsungsds.analyst.code.api;

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
		if (includeSubPackage) {
			fileList.add(packageName + "/**/*.java"); 
		} else {
			fileList.add(packageName + "/*.java");
		}
	}
	
	public void addFile(String packageName, String file) {
		fileList.add(packageName + "/" + file);
	}
	
	public String[] getFiles() {
		return fileList.toArray(new String[0]);
	}
}