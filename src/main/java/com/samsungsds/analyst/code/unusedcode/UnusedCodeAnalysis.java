package com.samsungsds.analyst.code.unusedcode;

public interface UnusedCodeAnalysis {
	
	void setProjectBaseDir(String directory);
	void setTargetSrc(String directory);
	void setTargetBinary(String directory);
	void run(String instanceKey);
}
