package com.samsungsds.analyst.code.main;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.main.filter.FilePathExcludeFilter;
import com.samsungsds.analyst.code.main.filter.FilePathFilter;
import com.samsungsds.analyst.code.main.filter.FilePathIncludeFilter;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.sonar.DuplicationResult;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import com.samsungsds.analyst.code.util.PackageUtils;

public class MeasuredResult implements Serializable {
	private static final Logger LOGGER = LogManager.getLogger(MeasuredResult.class);
	
	private static final long serialVersionUID = 1L;
	
	private static MeasuredResult instance = null;
	
	@Expose
	@SerializedName("target")
	private String projectDirectory;
	
	@Expose
	private String source;
	@Expose
	private String binary;
	@Expose
	private String encoding;
	@Expose
	private String javaVersion;
	@Expose
	private String dateTime;
	@Expose
	private String pmdRuleSetFile;
	@Expose
	private String findBugsRuleSetFile;
	
	@Expose
	private String includes = "";
	@Expose
	private String excludes = "";
	
	private MeasurementMode mode;
	
	@Expose
	@SerializedName("mode")
	private String individualModeString ="all";
	
	private IndividualMode individualMode;
	
	@Expose
	private String version = Version.CODE_ANALYST.substring(0, Version.CODE_ANALYST.lastIndexOf("."));
	
	@Expose
	private int directories = 0;
	@Expose
	private int files = 0;
	@Expose
	private int classes = 0;
	@Expose
	private int commentLines = 0;
	@Expose
	private int complexity = 0;
	@Expose
	private int functions = 0;
	@Expose
	private int lines = 0;
	@Expose
	private int ncloc = 0;
	@Expose
	private int statements = 0;
	
	@Expose
	private List<DuplicationResult> duplicationList = Collections.synchronizedList(new ArrayList<>());
	@Expose
	private int duplicatedLines = 0;
	
	private Map<String, Set<Integer>> duplicatedBlockData = new HashMap<>();
	
	// contains all method for complexity mode
	private List<ComplexityResult> allMethodList = Collections.synchronizedList(new ArrayList<>());
	
	// used to check target package (include or exclude)
	private List<String> packageList = Collections.synchronizedList(new ArrayList<>());
	
	@Expose
	@SerializedName("complexityList")
	private List<ComplexityResult> complexityListOver10 = Collections.synchronizedList(new ArrayList<>());
	
	@Expose
	private int complexityFunctions = 0;
	@Expose
	@SerializedName("complexityTotal")
	private int complexitySum = 0;
	@Expose
	private int complexityOver10 = 0;
	@Expose
	private int complexityOver15 = 0;
	@Expose
	private int complexityOver20 = 0;
	@Expose
	private int complexityEqualOrOver50 = 0;
	
	@Expose
	private List<PmdResult> pmdList = Collections.synchronizedList(new ArrayList<>());
	@Expose
	private int[] pmdCount = new int[6];	// 0 : 전체, 1 ~ 5 (Priority)
	
	@Expose
	private List<FindBugsResult> findBugsList = Collections.synchronizedList(new ArrayList<>());
	@Expose
	private int[] findBugsCount = new int[6];	// 0 : 전체, 1 ~ 5 (High, Normal, Low, Experimental, Ignore)
	
	@Expose
	private List<FindBugsResult> findSecBugsList = Collections.synchronizedList(new ArrayList<>());
	@Expose
	private int[] findSecBugsCount = new int[6];	// 0 : 전체, 1 ~ 5 (High, Normal, Low, Experimental, Ignore)
	
	@Expose
	private List<String> acyclicDependencyList = Collections.synchronizedList(new ArrayList<>());
	
	private List<FilePathFilter> filePathFilterList = new ArrayList<>();

	private File outputFile;
	
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
	
