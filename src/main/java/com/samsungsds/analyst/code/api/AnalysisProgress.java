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
