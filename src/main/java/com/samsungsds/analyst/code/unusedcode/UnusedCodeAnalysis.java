package com.samsungsds.analyst.code.unusedcode;

public interface UnusedCodeAnalysis {
	void addOption(String option, String value);
	
	void run(String instanceKey);
}
