package com.samsungsds.analyst.code.pmd;

import java.io.File;
import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.samsungsds.analyst.code.main.issue.IssueType;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.main.issue.IssueTypeRepository;
import com.samsungsds.analyst.code.util.CSVFileResult;

public class PmdResult implements Serializable, CSVFileResult {
	
	private static final long serialVersionUID = -7225402070361848065L;

	@Expose
	private IssueType type;

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
	
	public PmdResult() {
		// default constructor (CSV)
		// column : path, line, rule, priority, description, type
		problem = 0;
		packageName = "";
		file = "";
		ruleSet = "";
	}
	
	@Override
	public int getColumnSize() {
		return 6;
	}

	@Override
	public String getDataIn(int columnIndex) {
		switch (columnIndex) {
		case 0 : return path;
		case 1 : return String.valueOf(line);
		case 2 : return rule;
		case 3 : return String.valueOf(priority);
		case 4 : return description;
		case 5 : return type.toString();
		default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	@Override
	public void setDataIn(int columnIndex, String data) {
		switch (columnIndex) {
		case 0 : path = data; break;
		case 1 : line = Integer.parseInt(data); break;
		case 2 : rule = data; break;
		case 3 : priority = Integer.parseInt(data); break;
		case 4 : description = data; break;
		case 5 : type = IssueType.getIssueTypeOf(data); break;
		default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}
	
	public static String getConvertedFilePath(String filePath, String instanceKey) {
		String path = filePath.replaceAll("\\\\", "/");
		
		String project = MeasuredResult.getInstance(instanceKey).getProjectDirectory().replaceAll("\\\\", "/");
		
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
	
	public PmdResult(String problem, String packageName, String file, String priority, String line, String description, String ruleSet, String rule, String instanceKey) {
		this.problem = Integer.parseInt(problem);
		this.packageName = packageName;
		this.file = getConvertedFileName(file);
		this.priority = Integer.parseInt(priority);
		this.line = Integer.parseInt(line);
		this.description = description;
		this.ruleSet = ruleSet;
		this.rule = rule;
		
		this.path = getConvertedFilePath(file, instanceKey);

		this.type = IssueTypeRepository.getIssueType("PMD", rule);
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

	public IssueType getIssueType() {
		return type;
	}
}
