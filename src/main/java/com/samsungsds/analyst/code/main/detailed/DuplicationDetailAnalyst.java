package com.samsungsds.analyst.code.main.detailed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.samsungsds.analyst.code.sonar.DuplicationResult;

public class DuplicationDetailAnalyst {
	public static final int TOP = 10;
	private Map<Duplication, Pair> duplicationData = new HashMap<>();
	private Set<DuplicationCheck> haveToSkipData = new HashSet<>();
	
	public void add(DuplicationResult duplication) {
		DuplicationCheck check = DuplicationCheck.createFrom(duplication);
		
		if (haveToSkipData.contains(check)) {
			return;
		}
		Duplication data = new Duplication(duplication.getPath(), duplication.getStartLine(), duplication.getEndLine());
		
		int duplicatedLines = duplication.getDuplicatedEndLine() - duplication.getDuplicatedStartLine() + 1;

		if (duplicationData.get(data) == null) {
			duplicationData.put(data, new Pair(duplicatedLines, 1));
		} else {
			Pair pair = duplicationData.get(data);
			pair.addTotalLines(duplicatedLines);
			pair.addCount(1);
		}
		
		// 중복된 데이터 처리 (duplicated lines을 minus로 등록)
		String duplicatedPath = duplication.getDuplicatedPath();
		
		if (duplicatedPath.equals(DuplicationResult.DUPLICATED_FILE_SAME_MARK)) {
			duplicatedPath = duplication.getPath();
		}
		
		data = new Duplication(duplicatedPath, duplication.getDuplicatedStartLine(), duplication.getDuplicatedEndLine());
		
		duplicatedLines = duplication.getEndLine() - duplication.getStartLine() + 1;

		if (duplicationData.get(data) == null) {
			duplicationData.put(data, new Pair(-duplicatedLines, -1));
		} else {
			Pair pair = duplicationData.get(data);
			pair.addTotalLines(-duplicatedLines);
			pair.addCount(-1);
		}
		
		haveToSkipData.add(new DuplicationCheck(duplicatedPath, duplication.getDuplicatedStartLine(), duplication.getDuplicatedEndLine()));
	}
	
	public List<Duplication> getTopList() {
		List<Duplication> result = new ArrayList<>(TOP);
		
		/*
		for (Entry<Duplication, Pair> entry : duplicationData.entrySet()) {            
			entry.getValue().calcuateTotalLines();
        }
        */
		
		List<Map.Entry<Duplication,Pair>> sortedList = 
		duplicationData.entrySet().stream()
			.sorted(Map.Entry.comparingByValue())
			.collect(Collectors.toList());
		
		for (int i = 0; i < TOP && i < sortedList.size(); i++) {
			int index = sortedList.size() - i - 1;
			
			if (sortedList.get(index).getValue().getTotalLines() <= 0) {
				break;
			}
			
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
	
	public void calcuateTotalLines() {
		this.totalLines *= (count + 1);
	}

	@Override
	public int compareTo(Pair o) {
		return this.totalLines - o.totalLines;
	}
}