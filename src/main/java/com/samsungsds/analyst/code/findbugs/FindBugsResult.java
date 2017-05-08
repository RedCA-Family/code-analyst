package com.samsungsds.analyst.code.findbugs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.umd.cs.findbugs.BugInstance;

public class FindBugsResult {
	private static final Logger LOGGER = LogManager.getLogger(FindBugsResult.class);
	
	private String patternKey;
	private String pattern;
	private int priority;
	private String priorityString;
	
	private String detailedDescription;
	private String message;
	
	private String packageName = "";
	private String className = "";
	private String file = "";
	private String field = "";
	private String localVariable = "";
	private String method = "";
	private int startLine = 0;
	private int endLine = 0;
	
	public FindBugsResult() {
		// no-op
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
				
		LOGGER.debug("pattern : {}, priority : {}, message : {}, package : {}, class : {}, field : {}, localVar : {}, method : {}, line : {} ~ {}", patternKey, priority, message, packageName, className, field, localVariable, method, startLine, endLine);
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
}
