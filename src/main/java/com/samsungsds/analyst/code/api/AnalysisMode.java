package com.samsungsds.analyst.code.api;

public class AnalysisMode {
	private boolean codeSize = false;
	private boolean duplication = false;
	private boolean complexity = false;
	private boolean pmd = false;
	private boolean findBugs = false;
	private boolean findSecBugs = false;
	private boolean dependency = false;
	
	public boolean isCodeSize() {
		return codeSize;
	}
	
	public void setCodeSize(boolean codeSize) {
		this.codeSize = codeSize;
	}
	
	public boolean isDuplication() {
		return duplication;
	}
	
	public void setDuplication(boolean duplication) {
		this.duplication = duplication;
	}
	
	public boolean isComplexity() {
		return complexity;
	}
	
	public void setComplexity(boolean complexity) {
		this.complexity = complexity;
	}
	
	public boolean isPmd() {
		return pmd;
	}
	
	public void setPmd(boolean pmd) {
		this.pmd = pmd;
	}
	
	public boolean isFindBugs() {
		return findBugs;
	}
	
	public void setFindBugs(boolean findBugs) {
		this.findBugs = findBugs;
	}
	
	public boolean isFindSecBugs() {
		return findSecBugs;
	}
	
	public void setFindSecBugs(boolean findSecBugs) {
		this.findSecBugs = findSecBugs;
	}
	
	public boolean isDependency() {
		return dependency;
	}
	
	public void setDependency(boolean dependency) {
		this.dependency = dependency;
	}
}
