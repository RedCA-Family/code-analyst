package com.samsungsds.analyst.code.api;

public class AnalysisProgress {
	private ProgressEvent progressEvent;
	
	private int totalSteps = 0;
	private int completedSteps = 0;
	
	private long elapsedTimeInMillisecond = 0;
	
	public AnalysisProgress(int totalSteps, int completedSteps) {
		this(totalSteps);
		this.completedSteps = completedSteps;
	}
	
	public AnalysisProgress(int totalSteps) {
		this.totalSteps = totalSteps;
	}

	public ProgressEvent getProgressEvent() {
		return progressEvent;
	}
	
	public void setProgressEvent(ProgressEvent progressEvent) {
		this.progressEvent = progressEvent;
	}
	
	public void addCompletedStep(int addedSteps) {
		completedSteps += addedSteps;
		
		if (completedSteps > totalSteps) {
			completedSteps = totalSteps;
		}
	}
	
	public int getCompletedPercent() {
		return completedSteps * 100 / totalSteps;
	}

	public int getCompletedSteps() {
		return completedSteps;
	}

	public long getElapsedTimeInMillisecond() {
		return elapsedTimeInMillisecond;
	}

	public void setElapsedTimeInMillisecond(long elapsedTimeInMillisecond) {
		this.elapsedTimeInMillisecond = elapsedTimeInMillisecond;
	}
}
