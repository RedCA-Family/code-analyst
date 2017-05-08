package com.samsungsds.analyst.code.pmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ComplexityResult implements Serializable {

	private static final long serialVersionUID = 1418436775355728495L;

	private static final Logger LOGGER = LogManager.getLogger(ComplexityResult.class);
	
	private String path;
	private String packageName;
	private String fileName;
	private int line;
	private String methodName;
	private int complexity;
	
	public String getPath() {
		return path;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public String getFileName() {
		return fileName;
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

	public static ComplexityResult convertComplexitResult(PmdResult result) {
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
	
	public static List<ComplexityResult> convertComplexitResult(List<PmdResult> resultList) {
		List<ComplexityResult> list = new ArrayList<>();
		
		for (PmdResult result : resultList) {
			ComplexityResult converted = convertComplexitResult(result);
			
			if (converted != null) {
				LOGGER.debug("file : {}, method : {}, line : {}, complexity : {}", converted.fileName, converted.methodName, converted.line, converted.complexity );
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
