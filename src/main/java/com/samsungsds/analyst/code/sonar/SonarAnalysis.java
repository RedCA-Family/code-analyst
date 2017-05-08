package com.samsungsds.analyst.code.sonar;

public interface SonarAnalysis {
	void addProperty(String key, String value);
	
	void run();
}
