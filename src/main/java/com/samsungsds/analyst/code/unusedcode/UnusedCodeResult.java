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
package com.samsungsds.analyst.code.unusedcode;

import com.google.gson.annotations.Expose;
import com.samsungsds.analyst.code.util.CSVFileResult;

import java.io.Serializable;

public class UnusedCodeResult implements Serializable, CSVFileResult {

	private static final long serialVersionUID = 2065765915311874437L;

	@Expose
	private String packageName = "";

	@Expose
	private String className;

	@Expose
	private String name;

	@Expose
	private int line;

	@Expose
	private String type;

	@Expose
	private String description;

	public UnusedCodeResult() {
		// default constructor (CSV)
	}

	@Override
	public int getColumnSize() {
		return 6;
	}

	@Override
	public String getDataIn(int columnIndex) {
		switch (columnIndex) {
			case 0 : return packageName;
			case 1 : return className;
			case 2 : return name;
			case 3 : return String.valueOf(line);
			case 4 : return type;
			case 5 : return description;
			default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	@Override
	public void setDataIn(int columnIndex, String data) {
		switch (columnIndex) {
			case 0 : packageName = data; break;
			case 1 : className = data; break;
			case 2 : name = data; break;
			case 3 : line = Integer.parseInt(data); break;
			case 4 : type = data; break;
			case 5 : description = data; break;
			default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}
	
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
