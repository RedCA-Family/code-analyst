package com.samsungsds.analyst.code.sonar;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DuplicationResult implements Serializable {

	private static final long serialVersionUID = -3351274587827246945L;

	private static final Logger LOGGER = LogManager.getLogger(DuplicationResult.class);
	
	public static final String DUPLICATED_FILE_SAME_MARK = "-";
	
	private String path;
	private int startLine;
	private int endLine;
	
	private String duplicatedPath;
	private int duplicatedStartLine;
	private int duplicatedEndLine;
	
	public DuplicationResult(String path, int startLine, int endLine, String duplicatedPath, int duplicatedStartLine, int duplicatedEndLine) {
		this.path = path;
		
		if (startLine > endLine) {
			this.startLine = endLine;
			this.endLine = startLine;
		} else {
			this.startLine = startLine;
			this.endLine = endLine;
		}
		
		this.duplicatedPath = duplicatedPath;
		if (duplicatedStartLine > duplicatedEndLine) {
			this.duplicatedStartLine = duplicatedEndLine;
			this.duplicatedEndLine = duplicatedStartLine;
			
		} else {
			this.duplicatedStartLine = duplicatedStartLine;
			this.duplicatedEndLine = duplicatedEndLine;
		}
		
		LOGGER.debug("path : {}, {} ~ {}, duplicated path : {}, {} ~ {}", path, startLine, endLine, duplicatedPath, duplicatedStartLine, duplicatedEndLine);
	}

	public String getPath() {
		return path;
	}

	public int getStartLine() {
		return startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public String getDuplicatedPath() {
		return duplicatedPath;
	}

	public int getDuplicatedStartLine() {
		return duplicatedStartLine;
	}

	public int getDuplicatedEndLine() {
		return duplicatedEndLine;
	}
	
	public int getDuplicatedLine() {
		return endLine - startLine + 1;
	}
}
