package com.samsungsds.analyst.code.findbugs;

public interface FindBugsAnalysis {
	void addOption(String option, String value);
	
	void setTarget(String directory);
	
	void run(String instanceKey);
}
