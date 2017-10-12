package com.samsungsds.analyst.code.jdepend;

import java.io.Serializable;

import com.samsungsds.analyst.code.util.CSVFileResult;

public class JDependResult implements Serializable, CSVFileResult {
	
	private static final long serialVersionUID = -6859698000616986124L;
	
	private String acyclicDependecies;
	
	public JDependResult(String acyclicDependecies) {
		this.acyclicDependecies = acyclicDependecies;
	}
	
	public JDependResult() {
		// default constructor (CSV)
		// column : acyclicDependecies
	}

	@Override
	public int getColumnSize() {
		return 1;
	}

	@Override
	public String getDataIn(int columnIndex) {
		switch (columnIndex) {
		case 0 : return acyclicDependecies;
		default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	@Override
	public void setDataIn(int columnIndex, String data) {
		switch (columnIndex) {
		case 0 : acyclicDependecies = data; break;
		default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	public String getAcyclicDependecies() {
		return acyclicDependecies;
	}

	public void setAcyclicDependecies(String acyclicDependecies) {
		this.acyclicDependecies = acyclicDependecies;
	}
}
