package com.samsungsds.analyst.code.util;

public interface CSVFileResult {
	int getColumnSize();
	String getDataIn(int columnIndex);
	void setDataIn(int columnIndex, String data);
}
