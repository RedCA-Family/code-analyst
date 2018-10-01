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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + startLine;
		result = prime * result + endLine;
		result = prime * result + ((duplicatedPath == null) ? 0 : duplicatedPath.hashCode());
		result = prime * result + duplicatedStartLine;
		result = prime * result + duplicatedEndLine;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		DuplicationResult other = (DuplicationResult) obj;
		
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		if (startLine != other.startLine) {
			return false;
		}
		if (endLine != other.endLine) {
			return false;
		}
		
		if (duplicatedPath == null) {
			if (other.duplicatedPath != null) {
				return false;
			}
		} else if (!duplicatedPath.equals(other.duplicatedPath)) {
			return false;
		}
		if (duplicatedStartLine != other.duplicatedStartLine) {
			return false;
		}
		if (duplicatedEndLine != other.duplicatedEndLine) {
			return false;
		}
		
		return true;
	}
	
	
}