	public void setProjectInfo(CliParser cli) {
		source = cli.getSrc();
		binary = cli.getBinary();
		encoding = cli.getEncoding();
		javaVersion = cli.getJavaVersion();
		dateTime = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date());
		if (cli.getRuleSetFileForPMD() != null && !cli.getRuleSetFileForPMD().equals("")) {
			pmdRuleSetFile = cli.getRuleSetFileForPMD();
		} else {
			pmdRuleSetFile = "";
		}
		if (cli.getRuleSetFileForFindBugs() != null && !cli.getRuleSetFileForFindBugs().equals("")) {
			findBugsRuleSetFile = cli.getRuleSetFileForFindBugs();
		} else {
			findBugsRuleSetFile = "";
		}
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
		return duplicatedLines;
	}
	
	public String getDuplicatedLinesPercent() {
		return String.format("%.2f%%", (double) duplicatedLines / (double) lines * 100);
	}

	public synchronized void addDuplicationResult(DuplicationResult result) {
		String path = result.getPath();
		String duplcatedPath = null;
		
		if (DuplicationResult.DUPLICATED_FILE_SAME_MARK.equals(result.getDuplicatedPath())) {
			duplcatedPath = result.getPath();
		} else {
			duplcatedPath = result.getDuplicatedPath();
		}
		
		if (haveToSkip(path) || haveToSkip(duplcatedPath)) {
			return;
		}
		
		duplicationList.add(result);
		
		if (duplicatedBlockData.containsKey(path)) {
			this.duplicatedLines += getAddedDuplicatedLines(result.getStartLine(), result.getEndLine(), duplicatedBlockData.get(path));
		} else {
			Set<Integer> duplicatedLineNumbers = new HashSet<>();
			
			this.duplicatedLines += getAddedDuplicatedLines(result.getStartLine(), result.getEndLine(), duplicatedLineNumbers);
			
			duplicatedBlockData.put(path, duplicatedLineNumbers);
		}
		
		if (duplicatedBlockData.containsKey(duplcatedPath)) {
			this.duplicatedLines += getAddedDuplicatedLines(result.getDuplicatedStartLine(), result.getDuplicatedEndLine(), duplicatedBlockData.get(duplcatedPath));
		} else {			
			Set<Integer> duplicatedLineNumbers = new HashSet<>();
			
			this.duplicatedLines += getAddedDuplicatedLines(result.getDuplicatedStartLine(), result.getDuplicatedEndLine(), duplicatedLineNumbers);
			
			duplicatedBlockData.put(duplcatedPath, duplicatedLineNumbers);
		}
	}

	private int getAddedDuplicatedLines(int from, int to, Set<Integer> duplicatedLineNumbers) {
		int beforeLines = duplicatedLineNumbers.size();
		for (int i = from; i <= to; i++) {
			duplicatedLineNumbers.add(i);
		}
		int afterLines = duplicatedLineNumbers.size();
		
		return (afterLines - beforeLines);
	}
	
	private boolean haveToSkip(String path) {
		return haveToSkip(path, false);
	}
	
	private boolean haveToSkip(String path, boolean withoutFilename) {
		for (FilePathFilter filter : filePathFilterList) {
			if (!filter.matched(path, withoutFilename)) {
				return true;
			}	
		}
		return false;
	}
	
	public String getDuplicatedBlockDebugInfo() {
		StringBuilder logMessage = new StringBuilder();
		
		for (String path : duplicatedBlockData.keySet()) {
			if (logMessage.length() != 0) {
				logMessage.append(IOAndFileUtils.CR_LF);
			}
			logMessage.append(" > ").append(path).append(" : ").append(duplicatedBlockData.get(path).size());
		}
		
		return logMessage.toString();
	}

	public synchronized void putComplexityList(List<ComplexityResult> list) {
		for (ComplexityResult result : list) {
			if (haveToSkip(result.getPath())) {
				continue;
			}
			
			if (individualMode.isComplexity()) {
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
					
					complexityListOver10.add(result);
				}
			}
			
			allMethodList.add(result);
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
	
	public int getComplexityEqualOrOver50() {
		return complexityEqualOrOver50;
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
	
	public String getComplexityEqualOrOver50Percent() {
		return String.format("%.2f%%", (double) complexityEqualOrOver50 / (double) complexityFunctions * 100);
	}
	
	public synchronized void putPmdList(List<PmdResult> list) {		
		for (PmdResult result : list) {
			if (haveToSkip(result.getPath())) {
				continue;
			}
			pmdCount[0]++;
			
			pmdCount[result.getPriority()]++;
			
			pmdList.add(result);
			
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
		for (FindBugsResult result : list) {
			if (haveToSkip(result.getPackageName().replaceAll("\\.", "/") + "/" + result.getFile())) {
				continue;
			}
			findBugsCount[0]++;
			
			findBugsCount[result.getPriority()]++;
			
			findBugsList.add(result);
		}
	}
	
	public int getFindBugsCountAll() {
		return findBugsCount[0];
	}
	
	public int getFindBugsCount(int priority) {
		return findBugsCount[priority];
	}
	
	public synchronized void putFindSecBugsList(List<FindBugsResult> list) {
		for (FindBugsResult result : list) {
			if (haveToSkip(result.getPackageName().replaceAll("\\.", "/") + "/" + result.getFile())) {
				continue;
			}
			findSecBugsCount[0]++;
			
			findSecBugsCount[result.getPriority()]++;
			
			findSecBugsList.add(result);
		}
	}
	
	public int getFindSecBugsCountAll() {
		return findSecBugsCount[0];
	}
	
	public int getFindSecBugsCount(int priority) {
		return findSecBugsCount[priority];
	}
	
	public synchronized List<String> getPackageList() {
		if (packageList.size() == 0) {
			LOGGER.info("Get target packages");
			
			List<String> allPackages = PackageUtils.getProjectPackages(projectDirectory + File.separator + binary);
			
			for (String sourcePackage : allPackages) {
				if (haveToSkip(sourcePackage.replaceAll("\\.", "/") + "/*.java", true)) {
					continue;
				}
				packageList.add(sourcePackage);
			}
			
			/*
			for (ComplexityResult result : allMethodList) {
				if (packageList.contains(result.getPackageName())) {
					continue;
				}
				packageList.add(result.getPackageName());
			}
			*/
		}		
		
		return packageList;
	}
	
	public List<DuplicationResult> getDulicationList() {
		return duplicationList;
	}
	
	public List<ComplexityResult> getComplexityAllList() {
		return allMethodList;
	}
	
	public List<ComplexityResult> getComplexityList() {
		return complexityListOver10;
	}
	
	public List<PmdResult> getPmdList() {
		return pmdList;
	}
	
	public List<FindBugsResult> getFindBugsList() {
		return findBugsList;
	}
	
	public List<FindBugsResult> getFindSecBugsList() {
		return findSecBugsList;
	}
	
	public void addAcyclicDependency(String acyclicDependency) {
		acyclicDependencyList.add(acyclicDependency);
	}
	
	public int getAcyclicDependencyCount() {
		return acyclicDependencyList.size();
	}
	
	public List<String> getAcyclicDependencyList() {
		return acyclicDependencyList;
	}

	public void setMode(MeasurementMode mode) {
		this.mode = mode;
	}
	
	public MeasurementMode getMode() {
		return mode;
	}

	public void setOutputFile(File file) {
		this.outputFile = file;
	}
	
	public File getOutputFile() {
		return outputFile;
	}
	
	public void setIncludeFilters(String includes) {
		FilePathIncludeFilter filter = new FilePathIncludeFilter(includes);
		
		filePathFilterList.add(filter);
		
		this.includes = filter.getNormalizedFilterString(); 
	}
	
	public void setExcludeFilters(String excludes) {
		FilePathExcludeFilter filter = new FilePathExcludeFilter(excludes);
		
		filePathFilterList.add(filter);
		
		this.excludes = filter.getNormalizedFilterString(); 
	}

	public String getIndividualModeString() {
		return individualModeString;
	}

	public void setIndividualModeString(String individualMode) {
		this.individualModeString = individualMode;
	}

	public void setIndividualMode(IndividualMode individualMode) {
		this.individualMode = individualMode;
	}

	public IndividualMode getIndividualMode() {
		return individualMode;
	}

	public String getVersion() {
		return version;
	}
	
	public String getIncludes() {
		return includes;
	}
	
	public String getExcludes() {
		return excludes;
	}
}