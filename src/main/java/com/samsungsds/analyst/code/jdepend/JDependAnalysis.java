package com.samsungsds.analyst.code.jdepend;

public interface JDependAnalysis {
	void addIncludePackage(String packageName);
	
	void setTarget(String directory);
	
	void run();
}
