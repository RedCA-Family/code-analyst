package com.samsungsds.analyst.code.main.detailed;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class Duplication implements Serializable {
	private static final long serialVersionUID = 3299810301014333784L;
	
	@Expose
	private final String path;
	@Expose
	private final int startLine;
	@Expose
	private final int endLine;
	
	@Expose
	private int totalDuplicatedLines = 0;
	@Expose
	private int count = 0;
	
	public Duplication(String path, int startLine, int endLine) {
		this.path = path;
		this.startLine = startLine;
		this.endLine = endLine;
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

	public int getTotalDuplicatedLines() {
		return totalDuplicatedLines;
	}

	public void setTotalDuplicatedLines(int totalDuplicatedLines) {
		this.totalDuplicatedLines += totalDuplicatedLines;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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
		
		Duplication other = (Duplication) obj;
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
