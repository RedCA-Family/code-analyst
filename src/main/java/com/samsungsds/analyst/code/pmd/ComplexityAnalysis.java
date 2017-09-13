package com.samsungsds.analyst.code.pmd;

public interface ComplexityAnalysis {
	void addOption(String option, String value);
	
	void run(String instanceKey);
}
