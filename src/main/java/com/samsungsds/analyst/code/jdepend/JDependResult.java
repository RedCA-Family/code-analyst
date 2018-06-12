package com.samsungsds.analyst.code.jdepend;

import java.io.Serializable;

import com.samsungsds.analyst.code.util.CSVFileResult;

public class JDependResult implements Serializable, CSVFileResult {
	private static final long serialVersionUID = -6859698000616986124L;
	
	private String acyclicDependencies;
	
	public JDependResult(String acyclicDependencies) {
		this.acyclicDependencies = acyclicDependencies;
	}
	
	public JDependResult() {
		// default constructor (CSV)
		// column : acyclicDependencies
	}

	@Override
	public int getColumnSize() {
		return 1;
	}

	@Override
	public String getDataIn(int columnIndex) {
		switch (columnIndex) {
		case 0 : return acyclicDependencies;
		default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	@Override
	public void setDataIn(int columnIndex, String data) {
		switch (columnIndex) {
		case 0 : acyclicDependencies = data; break;
		default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	public String getAcyclicDependencies() {
		return acyclicDependencies;
	}

	public void setAcyclicDependencies(String acyclicDependencies) {
		this.acyclicDependencies = acyclicDependencies;
	}

	@Override
	public String toString() {
		return acyclicDependencies;
	}
}
