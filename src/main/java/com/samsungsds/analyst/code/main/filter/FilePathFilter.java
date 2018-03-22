package com.samsungsds.analyst.code.main.filter;

public interface FilePathFilter {
	String getFilterName();
	boolean matched(String filePath);
	boolean matched(String filePath, boolean withoutFilename);

	String getSrcPrefix();
}
