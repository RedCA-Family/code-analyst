package com.samsungsds.analyst.code.pmd;

import java.io.File;

import com.google.gson.annotations.Expose;
import com.samsungsds.analyst.code.main.MeasuredResult;

public class PmdResult {
	private int problem;
	private String packageName;
	private String file;
	@Expose
	private int priority;
	@Expose
	private int line;
	@Expose
	private String description;
	private String ruleSet;
	@Expose
	private String rule;
	
	@Expose
	private String path;
	
	public static String getConvertedFilePath(String filePath) {
		String path = filePath.replaceAll("\\\\", "/");
		
		String project = MeasuredResult.getInstance().getProjectDirectory().replaceAll("\\\\", "/");
		
		if (!project.endsWith("/")) {
			project += "/";
		}
		
		if (path.startsWith(project)) {
			path = path.substring(project.length());
		}
		
		return path;
	}
	
	public static String getConvertedFileName(String filePath) {
		String fileName = filePath.replace("\\", File.separator);
		fileName = fileName.replace("/", File.separator);
		
		int lastIndex = fileName.lastIndexOf(File.separator);
		
		fileName = lastIndex > 0 ? fileName.substring(lastIndex + 1) : fileName;
		
		return fileName;
	}
	
	public PmdResult(String problem, String packageName, String file, String priority, String line, String description, String ruleSet, String rule) {
		this.problem = Integer.parseInt(problem);
		this.packageName = packageName;
		this.file = getConvertedFileName(file);
		this.priority = Integer.parseInt(priority);
		this.line = Integer.parseInt(line);
		this.description = description;
		this.ruleSet = ruleSet;
		this.rule = rule;
		
		this.path = getConvertedFilePath(file);
	}
	

	public int getProblem() {
		return problem;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getFile() {
		return file;
	}

	public int getPriority() {
		return priority;
	}

	public int getLine() {
		return line;
	}

	public String getDescription() {
		return description;
	}

	public String getRuleSet() {
		return ruleSet;
	}

	public String getRule() {
		return rule;
	}
	
	public String getPath() {
		return path;
	}
}
