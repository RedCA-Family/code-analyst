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
package com.samsungsds.analyst.code.main;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.annotations.JsonAdapter;
import com.samsungsds.analyst.code.api.Language;
import com.samsungsds.analyst.code.checkstyle.CheckStyleResult;
import com.samsungsds.analyst.code.ckmetrics.CkMetricsResult;
import com.samsungsds.analyst.code.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sonar.core.util.stream.MoreCollectors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.jdepend.JDependResult;
import com.samsungsds.analyst.code.main.detailed.Duplication;
import com.samsungsds.analyst.code.main.detailed.DuplicationDetailAnalyst;
import com.samsungsds.analyst.code.main.detailed.Inspection;
import com.samsungsds.analyst.code.main.detailed.InspectionDetailAnalyst;
import com.samsungsds.analyst.code.main.detailed.MartinMetrics;
import com.samsungsds.analyst.code.main.filter.FilePathExcludeFilter;
import com.samsungsds.analyst.code.main.filter.FilePathFilter;
import com.samsungsds.analyst.code.main.filter.FilePathIncludeFilter;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.sonar.DuplicationResult;
import com.samsungsds.analyst.code.sonar.SonarIssueResult;
import com.samsungsds.analyst.code.sonar.WebResourceResult;
import com.samsungsds.analyst.code.unusedcode.UnusedCodeResult;
import com.samsungsds.analyst.code.technicaldebt.TechnicalDebtResult;
import xdean.jex.util.reflect.AnnotationUtil;

public class MeasuredResult implements Serializable, FileSkipChecker {
	private static final Logger LOGGER = LogManager.getLogger(MeasuredResult.class);

	private static final long serialVersionUID = 1L;

	private static Map<String, MeasuredResult> instances = new HashMap<>();

	@Expose
	private String language;

	private Language languageType;

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
    private int elapsedAnalysisTime;
	@Expose
	private String pmdRuleSetFile;
	@Expose
	private String findBugsRuleSetFile;
	@Expose
	private String sonarRuleSetFile;

	private Set<String> sonarIssueFilterSet = new HashSet<>();

	@Expose
	private String webapp = "";

	@Expose
	private String includes = "";
	@Expose
	private String excludes = "";

	private MeasurementMode mode;

	@Expose
	@SerializedName("mode")
	private String individualModeString = Constants.DEFAULT_ANALYSIS_MODE;

	@Expose
	private boolean detailAnalysis = false;

	@Expose
	private boolean seperatedOutput = false;

	@Expose
	private boolean saveCatalog = false;

	private IndividualMode individualMode;

	private boolean debug = false;

	@Expose
	private String version = Version.DOCUMENT_VERSION;
	@Expose
	private String engineVersion = Version.CODE_ANALYST;

	@Expose
	private int directories = 0;
	@Expose
	private int files = 0;
	@Expose
	private int classes = 0;
	@Expose
	private int commentLines = 0;
	@Expose
	private int functions = 0;
	@Expose
	private int lines = 0;
	@Expose
	private int ncloc = 0;
	@Expose
	private int statements = 0;

	@Expose
    @JsonAdapter(FilePathInfoAdapter.class)
	private List<FilePathInfo> filePathList = Collections.synchronizedList(new ArrayList<>());

	@Expose
	private List<DuplicationResult> duplicationList = null;

	@Expose
	private int duplicatedBlocks = 0;

	@Expose
	private int duplicatedLines = 0;

	private Map<String, Set<Integer>> duplicatedBlockData = new HashMap<>();

	private DuplicationDetailAnalyst duplicationDetailAnalyst = new DuplicationDetailAnalyst();

	@Expose
	private List<Duplication> topDuplicationList = null;

	// contains all method for complexity mode
	private List<ComplexityResult> allMethodList = Collections.synchronizedList(new ArrayList<>());

	// used to check target package (include or exclude)
	private List<String> packageList = Collections.synchronizedList(new ArrayList<>());

	@Expose
	@SerializedName("complexityList")
	private List<ComplexityResult> complexityListOver20 = null;

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
	private int sonarJavaRules = 0;
	@Expose
	private int pmdRules = 0;
	@Expose
	private int findBugsRules = 0;
	@Expose
	private int findSecBugsRules = 0;
	@Expose
	private int sonarJSRules = 0;
    @Expose
    private int sonarCSharpRules = 0;
    @Expose
    private int sonarPythonRules = 0;

    @Expose
	@SerializedName("sonarJavaList")
	private List<SonarIssueResult> sonarIssueList = null;

	@Expose
	@SerializedName("sonarJavaCount")
	private int[] sonarIssueCount = new int[6]; // 0 : 전체, 1 ~ 5 (Priority)
	@Expose
	@SerializedName("sonarJavaType")
	private int[] sonarIssueType = new int[4];	// 0 : NA, 1 : Bug, 2 : Vulnerability, 3 : Code Smell

