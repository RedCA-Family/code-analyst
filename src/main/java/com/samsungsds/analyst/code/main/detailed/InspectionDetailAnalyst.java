package com.samsungsds.analyst.code.main.detailed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.pmd.PmdResult;

public class InspectionDetailAnalyst {
	public static final int TOP = 10;
	private Map<Inspection, Integer> pmdList = new HashMap<>();
	private Map<Inspection, Integer> findBugsList = new HashMap<>();

	public void add(PmdResult result) {
		Inspection pmd = getInspection(result);
		
		if (pmdList.get(pmd) == null) {
			pmdList.put(pmd, 1);
		} else {
			pmdList.put(pmd, pmdList.get(pmd) + 1);
		}	
	}

	protected Inspection getInspection(PmdResult result) {
		return new Inspection(result.getRule());
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
		return new Inspection(result.getPatternKey());
	}
	
	public List<Inspection> getTopPmdList() {
		return getTopList(pmdList);
	}
	
	public List<Inspection> getTopFindBugsList() {
		return getTopList(findBugsList);
	}

	private List<Inspection> getTopList(Map<Inspection, Integer> targetList) {
		List<Inspection> result = new ArrayList<>(TOP);
		
		List<Map.Entry<Inspection, Integer>> sortedList = 
				targetList.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toList());
		
		for (int i = 0; i < TOP && i < sortedList.size(); i++) {
			int index = sortedList.size() - i - 1;
			
			if (sortedList.get(index).getValue() <= 1) {
				break;
			}
			
			Inspection inspection = sortedList.get(index).getKey();
			
			Inspection newInspection = new Inspection(inspection.getRule());
			
			newInspection.setCount(sortedList.get(index).getValue());
			
			result.add(newInspection);
		}
		
		return result;
	}
}
