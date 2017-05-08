package com.samsungsds.analyst.code.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.sonar.DuplicationResult;

public class MeasuredResult implements Serializable {
	private static final Logger LOGGER = LogManager.getLogger(MeasuredResult.class);
	
	private static final long serialVersionUID = 1L;
	
	private static MeasuredResult instance = null;
	
	private String projectDirectory;
	
	private int directories = 0;
	private int files = 0;
	private int classes = 0;
	private int commentLines = 0;
	private int complexity = 0;
	private int functions = 0;
	private int lines = 0;
	private int ncloc = 0;
	private int statements = 0;
	
	private List<DuplicationResult> duplicationList = Collections.synchronizedList(new ArrayList<>());
	private int duplicatedLines = 0;
	
	private List<ComplexityResult> complexityList = Collections.synchronizedList(new ArrayList<>());
	private int complexityFunctions = 0;
	private int complexitySum = 0;
	private int complexityOver10 = 0;
	private int complexityOver15 = 0;
	private int complexityOver20 = 0;
	
	private List<PmdResult> pmdList = Collections.synchronizedList(new ArrayList<>());
	private int[] pmdCount = new int[6];	// 0 : 전체, 1 ~ 5 (Priority)
	
	private List<FindBugsResult> findBugsList = Collections.synchronizedList(new ArrayList<>());
	private int[] findBugsCount = new int[6];	// 0 : 전체, 1 ~ 5 (High, Normal, Low, Experimental, Ignore)
	
	public static MeasuredResult getInstance() {
		if (instance == null) {
			synchronized (MeasuredResult.class) {
				if (instance == null) {
					instance = new MeasuredResult();
				}
			}
		}
		
		return instance;
	}
	
	public synchronized void setProjectDirectory(String projectDirectory) {
		this.projectDirectory = projectDirectory;
	}
	
	public synchronized String getProjectDirectory() {
		return projectDirectory;
	}
	
	public int getDirectories() {
		return directories;
	}

	public synchronized void addDirectories(int directories) {
		this.directories += directories;
	}

	public int getFiles() {
		return files;
	}
	
	public synchronized void addFiles(int files) {
		this.files += files;
	}
	
	public int getClasses() {
		return classes;
	}
	
	public synchronized void addClasses(int classes) {
		this.classes += classes;
	}
	
	public int getCommentLines() {
		return commentLines;
	}
	
	public synchronized void addCommentLines(int commentLines) {
		this.commentLines += commentLines;
	}
	
	public int getComplexity() {
		return complexity;
	}
	
	public synchronized void addComplexity(int complexity) {
		this.complexity += complexity;
	}
	
	public int getFunctions() {
		return functions;
	}
	
	public synchronized void addFunctions(int functions) {
		this.functions += functions;
	}
	
	public int getLines() {
		return lines;
	}
	
	public synchronized void addLines(int lines) {
		this.lines += lines;
	}
	
	public int getNcloc() {
		return ncloc;
	}
	
	public synchronized void addNcloc(int ncloc) {
		this.ncloc += ncloc;
	}
	
	public int getStatements() {
		return statements;
	}
	
	public synchronized void addStatements(int statements) {
		this.statements += statements;
	}

	public int getDuplicatedLines() {
		return duplicatedLines / 2;
	}
	
	public String getDuplicatedLinesPercent() {
		return String.format("%.2f%%", (double) (duplicatedLines / 2) / (double) lines * 100);
	}

	public synchronized void addDuplicationResult(DuplicationResult result) {
		this.duplicatedLines += result.getDuplicatedLine();
		
		duplicationList.add(result);
	}
	
	public synchronized void putComplexityList(List<ComplexityResult> list) {
		complexityList.addAll(list);
		
		for (ComplexityResult result : list) {
			complexityFunctions++;
			complexitySum += result.getComplexity();
			
			if (result.getComplexity() > 20) {
				complexityOver20++;
			} 
			if (result.getComplexity() > 15) {
				complexityOver15++;
			} 
			if (result.getComplexity() > 10) {
				complexityOver10++;
			} 
		}
	}
	
	public int getComplexityFunctions() {
		return complexityFunctions;
	}
	
	public int getComplexitySum() {
		return complexitySum;
	}

	public int getComplexityOver10() {
		return complexityOver10;
	}

	public int getComplexityOver15() {
		return complexityOver15;
	}

	public int getComplexityOver20() {
		return complexityOver20;
	}

	public String getComplexityOver10Percent() {
		return String.format("%.2f%%", (double) complexityOver10 / (double) complexityFunctions * 100);
	}

	public String getComplexityOver15Percent() {
		return String.format("%.2f%%", (double) complexityOver15 / (double) complexityFunctions * 100);
	}

	public String getComplexityOver20Percent() {
		return String.format("%.2f%%", (double) complexityOver20 / (double) complexityFunctions * 100);
	}
	
	public synchronized void putPmdList(List<PmdResult> list) {
		pmdList.addAll(list);
		
		for (PmdResult result : list) {
			pmdCount[0]++;
			
			pmdCount[result.getPriority()]++;
			
			
			LOGGER.debug("file : {}, line : {}, priority : {}, rule : {}, desc : {}", result.getFile(), result.getLine(), result.getPriority(), result.getRule(), result.getDescription());
		}
	}
	
	public int getPmdCountAll() {
		return pmdCount[0];
	}
	
	public int getPmdCount(int priority) {
		return pmdCount[priority];
	}
	
	public synchronized void putFindBugsList(List<FindBugsResult> list) {
		findBugsList.addAll(list);
		
		for (FindBugsResult result : list) {
			findBugsCount[0]++;
			
			findBugsCount[result.getPriority()]++;
		}
	}
	
	public int getFindBugsCountAll() {
		return findBugsCount[0];
	}
	
	public int getFindBugsCount(int priority) {
		return findBugsCount[priority];
	}
	
	public synchronized List<String> getPackageList() {
		List<String> packageList = new ArrayList<>();
		
		for (ComplexityResult result : complexityList) {
			if (packageList.contains(result.getPackageName())) {
				continue;
			}
			packageList.add(result.getPackageName());
		}
		
		return packageList;
	}
	
	public List<DuplicationResult> getDulicationList() {
		return duplicationList;
	}
	
	public List<ComplexityResult> getComplexityList() {
		return complexityList;
	}
	
	public List<PmdResult> getPmdList() {
		return pmdList;
	}
	
	public List<FindBugsResult> getFindBugsList() {
		return findBugsList;
	}
}