	@Expose
	private List<PmdResult> pmdList = null;
	@Expose
	private int[] pmdCount = new int[6]; // 0 : 전체, 1 ~ 5 (Priority)
	@Expose
	private int[] pmdType = new int[4];	// 0 : NA, 1 : Bug, 2 : Vulnerability, 3 : Code Smell

	@Expose
	private List<FindBugsResult> findBugsList = null;
	@Expose
	private int[] findBugsCount = new int[6]; // 0 : 전체, 1 ~ 5 (High, Normal, Low, Experimental, Ignore)
	@Expose
	private int[] findBugsType = new int[4];	// 0 : NA, 1 : Bug, 2 : Vulnerability, 3 : Code Smell

	@Expose
	private List<FindBugsResult> findSecBugsList = null;
	@Expose
	private int[] findSecBugsCount = new int[6]; // 0 : 전체, 1 ~ 5 (High, Normal, Low, Experimental, Ignore)

	@Expose
	private List<WebResourceResult> webResourceList = null;
	@Expose
	private int[] webResourceCount = new int[6]; // 0 : 전체, 1 ~ 5 (Priority)
	@Expose
	private int[] webResourceType = new int[4];	// 0 : NA, 1 : Bug, 2 : Vulnerability, 3 : Code Smell

	@Expose
	private List<JDependResult> acyclicDependencyList = null;

	private List<FilePathFilter> filePathFilterList = new ArrayList<>();

	private File outputFile;

	private boolean withDefaultPackageClasses = false;

	private List<CSVFileCollectionList<?>> closeTargetList = new ArrayList<>();

	@Expose
	private List<MartinMetrics> topMartinMetricsList = null;

	private InspectionDetailAnalyst inspectionDetailAnalyst = new InspectionDetailAnalyst();

	@Expose
	@SerializedName("topSonarJavaList")
	private List<Inspection> topSonarIssueList = null;

	@Expose
	private List<Inspection> topPmdList = null;

	@Expose
	private List<Inspection> topFindBugsList = null;

	@Expose
	private List<UnusedCodeResult> unusedCodeList = null;

	@Expose
	private int unusedCodeCount = 0;

	@Expose
	private int ucTotalClassCount = 0;
	@Expose
	private int ucTotalMethodCount = 0;
	@Expose
	private int ucTotalFieldCount = 0;
	@Expose
	private int ucTotalConstantCount = 0;
	@Expose
	private int unusedClassCount = 0;
	@Expose
	private int unusedMethodCount = 0;
	@Expose
	private int unusedFieldCount = 0;
	@Expose
	private int unusedConstantCount = 0;

	@Expose
	private List<CkMetricsResult> ckMetricsResultList = null;

    @Expose
    private List<CheckStyleResult> checkStyleList = null;

    @Expose
    private int checkStyleCount = 0;

	@Expose
	private TechnicalDebtResult technicalDebtResult = null;

	@Expose
	@SerializedName("tokenBasedCPD")
	private boolean tokenBased = false;

	@Expose
	private int minimumTokens = 100;

	private String nodeExecutablePath;
	private String nodeVersion;

	public static MeasuredResult getInstance(String instanceKey) {
		if (!instances.containsKey(instanceKey)) {
			synchronized (MeasuredResult.class) {
				if (!instances.containsKey(instanceKey)) {
					instances.put(instanceKey, new MeasuredResult());
				}
			}
		}

		return instances.get(instanceKey);
	}

	public static void removeInstance(String instanceKey) {
		if (instances.containsKey(instanceKey)) {
			synchronized (MeasuredResult.class) {
				if (instances.containsKey(instanceKey)) {
					instances.remove(instanceKey);
				}
			}
		}
	}

	public void initialize(boolean detailAnalysis, boolean seperatedOutput) {
		IndividualMode defaultMode = new IndividualMode();
		defaultMode.setDefault();

		initialize(detailAnalysis, seperatedOutput, defaultMode);
	}

