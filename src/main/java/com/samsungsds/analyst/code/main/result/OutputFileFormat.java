package com.samsungsds.analyst.code.main.result;

public enum OutputFileFormat {
	TEXT("text"), JSON("json"), NONE("none");
	
	private String format;
	OutputFileFormat(String format) {
		this.format = format.toLowerCase();
	}
	
	String format() {
		return format;
	}
}
