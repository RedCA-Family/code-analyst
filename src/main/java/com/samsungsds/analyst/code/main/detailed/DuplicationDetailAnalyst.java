package com.samsungsds.analyst.code.main.detailed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.samsungsds.analyst.code.sonar.DuplicationResult;

public class DuplicationDetailAnalyst {
	public static final int TOP = 10;
	private Map<Duplication, Pair> duplicationData = new HashMap<>();
	
	public void add(DuplicationResult duplication) {
		Duplication data = new Duplication(duplication.getPath(), duplication.getStartLine(), duplication.getEndLine());
		
		int duplicatedLines = duplication.getDuplicatedEndLine() - duplication.getDuplicatedStartLine() + 1;

		if (duplicationData.get(data) == null) {
			duplicationData.put(data, new Pair(duplicatedLines, 1));
		} else {
			Pair pair = duplicationData.get(data);
			pair.addTotalLines(duplicatedLines);
			pair.addCount(1);
		}
	}
	
	public List<Duplication> getTopList() {
		List<Duplication> result = new ArrayList<>(TOP);
		
		List<Map.Entry<Duplication,Pair>> sortedList = 
		duplicationData.entrySet().stream()
			.sorted(Map.Entry.comparingByValue())
			.collect(Collectors.toList());
		
		for (int i = 0; i < TOP && i < sortedList.size(); i++) {
			int index = sortedList.size() - i - 1;
			Duplication duplication = sortedList.get(index).getKey();
			
			Duplication newDuplication = new Duplication(duplication.getPath(), duplication.getStartLine(), duplication.getEndLine());
			
			newDuplication.setTotalDuplicatedLines(sortedList.get(index).getValue().getTotalLines());
			newDuplication.setCount(sortedList.get(index).getValue().getCount());
			
			result.add(newDuplication);
		}
		
		return result;
	}
}

class Pair implements Comparable<Pair> {
	private int totalLines;
	private int count;
	
	public Pair(int totalLines, int count) {
		this.totalLines = totalLines;
		this.count = count;
	}
	
	public int getTotalLines() {
		return totalLines;
	}
	
	public void addTotalLines(int totalLines) {
		this.totalLines += totalLines;
	}
	
	public int getCount() {
		return count;
	}
	
	public void addCount(int count) {
		this.count += count;
	}

	@Override
	public int compareTo(Pair o) {
		return this.totalLines - o.totalLines;
	}

}