	public void initialize(boolean detailAnalysis, boolean seperatedOutput, IndividualMode individualMode) {
		this.detailAnalysis = detailAnalysis;
		this.seperatedOutput = seperatedOutput;

		if (detailAnalysis) {
			duplicationList = Collections.synchronizedList(new ArrayList<>());
			complexityListOver20 = Collections.synchronizedList(new ArrayList<>());
			sonarIssueList = Collections.synchronizedList(new ArrayList<>());
			pmdList = Collections.synchronizedList(new ArrayList<>());
			findBugsList = Collections.synchronizedList(new ArrayList<>());
			findSecBugsList = Collections.synchronizedList(new ArrayList<>());
			webResourceList = Collections.synchronizedList(new ArrayList<>());
			acyclicDependencyList = Collections.synchronizedList(new ArrayList<>());
			ckMetricsResultList = Collections.synchronizedList(new ArrayList<>());
			unusedCodeList = Collections.synchronizedList(new ArrayList<>());
			checkStyleList = Collections.synchronizedList(new ArrayList<>());
		} else {
			if (individualMode.isDuplication()) {
				duplicationList = makeCSVFileCollectionList(DuplicationResult.class, this);
			} else {
				duplicationList = new ArrayList<>(0);
			}
			if (individualMode.isComplexity()) {
				complexityListOver20 = makeCSVFileCollectionList(ComplexityResult.class, this);
			} else {
				complexityListOver20 = new ArrayList<>(0);
			}
			if (individualMode.isSonarJava()) {
				sonarIssueList = makeCSVFileCollectionList(SonarIssueResult.class, this);
			} else {
				sonarIssueList = new ArrayList<>(0);
			}
			if (individualMode.isPmd()) {
				pmdList = makeCSVFileCollectionList(PmdResult.class, this);
			} else {
				pmdList = new ArrayList<>(0);
			}
			if (individualMode.isFindBugs()) {
				findBugsList = makeCSVFileCollectionList(FindBugsResult.class, this);
			} else {
				findBugsList = new ArrayList<>(0);
			}
			if (individualMode.isFindSecBugs()) {
				findSecBugsList = makeCSVFileCollectionList(FindBugsResult.class, this);
			} else {
				findSecBugsList = new ArrayList<>(0);
			}
			if (individualMode.isWebResources()) {
				webResourceList = makeCSVFileCollectionList(WebResourceResult.class, this);
			} else {
				webResourceList = new ArrayList<>(0);
			}
			if (individualMode.isDependency()) {
				acyclicDependencyList = makeCSVFileCollectionList(JDependResult.class, this);
			} else {
				acyclicDependencyList = new ArrayList<>(0);
			}
			if (individualMode.isCkMetrics()) {
				ckMetricsResultList = makeCSVFileCollectionList(CkMetricsResult.class, this);
			} else {
				ckMetricsResultList = new ArrayList<>(0);
			}
			if (individualMode.isUnusedCode()) {
				unusedCodeList = makeCSVFileCollectionList(UnusedCodeResult.class, this);
			} else {
                unusedCodeList = new ArrayList<>(0);
            }
			if (individualMode.isCheckStyle()) {
			    checkStyleList = makeCSVFileCollectionList(CheckStyleResult.class, this);
            } else {
                checkStyleList = new ArrayList<>(0);
            }
		}
	}

	public static <T extends CSVFileResult> List<T> makeCSVFileCollectionList(Class<T> clazz, MeasuredResult target) {

		CSVFileCollectionList<T> list = new CSVFileCollectionList<T>(clazz);
		target.closeTargetList.add(list);

		return Collections.synchronizedList(list);
	}

	public void setProjectInfo(CliParser cli) {
		language = cli.getLanguage();
		languageType = cli.getLanguageType();

		source = cli.getSrc();
		binary = cli.getBinary();
		encoding = cli.getEncoding();
		javaVersion = cli.getJavaVersion();
		dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
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
		if (cli.getRuleSetFileForSonar() != null && !cli.getRuleSetFileForSonar().equals("")) {
			sonarRuleSetFile = cli.getRuleSetFileForSonar();
		} else {
			sonarRuleSetFile = "";
		}

		webapp = cli.getWebapp();
	}

	public void setChangeSourceAndBinary(String source, String binary) {
        this.source = source;
        this.binary = binary;
    }

	public void setDebug(boolean debug) {
	    this.debug = debug;
    }

    public boolean isDebug() {
	    return debug;
    }

	public void changeSerializedName(CliParser cli) {
        if (cli.getLanguageType() == Language.JAVA) {
            if (cli.getIndividualMode().isSonarJava() && cli.getIndividualMode().isJavascript()) {
                changeSerializedName("sonarIssueList", "sonarIssueList");
                changeSerializedName("sonarIssueCount", "sonarIssueCount");
                changeSerializedName("sonarIssueType", "sonarIssueType");
                changeSerializedName("topSonarIssueList", "topSonarIssueList");
            } else if (cli.getIndividualMode().isSonarJava()) {
                changeSerializedName("sonarIssueList", "sonarJavaList");
                changeSerializedName("sonarIssueCount", "sonarJavaCount");
                changeSerializedName("sonarIssueType", "sonarJavaType");
                changeSerializedName("topSonarIssueList", "topSonarJavaList");
            } else if (cli.getIndividualMode().isJavascript()) {
                changeSerializedName("sonarIssueList", "sonarJSList");
                changeSerializedName("sonarIssueCount", "sonarJSCount");
                changeSerializedName("sonarIssueType", "sonarJSType");
                changeSerializedName("topSonarIssueList", "topSonarJSList");
            }
        } else if (cli.getLanguageType() == Language.JAVASCRIPT) {
            changeSerializedName("sonarIssueList", "sonarJSList");
            changeSerializedName("sonarIssueCount", "sonarJSCount");
            changeSerializedName("sonarIssueType", "sonarJSType");
            changeSerializedName("topSonarIssueList", "topSonarJSList");
        } else if (cli.getLanguageType() == Language.CSHARP) {
            changeSerializedName("sonarIssueList", "sonarCSharpList");
            changeSerializedName("sonarIssueCount", "sonarCSharpCount");
            changeSerializedName("sonarIssueType", "sonarCSharpType");
            changeSerializedName("topSonarIssueList", "topSonarCSharpList");
        } else {    // Python
            changeSerializedName("sonarIssueList", "sonarPythonList");
            changeSerializedName("sonarIssueCount", "sonarPythonCount");
            changeSerializedName("sonarIssueType", "sonarPythonType");
            changeSerializedName("topSonarIssueList", "topSonarPythonList");
        }
	}

