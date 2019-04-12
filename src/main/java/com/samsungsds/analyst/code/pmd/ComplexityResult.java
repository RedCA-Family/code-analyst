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
package com.samsungsds.analyst.code.pmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.Expose;
import com.samsungsds.analyst.code.util.CSVFileResult;

public class ComplexityResult extends TargetPackageResult implements Serializable, CSVFileResult {

	private static final long serialVersionUID = -8409553726959660496L;

	private static final Logger LOGGER = LogManager.getLogger(ComplexityResult.class);
	
	@Expose
	private String path;
	@Expose
	private int line;
	@Expose
	private String methodName;
	@Expose
	private int complexity;

	public ComplexityResult() {
		// no-op
	}

	public ComplexityResult(String path, int line, String methodName, int complexity) {
		this.path = path;
		this.line = line;
		this.methodName = methodName;
		this.complexity = complexity;
	}

	@Override
	public int getColumnSize() {
		return 4;
	}

	@Override
	public String getDataIn(int columnIndex) {
		switch (columnIndex) {
		case 0 : return path;
		case 1 : return String.valueOf(line);
		case 2 : return methodName;
		case 3 : return String.valueOf(complexity);
		default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	@Override
	public void setDataIn(int columnIndex, String data) {
		switch (columnIndex) {
		case 0 : path = data; break;
		case 1 : line = Integer.parseInt(data); break;
		case 2 : methodName = data; break;
		case 3 : complexity = Integer.parseInt(data); break;
		default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}
	
	public String getPath() {
		return path;
	}
	
	public int getLine() {
		return line;
	}

	public String getMethodName() {
		return methodName;
	}

	public int getComplexity() {
		return complexity;
	}

	public static ComplexityResult convertComplexityResult(PmdResult result) {
		ComplexityResult converted = new ComplexityResult();

		converted.path = result.getPath();
		converted.fileName = result.getFile();
		
		converted.packageName = result.getPackageName();
		
		converted.line = result.getLine();
		
		Pattern p = Pattern.compile("^The method '(.+)' has a Modified Cyclomatic Complexity of ([0-9]+)\\.$");
		Matcher m = p.matcher(result.getDescription());
		
		if (m.find()) {
			converted.methodName = m.group(1);
			converted.complexity = Integer.parseInt(m.group(2));
		} else {
			Pattern p2 = Pattern.compile("^The constructor '(.+)' has a Modified Cyclomatic Complexity of ([0-9]+)\\.$");
			Matcher m2 = p2.matcher(result.getDescription());
			
			if (m2.find()) {
				converted.methodName = m2.group(1);
				converted.complexity = Integer.parseInt(m2.group(2));
			} else {
				return null;
			}
		}
		
		return converted;
	}
	
	public static List<ComplexityResult> convertComplexityResult(List<PmdResult> resultList) {
		List<ComplexityResult> list = new ArrayList<>();
		
		for (PmdResult result : resultList) {
			ComplexityResult converted = convertComplexityResult(result);
			
			if (converted != null) {
				LOGGER.debug("file : {}, method : {}, line : {}, complexity : {}", converted.fileName, converted.methodName, converted.line, converted.complexity);
				list.add(converted);
			}
		}
		
		return list;
	}
	
	public static void main(String[] args) {
		Pattern p = Pattern.compile("^The method '(.+)' has a Modified Cyclomatic Complexity of ([0-9]+)\\.$");
		
		Matcher m = p.matcher(" The method 'process' has a Modified Cyclomatic Complexity of 55.");
		
		if (m.find()) {
			System.out.println(m.group(1));
			System.out.println(m.group(2));
		} else {
			System.out.println("No group...");
		}
	}
	
}
