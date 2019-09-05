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
			ProgressEvent.PREPARE_COMPLETE, ProgressEvent.SONAR_START_COMPLETE,
			ProgressEvent.CODE_SIZE_COMPLETE, ProgressEvent.DUPLICATION_COMPLETE,
			ProgressEvent.COMPLEXITY_COMPLETE, ProgressEvent.SONARJAVA_COMPLETE,
			ProgressEvent.JAVASCRIPT_COMPLETE, ProgressEvent.CSS_COMPLETE, ProgressEvent.HTML_COMPLETE,
            ProgressEvent.SONARCSHARP_COMPLETE, ProgressEvent.SONARPYTHON_COMPLETE,
			ProgressEvent.SONAR_ALL_COMPLETE,
			ProgressEvent.PMD_COMPLETE, ProgressEvent.FINDBUGS_COMPLETE, ProgressEvent.FINDSECBUGS_COMPLETE,
			ProgressEvent.DEPENDENCY_COMPLETE, ProgressEvent.UNUSED_COMPLETE, ProgressEvent.CK_METRICS_COMPLETE,
			ProgressEvent.FINAL_COMPLETE };
	private static Map<ProgressEvent, Integer> stepRates = new HashMap<>();
	private static Map<ProgressEvent, String> stepProperties = new HashMap<>();

	private AnalysisMode analysisMode;

	private int totalSteps = 0;

	private AnalysisProgress currentProgress = null;

	private long time = 0L;

	static {
		stepRates.put(ProgressEvent.PREPARE_COMPLETE, 50);
		stepRates.put(ProgressEvent.SONAR_START_COMPLETE, 1_000);
		stepRates.put(ProgressEvent.CODE_SIZE_COMPLETE, 10_000);
		stepRates.put(ProgressEvent.DUPLICATION_COMPLETE, 9_000);
		stepRates.put(ProgressEvent.COMPLEXITY_COMPLETE, 1_500);
		stepRates.put(ProgressEvent.SONARJAVA_COMPLETE, 8_000);
		stepRates.put(ProgressEvent.JAVASCRIPT_COMPLETE, 24_000);
		stepRates.put(ProgressEvent.CSS_COMPLETE, 2_000);
		stepRates.put(ProgressEvent.HTML_COMPLETE, 2_000);
        stepRates.put(ProgressEvent.SONARCSHARP_COMPLETE, 20_000);
        stepRates.put(ProgressEvent.SONARPYTHON_COMPLETE, 15_000);
		stepRates.put(ProgressEvent.SONAR_ALL_COMPLETE, 5_000);
		stepRates.put(ProgressEvent.PMD_COMPLETE, 2_800);
		stepRates.put(ProgressEvent.FINDBUGS_COMPLETE, 9_500);
		stepRates.put(ProgressEvent.FINDSECBUGS_COMPLETE, 9_000);
		stepRates.put(ProgressEvent.DEPENDENCY_COMPLETE, 1_700);
		stepRates.put(ProgressEvent.UNUSED_COMPLETE, 1_500);
		stepRates.put(ProgressEvent.CK_METRICS_COMPLETE, 1_500);
        stepRates.put(ProgressEvent.CHECKSTYLE_COMPLETE, 1_500);
		stepRates.put(ProgressEvent.FINAL_COMPLETE, 100);

		stepProperties.put(ProgressEvent.SONAR_START_COMPLETE, "sonarServer");
		stepProperties.put(ProgressEvent.CODE_SIZE_COMPLETE, "codeSize");
		stepProperties.put(ProgressEvent.DUPLICATION_COMPLETE, "duplication");
		stepProperties.put(ProgressEvent.COMPLEXITY_COMPLETE, "complexity");
		stepProperties.put(ProgressEvent.SONARJAVA_COMPLETE, "sonarJava");
		stepProperties.put(ProgressEvent.JAVASCRIPT_COMPLETE, "javascript");
		stepProperties.put(ProgressEvent.CSS_COMPLETE, "css");
		stepProperties.put(ProgressEvent.HTML_COMPLETE, "html");
        stepProperties.put(ProgressEvent.SONARCSHARP_COMPLETE, "sonarCSharp");
        stepProperties.put(ProgressEvent.SONARPYTHON_COMPLETE, "sonarPython");
		stepProperties.put(ProgressEvent.SONAR_ALL_COMPLETE, "sonarServer");	// Same as SONAR_START_COMPLETE;
		stepProperties.put(ProgressEvent.PMD_COMPLETE, "pmd");
		stepProperties.put(ProgressEvent.FINDBUGS_COMPLETE, "findBugs");
		stepProperties.put(ProgressEvent.FINDSECBUGS_COMPLETE, "findSecBugs");
		stepProperties.put(ProgressEvent.DEPENDENCY_COMPLETE, "dependency");
		stepProperties.put(ProgressEvent.UNUSED_COMPLETE, "unusedCode");
		stepProperties.put(ProgressEvent.CK_METRICS_COMPLETE, "ckMetrics");
        stepProperties.put(ProgressEvent.CHECKSTYLE_COMPLETE, "checkStyle");
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