	private void changeSerializedName(String fieldName, String newSerializedName) {
		try {
			Field declaredAnnotations = MeasuredResult.class.getDeclaredField(fieldName);
			declaredAnnotations.setAccessible(true);
			SerializedName serializedName = declaredAnnotations.getAnnotation(SerializedName.class);
			String oldName = serializedName.value();

			AnnotationUtil.changeAnnotationValue(serializedName, "value", newSerializedName);

			String newName = MeasuredResult.class.getDeclaredField(fieldName).getAnnotation(SerializedName.class).value();
			LOGGER.debug("SerializedName changed from {} to {}", oldName, newName);
		} catch (NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}
	}

	public String getLanguage() {
		return language;
	}

	public Language getLanguageType() {
		return languageType;
	}

	public boolean isSaveCatalog() {
		return saveCatalog;
	}

	public void setSaveCatalog(boolean saveCatalog) {
		this.saveCatalog = saveCatalog;
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

	public List<String> getFilePathList() {
	    return filePathList.stream()
            .map(file -> new String(file.getPath()))
            .collect(Collectors.toList());
	}

	public void addFilePathList(String moduleName, String filePath) {
		filePathList.add(new FilePathInfo(moduleName, filePath));
	}

	public int getDuplicatedBlocks() {
		return duplicatedBlocks;
	}

	public void addDuplicatedBlocks() {
		this.duplicatedBlocks++;
	}

	public int getDuplicatedLines() {
		return duplicatedLines;
	}

	public String getDuplicatedLinesPercent() {
		return String.format("%.2f%%", (double) duplicatedLines / (double) lines * 100);
	}

	public void setSonarIssueFilterSet(Set<String> sonarIssueFilterSet) {
		this.sonarIssueFilterSet = sonarIssueFilterSet;
	}

	public synchronized boolean addDuplicationResult(DuplicationResult result) {
		String path = result.getPath();
		String duplicatedPath = null;

		if (DuplicationResult.DUPLICATED_FILE_SAME_MARK.equals(result.getDuplicatedPath())) {
		    // 이 경우 addDuplicatedBlocks() 호출 이유 확인 필요
			//addDuplicatedBlocks();
			duplicatedPath = result.getPath();
		} else {
			duplicatedPath = result.getDuplicatedPath();
		}

		if (haveToSkip(path) || haveToSkip(duplicatedPath)) {
			return false;
		}

		duplicationList.add(result);

		if (duplicatedBlockData.containsKey(path)) {
			this.duplicatedLines += getAddedDuplicatedLines(result.getStartLine(), result.getEndLine(), duplicatedBlockData.get(path));
		} else {
			Set<Integer> duplicatedLineNumbers = new HashSet<>();

			this.duplicatedLines += getAddedDuplicatedLines(result.getStartLine(), result.getEndLine(), duplicatedLineNumbers);

			duplicatedBlockData.put(path, duplicatedLineNumbers);
		}

		if (duplicatedBlockData.containsKey(duplicatedPath)) {
			this.duplicatedLines += getAddedDuplicatedLines(result.getDuplicatedStartLine(), result.getDuplicatedEndLine(), duplicatedBlockData.get(duplicatedPath));
		} else {
			Set<Integer> duplicatedLineNumbers = new HashSet<>();

			this.duplicatedLines += getAddedDuplicatedLines(result.getDuplicatedStartLine(), result.getDuplicatedEndLine(), duplicatedLineNumbers);

			duplicatedBlockData.put(duplicatedPath, duplicatedLineNumbers);
		}

		if (detailAnalysis) {
			duplicationDetailAnalyst.add(result);
		}

		return true;
	}

	private int getAddedDuplicatedLines(int from, int to, Set<Integer> duplicatedLineNumbers) {
		int beforeLines = duplicatedLineNumbers.size();
		for (int i = from; i <= to; i++) {
			duplicatedLineNumbers.add(i);
		}
		int afterLines = duplicatedLineNumbers.size();

		return (afterLines - beforeLines);
	}

	public boolean haveToSkip(String path) {
		return haveToSkip(path, false, false);
	}

	public boolean haveToSkip(String path, boolean addSrcPrefix) {
		return haveToSkip(path, addSrcPrefix, false);
	}

	public boolean haveToSkip(String path, boolean addSrcPrefix, boolean withoutFilename) {
	    path = path.replace("\\", "/");

		String[] pathArray;

		if (addSrcPrefix) {
			String[] sourceDirectories = source.split(FindFileUtils.COMMA_SPLITTER);

			pathArray = new String[sourceDirectories.length];

			for (int i = 0; i < sourceDirectories.length; i++) {
				pathArray[i] = sourceDirectories[i] + (path.startsWith("/") ? path : "/" + path);
			}
		} else {
			pathArray = new String[1];
			pathArray[0] = path;
		}

		for (String pathDirectory : pathArray) {
			for (FilePathFilter filter : filePathFilterList) {
				if (!filter.matched(pathDirectory, withoutFilename)) {
					return true;
				}
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

				if (result.getComplexity() >= 50) {
					complexityEqualOrOver50++;
				}
				if (result.getComplexity() > 20) {
					complexityOver20++;

					complexityListOver20.add(result);
				}
				if (result.getComplexity() > 15) {
					complexityOver15++;
				}
				if (result.getComplexity() > 10) {
					complexityOver10++;
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

			pmdType[result.getIssueType().getTypeIndex()]++;

			pmdList.add(result);

			if (detailAnalysis) {
				inspectionDetailAnalyst.add(result);
			}

			LOGGER.debug("file : {}, line : {}, priority : {}, rule : {}, desc : {}", result.getFile(), result.getLine(), result.getPriority(), result.getRule(), result.getDescription());
		}
	}

	public int getPmdCountAll() {
		return pmdCount[0];
	}

	public int getPmdCount(int priority) {
		return pmdCount[priority];
	}

	public int getPmdType(int index) {
		return pmdType[index];
	}

	public synchronized void putFindBugsList(List<FindBugsResult> list) {
		for (FindBugsResult result : list) {
			if (haveToSkip(result.getPackageName().replaceAll("\\.", "/") + "/" + result.getFile(), true)) {
				continue;
			}
			findBugsCount[0]++;

			findBugsCount[result.getPriority()]++;

			findBugsType[result.getIssueType().getTypeIndex()]++;

			findBugsList.add(result);

			if (detailAnalysis) {
				inspectionDetailAnalyst.add(result);
			}
		}
	}

	public int getFindBugsCountAll() {
		return findBugsCount[0];
	}

	public int getFindBugsCount(int priority) {
		return findBugsCount[priority];
	}

	public int getFindBugsType(int index) {
		return findBugsType[index];
	}

	public synchronized void putFindSecBugsList(List<FindBugsResult> list) {
		for (FindBugsResult result : list) {
			if (haveToSkip(result.getPackageName().replaceAll("\\.", "/") + "/" + result.getFile(), true)) {
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

			String[] binaryDirectories = binary.split(FindFileUtils.COMMA_SPLITTER);

			for (String dir : binaryDirectories) {
				List<String> allPackages = PackageUtils.getProjectPackages(projectDirectory + File.separator + dir);

				for (String sourcePackage : allPackages) {
					if (haveToSkip(sourcePackage.replaceAll("\\.", "/") + "/*.java", true, true)) {
						continue;
					}
					if (!packageList.contains(sourcePackage)) {
						packageList.add(sourcePackage);
					}
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

		}

		return packageList;
	}

	public List<DuplicationResult> getDuplicationList() {
		processTopDuplicationList();

		return duplicationList;
	}

	private void processTopDuplicationList() {
		if (topDuplicationList == null) {
			topDuplicationList = duplicationDetailAnalyst.getTopList();
		}
	}

	public List<Duplication> getTopDuplicationList() {
		if (detailAnalysis) {
			return topDuplicationList;
		} else {
			throw new IllegalStateException("getTopDUplicationList() can be called only detailed analysis mode.");
		}
	}

	public List<ComplexityResult> getComplexityAllList() {
		return allMethodList;
	}

	public List<ComplexityResult> getComplexityList() {
		return complexityListOver20;
	}

	public int getSonarJavaRules() {
		return sonarJavaRules;
	}

	public void setSonarJavaRules(int sonarJavaRules) {
		this.sonarJavaRules = sonarJavaRules;
	}

	public int getPmdRules() {
		return pmdRules;
	}

	public void setPmdRules(int pmdRules) {
		this.pmdRules = pmdRules;
	}

	public int getFindBugsRules() {
		return findBugsRules;
	}

	public void setFindBugsRules(int findBugsRules) {
		this.findBugsRules = findBugsRules;
	}

	public int getFindSecBugsRules() {
		return findSecBugsRules;
	}

	public void setFindSecBugsRules(int findSecBugsRules) {
		this.findSecBugsRules = findSecBugsRules;
	}

	public int getSonarJSRules() {
		return sonarJSRules;
	}

	public void setSonarJSRules(int sonarJSRules) {
		this.sonarJSRules = sonarJSRules;
	}

    public int getSonarCSharpRules() {
        return sonarCSharpRules;
    }

    public void setSonarCSharpRules(int sonarCSharpRules) {
        this.sonarCSharpRules = sonarCSharpRules;
    }

    public int getSonarPythonRules() {
        return sonarPythonRules;
    }

    public void setSonarPythonRules(int sonarPythonRules) {
        this.sonarPythonRules = sonarPythonRules;
    }

    public String getWebapp() {
		return webapp;
	}

	public List<SonarIssueResult> getSonarIssueList() {
		processTopSonarIssueList();

		return sonarIssueList;
	}

	private void processTopSonarIssueList() {
		if (topSonarIssueList == null) {
			topSonarIssueList = inspectionDetailAnalyst.getTopSonarIssueList();
		}
	}

	public List<Inspection> getTopSonarIssueList() {
		if (detailAnalysis) {
			return topSonarIssueList;
		} else {
			throw new IllegalStateException("getTopSonarIssueList() can be called only detailed analysis mode.");
		}
	}

	public void addSonarIssueResult(SonarIssueResult sonarIssueResult) {
		String ruleKey = sonarIssueResult.getRuleRepository() + ":" + sonarIssueResult.getRuleKey();
		if (sonarIssueFilterSet.contains(ruleKey)) {
			LOGGER.debug("Sonar Rule exclude : {}", ruleKey);
			return;
		}
		sonarIssueList.add(sonarIssueResult);
		sonarIssueCount[0]++;
		sonarIssueCount[sonarIssueResult.getSeverity()]++;

		sonarIssueType[sonarIssueResult.getIssueType().getTypeIndex()]++;

		if (detailAnalysis) {
			inspectionDetailAnalyst.add(sonarIssueResult);
		}
	}

	public int getSonarIssueCountAll() {
		return sonarIssueCount[0];
	}

	public int getSonarIssueCount(int priority) {
		return sonarIssueCount[priority];
	}

	public int getSonarIssueType(int index) {
		return sonarIssueType[index];
	}

	public String getSonarIssueTitle() {
		if (languageType == Language.JAVASCRIPT) {
            return "SonarJS";
        } else if (languageType == Language.CSHARP) {
            return "SonarCSharp";
        } else if (languageType == Language.PYTHON) {
		    return "SonarPython";
		} else {	// Language.JAVA
			if (individualMode.isJavascript()) {
				return "SonarIssue";
			} else {
				return "SonarJava";
			}
		}
	}

	public List<PmdResult> getPmdList() {
		processTopPmdList();

		return pmdList;
	}

	private void processTopPmdList() {
		if (topPmdList == null) {
			topPmdList = inspectionDetailAnalyst.getTopPmdList();
		}
	}

	public List<Inspection> getTopPmdList() {
		if (detailAnalysis) {
			return topPmdList;
		} else {
			throw new IllegalStateException("getTopPmdList() can be called only detailed analysis mode.");
		}
	}

	public List<FindBugsResult> getFindBugsList() {
		processTopFindBugsList();

		return findBugsList;
	}

	private void processTopFindBugsList() {
		if (topFindBugsList == null) {
			topFindBugsList = inspectionDetailAnalyst.getTopFindBugsList();
		}
	}

	public List<Inspection> getTopFindBugsList() {
		if (detailAnalysis) {
			return topFindBugsList;
		} else {
			throw new IllegalStateException("getTopFindBugsList() can be called only detailed analysis mode.");
		}
	}

	public List<FindBugsResult> getFindSecBugsList() {
		return findSecBugsList;
	}

	public List<WebResourceResult> getWebResourceList() {
		return webResourceList;
	}

	public void addWebResourceResult(WebResourceResult webResourceResult) {
		String ruleKey = webResourceResult.getRuleRepository() + ":" + webResourceResult.getRuleKey();
		if (sonarIssueFilterSet.contains(ruleKey)) {
			LOGGER.debug("WebResource Rule exclude : {}", ruleKey);
			return;
		}

		webResourceList.add(webResourceResult);
		webResourceCount[0]++;
		webResourceCount[webResourceResult.getSeverity()]++;

		webResourceType[webResourceResult.getIssueType().getTypeIndex()]++;
	}

	public int getWebResourceCountAll() {
		return webResourceCount[0];
	}

	public int getWebResourceCount(int priority) {
		return webResourceCount[priority];
	}

	public int getWebResourceType(int index) {
		return webResourceType[index];
	}

	public void putUnusedCodeList(List<UnusedCodeResult> unusedCodeResultList) {
		this.unusedCodeList = unusedCodeResultList;
		this.unusedCodeCount = unusedCodeResultList.size();
	}

	public List<UnusedCodeResult> getUnusedCodeList() {
		return this.unusedCodeList;
	}

    public int getUnusedCodeCount() {
        return unusedCodeCount;
    }

    public int getUcTotalClassCount() {
		return ucTotalClassCount;
	}

	public void setUcTotalClassCount(int ucTotalClassCount) {
		this.ucTotalClassCount = ucTotalClassCount;
	}

	public int getUcTotalMethodCount() {
		return ucTotalMethodCount;
	}

	public void setUcTotalMethodCount(int ucTotalMethodCount) {
		this.ucTotalMethodCount = ucTotalMethodCount;
	}

	public int getUcTotalFieldCount() {
		return ucTotalFieldCount;
	}

	public void setUcTotalFieldCount(int ucTotalFieldCount) {
		this.ucTotalFieldCount = ucTotalFieldCount;
	}

	public int getUcTotalConstantCount() {
		return ucTotalConstantCount;
	}

	public void setUcTotalConstantCount(int ucTotalConstantCount) {
		this.ucTotalConstantCount = ucTotalConstantCount;
	}

	public int getUnusedClassCount() {
		return unusedClassCount;
	}

	public void setUnusedClassCount(int unusedClassCount) {
		this.unusedClassCount = unusedClassCount;
	}

	public int getUnusedMethodCount() {
		return unusedMethodCount;
	}

	public void setUnusedMethodCount(int unusedMethodCount) {
		this.unusedMethodCount = unusedMethodCount;
	}

	public int getUnusedFieldCount() {
		return unusedFieldCount;
	}

	public void setUnusedFieldCount(int unusedFieldCount) {
		this.unusedFieldCount = unusedFieldCount;
	}

	public int getUnusedConstantCount() {
		return unusedConstantCount;
	}

	public void setUnusedConstantCount(int unusedConstantCount) {
		this.unusedConstantCount = unusedConstantCount;
	}

	public void addAcyclicDependency(String acyclicDependency) {
		acyclicDependencyList.add(new JDependResult(acyclicDependency));
	}

	public int getAcyclicDependencyCount() {
		return acyclicDependencyList.size();
	}

	public List<String> getAcyclicDependencyList() {
		return acyclicDependencyList.stream().map(s -> s.getAcyclicDependencies()).collect(MoreCollectors.toList());
	}

	public void setCkMetricsResultList(List<CkMetricsResult> ckMetricsResultList) {
		//this.ckMetricsResultList = ckMetricsResultList;
        this.ckMetricsResultList.clear();

        for (CkMetricsResult result : ckMetricsResultList) {
            if (haveToSkip(result.getFilePath())) {
                continue;
            }

            this.ckMetricsResultList.add(result);
        }
	}

	public List<CkMetricsResult> getCkMetricsResultList() {
		return ckMetricsResultList;
	}

    public synchronized void putCheckStyleList(List<CheckStyleResult> list) {
        for (CheckStyleResult result : list) {
            if (haveToSkip(result.getPath())) {
                continue;
            }

            checkStyleList.add(result);
            checkStyleCount++;
            LOGGER.debug("file : {}, line : {}, severity : {}, message : {}, checker : {}", result.getPath(), result.getLine(), result.getSeverity(), result.getMessage(), result.getChecker());
        }
    }

    public List<CheckStyleResult> getCheckStyleList() {
        return checkStyleList;
    }

    public int getCheckStyleCount() {
        return checkStyleCount;
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
		assert source != null : "source have to be set in advance.";

		FilePathIncludeFilter filter = new FilePathIncludeFilter(includes, source, webapp);

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

	public String getEngineVersion() {
		return engineVersion;
	}

	public String getIncludes() {
		return includes;
	}

	public String getExcludes() {
		return excludes;
	}

	public boolean isWithDefaultPackageClasses() {
		return withDefaultPackageClasses;
	}

	public void setWithDefaultPackageClasses(boolean withDefaultPackageClasses) {
		this.withDefaultPackageClasses = withDefaultPackageClasses;
	}

	public boolean isDetailAnalysis() {
		return detailAnalysis;
	}

	public boolean isSeperatedOutput() {
		return seperatedOutput;
	}

    public int getElapsedAnalysisTime() {
        return elapsedAnalysisTime;
    }

    public void clearSeperatedList() {
		if (detailAnalysis) {
			duplicationList.clear();
			complexityListOver20.clear();
			sonarIssueList.clear();
			pmdList.clear();
			findBugsList.clear();
			findSecBugsList.clear();
			webResourceList.clear();
			ckMetricsResultList.clear();
			unusedCodeList.clear();
			checkStyleList.clear();
		} else {
			for (CSVFileCollectionList<?> list : closeTargetList) {
				if (list.isTypeOf(JDependResult.class)) {
					continue;
				}

				try {
					list.close();
				} catch (IOException ex) {
					LOGGER.warn("CSVFileCollectionList close IOException : " + ex.getMessage());
				}
			}

			duplicationList = null;
			complexityListOver20 = null;
			sonarIssueList = null;
			pmdList = null;
			findBugsList = null;
			findSecBugsList = null;
			webResourceList = null;
			unusedCodeList = null;
			ckMetricsResultList = null;
			unusedCodeList = null;
			checkStyleList = null;
		}
	}

	public void setTopMartinMetrics(List<MartinMetrics> topMartinMetricsList) {
		this.topMartinMetricsList = topMartinMetricsList;
	}

	public List<MartinMetrics> getTopMartinMetrics() {
		return topMartinMetricsList;
	}

	public void setTechnicalDebtResult(TechnicalDebtResult technicalDebtResult) {
		this.technicalDebtResult = technicalDebtResult;
	}

	public TechnicalDebtResult getTechnicalDebt() {
		return technicalDebtResult;
	}

    public void calculateElapsedTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date startDate = df.parse(dateTime);

            Date endDate = new Date();

            long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
            long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);

            elapsedAnalysisTime = (int)diff + 1;

        } catch (ParseException ex) {
            throw new IllegalStateException("Date format error", ex);
        }
    }

	public boolean isTokenBased() {
		return tokenBased;
	}

	public void setTokenBased(boolean tokenBased) {
		this.tokenBased = tokenBased;
	}

	public int getMinimumTokens() {
		return minimumTokens;
	}

	public void setMinimumTokens(int minimumTokens) {
		this.minimumTokens = minimumTokens;
	}

	public String getNodeExecutablePath() {
		return nodeExecutablePath;
	}

	public void setNodeExecutablePath(String nodeExecutablePath) {
		this.nodeExecutablePath = nodeExecutablePath;
	}

	public String getNodeVersion() {
		return nodeVersion;
	}

	public void setNodeVersion(String nodeVersion) {
		this.nodeVersion = nodeVersion;
	}

	public void clear() {
		directories = 0;
		files = 0;
		classes = 0;
		commentLines = 0;
		functions = 0;
		lines = 0;
		ncloc = 0;
		statements = 0;

		sonarIssueFilterSet = new HashSet<>();

		duplicatedLines = 0;
		duplicatedBlockData.clear();

		allMethodList.clear();
		packageList.clear();

		filePathList.clear();

		complexityFunctions = 0;
		complexitySum = 0;
		complexityOver10 = 0;
		complexityOver15 = 0;
		complexityOver20 = 0;
		complexityEqualOrOver50 = 0;

		sonarJavaRules = 0;
		pmdRules = 0;
		findBugsRules = 0;
		sonarJSRules = 0;

		for (int i = 0; i < pmdCount.length; i++) {
			pmdCount[i] = 0;
		}

		for (int i = 0; i < findBugsCount.length; i++) {
			findBugsCount[i] = 0;
		}

		for (int i = 0; i < findSecBugsCount.length; i++) {
			findSecBugsCount[i] = 0;
		}

		for (int i = 0; i < webResourceCount.length; i++) {
			webResourceCount[i] = 0;
		}

        unusedCodeCount = 0;
        checkStyleCount = 0;

		filePathFilterList.clear();

		withDefaultPackageClasses = false;

		detailAnalysis = false;
		seperatedOutput = false;
		saveCatalog = false;

		if (detailAnalysis) {
			duplicationList.clear();
			complexityListOver20.clear();
			sonarIssueList.clear();
			pmdList.clear();
			findBugsList.clear();
			findSecBugsList.clear();
			webResourceList.clear();
			acyclicDependencyList.clear();
			ckMetricsResultList.clear();
			unusedCodeList.clear();
			checkStyleList.clear();
		} else {
			for (CSVFileCollectionList<?> list : closeTargetList) {
				try {
					list.close();
				} catch (IOException ex) {
					LOGGER.warn("CSVFileCollectionList close IOException : " + ex.getMessage());
				}
			}

			duplicationList = null;

		}

		topDuplicationList = null;
		topSonarIssueList = null;
		topPmdList = null;
		topFindBugsList = null;
		technicalDebtResult = null;
		unusedCodeList = null;
		ckMetricsResultList = null;
		unusedCodeList = null;
		checkStyleList = null;
	}

	public static String getConvertedFilePath(String filePath, String projectDirectory) {
		String path = filePath.replaceAll("\\\\", "/");

		String project = projectDirectory.replaceAll("\\\\", "/");

		if (!project.endsWith("/")) {
			project += "/";
		}

		if (path.startsWith(project)) {
			path = path.substring(project.length());
		}

		return path;
	}
}
