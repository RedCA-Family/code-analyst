package com.samsungsds.analyst.code.main.detailed;

import com.samsungsds.analyst.code.sonar.DuplicationResult;

public class DuplicationCheck {
	private String path;
	private int startLine;
	private int endLine;
	
	public DuplicationCheck(String path, int startLine, int endLine) {
		this.path = path;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	
	public static DuplicationCheck createFrom(DuplicationResult result) {
		return new DuplicationCheck(result.getPath(), result.getStartLine(), result.getEndLine());
	}

	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + startLine;
		result = prime * result + endLine;
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
		DuplicationCheck other = (DuplicationCheck) obj;
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
		return true;
	}
	
	
}
