package com.samsungsds.analyst.code.sonar;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.Expose;
import com.samsungsds.analyst.code.util.CSVFileResult;

public class DuplicationResult implements Serializable, CSVFileResult {

	private static final long serialVersionUID = -4613512668838091111L;

	private static final Logger LOGGER = LogManager.getLogger(DuplicationResult.class);
	
	public static final String DUPLICATED_FILE_SAME_MARK = "-";
	
	@Expose
	private String path;
	@Expose
	private int startLine;
	@Expose
	private int endLine;
	
	@Expose
	private String duplicatedPath;
	@Expose
	private int duplicatedStartLine;
	@Expose
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
	
	public DuplicationResult() {
		// default constructor (CSV)
		// column : path, start line, end line, duplicated path, duplicated start line, duplicated end line
	}
	
	@Override
	public int getColumnSize() {
		return 6;
	}

	@Override
	public String getDataIn(int columnIndex) {
		switch (columnIndex) {
		case 0 : return path;
		case 1 : return String.valueOf(startLine);
		case 2 : return String.valueOf(endLine);
		case 3 : return duplicatedPath;
		case 4 : return String.valueOf(duplicatedStartLine);
		case 5 : return String.valueOf(duplicatedEndLine);
		default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	@Override
	public void setDataIn(int columnIndex, String data) {
		switch (columnIndex) {
		case 0 : path = data; break;
		case 1 : startLine = Integer.parseInt(data); break;
		case 2 : endLine = Integer.parseInt(data); break;
		case 3 : duplicatedPath = data; break;
		case 4 : duplicatedStartLine = Integer.parseInt(data); break;
		case 5 : duplicatedEndLine = Integer.parseInt(data); break;
		default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
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
