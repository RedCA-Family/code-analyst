package com.samsungsds.analyst.code.pmd;

import java.io.Serializable;

public class TargetPackageResult implements Serializable {

	private static final long serialVersionUID = 8264467761447236737L;
	
	protected String packageName;
	protected String fileName;

	public String getPackageName() {
		return packageName;
	}

	public String getFileName() {
		return fileName;
	}

}