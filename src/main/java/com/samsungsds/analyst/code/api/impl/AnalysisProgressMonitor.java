package com.samsungsds.analyst.code.api.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.samsungsds.analyst.code.api.AnalysisMode;
import com.samsungsds.analyst.code.api.AnalysisProgress;
import com.samsungsds.analyst.code.api.ProgressEvent;

public class AnalysisProgressMonitor {
	private static ProgressEvent[] eventKeys = new ProgressEvent[] { 
			ProgressEvent.PREPARE_COMPLETE, ProgressEvent.CODE_SIZE_COMPLETE, ProgressEvent.DUPLICATION_COMPLETE, 
			ProgressEvent.COMPLEXITY_COMPLETE, ProgressEvent.PMD_COMPLETE, ProgressEvent.FINDBUGS_COMPLETE, 
			ProgressEvent.FINDSECBUGS_COMPLETE, ProgressEvent.DEPENDENCY_COMPLETE, ProgressEvent.FINAL_COMPLETE };
	private static Map<ProgressEvent, Integer> stepRates = new HashMap<>();
	private static Map<ProgressEvent, String> stepProperties = new HashMap<>();
	
	private AnalysisMode analysisMode;
	
	private int totalSteps = 0;
	
	private AnalysisProgress currentProgress = null;
	
	private long time = 0l;
	
	static {
		stepRates.put(ProgressEvent.PREPARE_COMPLETE, 10);
		stepRates.put(ProgressEvent.CODE_SIZE_COMPLETE, 10_000);
		stepRates.put(ProgressEvent.DUPLICATION_COMPLETE, 4_000);
		stepRates.put(ProgressEvent.COMPLEXITY_COMPLETE, 1_000);
		stepRates.put(ProgressEvent.PMD_COMPLETE, 2_000);
		stepRates.put(ProgressEvent.FINDBUGS_COMPLETE, 5_000);
		stepRates.put(ProgressEvent.FINDSECBUGS_COMPLETE, 5_500);
		stepRates.put(ProgressEvent.DEPENDENCY_COMPLETE, 500);
		stepRates.put(ProgressEvent.FINAL_COMPLETE, 30);
		
		stepProperties.put(ProgressEvent.CODE_SIZE_COMPLETE, "codeSize");
		stepProperties.put(ProgressEvent.DUPLICATION_COMPLETE, "duplication");
		stepProperties.put(ProgressEvent.COMPLEXITY_COMPLETE, "complexity");
		stepProperties.put(ProgressEvent.PMD_COMPLETE, "pmd");
		stepProperties.put(ProgressEvent.FINDBUGS_COMPLETE, "findBugs");
		stepProperties.put(ProgressEvent.FINDSECBUGS_COMPLETE, "findSecBugs");
		stepProperties.put(ProgressEvent.DEPENDENCY_COMPLETE, "dependency");
	}
	
	public AnalysisProgressMonitor(AnalysisMode analysisMode) {
		this.analysisMode = analysisMode;
		
		totalSteps += stepRates.get(ProgressEvent.PREPARE_COMPLETE);
		totalSteps += stepRates.get(ProgressEvent.FINAL_COMPLETE);
		
		for (ProgressEvent eventKey : stepProperties.keySet()) {
			if (getBooleanValue(stepProperties.get(eventKey))) {
				totalSteps += stepRates.get(eventKey);
			}
		}
		
		currentProgress = new AnalysisProgress(totalSteps);
		
		currentProgress.setElapsedTimeInMillisecond(0L);
		time = System.currentTimeMillis();
	}
	
	private boolean getBooleanValue(String propertyName) {
		try {
			Method method = analysisMode.getClass().getMethod("is" + StringUtils.capitalize(propertyName));
			
			return (boolean) method.invoke(analysisMode);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public AnalysisProgress getNextAnalysisProgress(ProgressEvent event) {
		AnalysisProgress newProgress = new AnalysisProgress(totalSteps, currentProgress.getCompletedSteps());
		
		for (ProgressEvent eventKey : eventKeys) {
			if (event == eventKey) {
				if (stepProperties.containsKey(eventKey)) {
					if (getBooleanValue(stepProperties.get(eventKey))) {
						newProgress.addCompletedStep(stepRates.get(event));
					}
				} else {
					newProgress.addCompletedStep(stepRates.get(event));
				}
			}
		}

		newProgress.setProgressEvent(event);
		
		newProgress.setElapsedTimeInMillisecond(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();
		
		currentProgress = newProgress;
		
		return currentProgress;
	}
}
