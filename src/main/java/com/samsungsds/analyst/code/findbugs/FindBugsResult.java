package com.samsungsds.analyst.code.findbugs;

import java.io.Serializable;

import com.samsungsds.analyst.code.main.issue.IssueType;
import com.samsungsds.analyst.code.main.issue.IssueTypeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.Expose;
import com.samsungsds.analyst.code.util.CSVFileResult;

import edu.umd.cs.findbugs.BugInstance;

public class FindBugsResult implements Serializable, CSVFileResult {
	
	private static final long serialVersionUID = 4683955564828835719L;

	private static final Logger LOGGER = LogManager.getLogger(FindBugsResult.class);

	@Expose
	private IssueType type;

	@Expose
	private String patternKey;
	@Expose
	private String pattern;
	@Expose
	private int priority;
	private String priorityString;
	
	private String detailedDescription;
	@Expose
	private String message;
	
	@Expose
	private String packageName = "";
	@Expose
	private String className = "";
	@Expose
	private String file = "";
	@Expose
	private String field = "";
	@Expose
	private String localVariable = "";
	@Expose
	private String method = "";
	@Expose
	private int startLine = 0;
	@Expose
	private int endLine = 0;
	
	public FindBugsResult() {
		// default constructor (CSV)
		// column : package, file, start line, end line, pattern key, pattern, priority, class, field, local var, method, message, type
	}
	
	@Override
	public int getColumnSize() {
		return 13;
	}

	@Override
	public String getDataIn(int columnIndex) {
		switch (columnIndex) {
		case 0 : return packageName;
		case 1 : return file;
		case 2 : return String.valueOf(startLine);
		case 3 : return String.valueOf(endLine);
		case 4 : return patternKey;
		case 5 : return pattern;
		case 6 : return String.valueOf(priority);
		case 7 : return className;
		case 8 : return field;
		case 9 : return localVariable;
		case 10 : return method;
		case 11 : return message;
		case 12 : return type.toString();
		default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	@Override
	public void setDataIn(int columnIndex, String data) {
		switch (columnIndex) {
		case 0 : packageName = data; break;
		case 1 : file = data; break;
		case 2 : startLine = Integer.parseInt(data); break;
		case 3 : endLine = Integer.parseInt(data); break;
		case 4 : patternKey = data; break;
		case 5 : pattern = data; break;
		case 6 : priority = Integer.parseInt(data); break;
		case 7 : className = data; break;
		case 8 : field = data; break;
		case 9 : localVariable = data; break;
		case 10 : method = data; break;
		case 11 : message = data; break;
		case 12 : type = IssueType.getIssueTypeOf(data); break;
		default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}
	
	public FindBugsResult(BugInstance instance) {
		this.patternKey = instance.getType();
		this.pattern = instance.toString();
		this.priority = instance.getPriority();
		this.priorityString = instance.getPriorityString();
		
		this.detailedDescription = instance.getInstanceKey();
		this.message = instance.getMessageWithoutPrefix();
		
		if (instance.getPrimaryClass() != null) {
			this.packageName = instance.getPrimaryClass().getPackageName();
			this.className = instance.getPrimaryClass().getSimpleClassName();
			this.file = instance.getPrimaryClass().getSourceFileName();
		}
		
		if (instance.getPrimaryField() != null) {
			this.field = instance.getPrimaryField().getFieldName();
		}
		
		if (instance.getPrimaryLocalVariableAnnotation() != null) {
			this.localVariable = instance.getPrimaryLocalVariableAnnotation().getName();
		}
		
		if (instance.getPrimaryMethod() != null) {
			this.method = instance.getPrimaryMethod().getMethodName();
		}
		
		if (instance.getPrimarySourceLineAnnotation() != null) {
			this.startLine = instance.getPrimarySourceLineAnnotation().getStartLine();
			this.endLine = instance.getPrimarySourceLineAnnotation().getEndLine();
			
			if (this.startLine == -1) {
				this.startLine = 0;
			}
			if (this.endLine == -1) {
				this.endLine = 0;
			}
		}

		this.type = IssueTypeRepository.getIssueType("FindBugs", patternKey);
				
		LOGGER.debug("pattern : {}, priority : {}, message : {}, package : {}, class : {}, field : {}, localVar : {}, method : {}, line : {} ~ {}, type : {}", patternKey, priority, message, packageName, className, field, localVariable, method, startLine, endLine, type);
	}

	public void setPatternKey(String patternKey) {
		this.patternKey = patternKey;
	}
	
	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getPriorityString() {
		return priorityString;
	}

	public void setPriorityString(String priorityString) {
		this.priorityString = priorityString;
	}

	public String getDetailedDescription() {
		return detailedDescription;
	}

	public void setDetailedDescription(String detailedDescription) {
		this.detailedDescription = detailedDescription;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getLocalVariable() {
		return localVariable;
	}

	public void setLocalVariable(String localVariable) {
		this.localVariable = localVariable;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public String getPatternKey() {
		return patternKey;
	}

	public IssueType getIssueType() {
		return type;
	}
}
