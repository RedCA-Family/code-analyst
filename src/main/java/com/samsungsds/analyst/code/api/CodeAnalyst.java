package com.samsungsds.analyst.code.api;

public interface CodeAnalyst {
	void addProgressObserver(ProgressObserver observer);
	void deleteProgressObserver(ProgressObserver observer);
	
	String analyze(String where, ArgumentInfo argument, TargetFileInfo targetFile);
	
	ResultInfo analyzeWithSeperatedResult(String where, ArgumentInfo argument, TargetFileInfo targetFile);
}
