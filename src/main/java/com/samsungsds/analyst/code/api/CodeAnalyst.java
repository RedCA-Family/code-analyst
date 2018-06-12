package com.samsungsds.analyst.code.api;

public interface CodeAnalyst {
	void addProgressObserver(ProgressObserver observer);
	void deleteProgressObserver(ProgressObserver observer);
	
	String analyze(String where, ArgumentInfo argument, TargetFileInfo targetFile);
	
	ResultInfo analyzeWithSeparatedResult(String where, ArgumentInfo argument, TargetFileInfo targetFile);

	String analyzeWebResource(String where, WebArgumentInfo webArgument, WebTargetFileInfo webTargetFile, boolean includeCssAndHtml);
}
