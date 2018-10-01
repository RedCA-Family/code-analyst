/*
Copyright 2018 Samsung SDS

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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
