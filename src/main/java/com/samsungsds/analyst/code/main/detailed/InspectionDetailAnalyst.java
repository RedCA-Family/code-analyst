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
package com.samsungsds.analyst.code.main.detailed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.sonar.SonarJavaResult;

public class InspectionDetailAnalyst {

	public static final int TOP = 10;
	private Map<Inspection, Integer> sonarJavaList = new HashMap<>();
	private Map<Inspection, Integer> pmdList = new HashMap<>();
	private Map<Inspection, Integer> findBugsList = new HashMap<>();

	public void add(SonarJavaResult result) {
		Inspection sonarJava = getInspection(result);

		if (sonarJavaList.get(sonarJava) == null) {
			sonarJavaList.put(sonarJava, 1);
		} else {
			sonarJavaList.put(sonarJava, sonarJavaList.get(sonarJava) + 1);
		}
	}

	protected Inspection getInspection(SonarJavaResult result) {
		return new Inspection(result.getRuleRepository() + ":" + result.getRuleKey(), result.getIssueType().toString());
	}

	public void add(PmdResult result) {
		Inspection pmd = getInspection(result);

		if (pmdList.get(pmd) == null) {
			pmdList.put(pmd, 1);
		} else {
			pmdList.put(pmd, pmdList.get(pmd) + 1);
		}
	}

	protected Inspection getInspection(PmdResult result) {
		return new Inspection(result.getRule(), result.getIssueType().toString());
	}

	public void add(FindBugsResult result) {
		Inspection pmd = getInspection(result);

		if (findBugsList.get(pmd) == null) {
			findBugsList.put(pmd, 1);
		} else {
			findBugsList.put(pmd, findBugsList.get(pmd) + 1);
		}
	}

	private Inspection getInspection(FindBugsResult result) {
		return new Inspection(result.getPatternKey(), result.getIssueType().toString());
	}

	public List<Inspection> getTopSonarJavaList() {
		return getTopList(sonarJavaList);
	}

	public List<Inspection> getTopPmdList() {
		return getTopList(pmdList);
	}

	public List<Inspection> getTopFindBugsList() {
		return getTopList(findBugsList);
	}

	private List<Inspection> getTopList(Map<Inspection, Integer> targetList) {
		List<Inspection> result = new ArrayList<>(TOP);

		List<Map.Entry<Inspection, Integer>> sortedList = targetList.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());

		for (int i = 0; i < TOP && i < sortedList.size(); i++) {
			int index = sortedList.size() - i - 1;

			if (sortedList.get(index).getValue() <= 1) {
				break;
			}

			Inspection inspection = sortedList.get(index).getKey();

			Inspection newInspection = new Inspection(inspection.getRule(), inspection.getType());

			newInspection.setCount(sortedList.get(index).getValue());

			result.add(newInspection);
		}

		return result;
	}
}
