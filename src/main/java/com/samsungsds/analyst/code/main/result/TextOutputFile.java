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
package com.samsungsds.analyst.code.main.result;

import static com.samsungsds.analyst.code.util.CSVUtil.getString;
import static com.samsungsds.analyst.code.util.CSVUtil.getStringsWithComma;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.samsungsds.analyst.code.api.Language;
import com.samsungsds.analyst.code.checkstyle.CheckStyleResult;
import com.samsungsds.analyst.code.ckmetrics.CkMetricsResult;
import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.main.detailed.Duplication;
import com.samsungsds.analyst.code.main.detailed.Inspection;
import com.samsungsds.analyst.code.main.detailed.MartinMetrics;
import com.samsungsds.analyst.code.main.issue.IssueType;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.sonar.DuplicationResult;
import com.samsungsds.analyst.code.sonar.SonarIssueResult;
import com.samsungsds.analyst.code.sonar.WebResourceResult;
import com.samsungsds.analyst.code.unusedcode.UnusedCodeResult;

public class TextOutputFile extends AbstractOutputFile {
	MeasuredResult result;

	CSVSeparatedOutput csvOutput;

	@Override
	protected void open(MeasuredResult result) {
		this.result = result;

		csvOutput = new CSVSeparatedOutput(result);
	}

	@Override
	protected void writeSeparator() {
		writer.println(";===============================================================================");
	}

	@Override
	protected void writeProjectInfo(CliParser cli, MeasuredResult result) {
		writer.println("[Project]");
		writer.println("Language = " + cli.getLanguage());
		writer.println("Target = " + result.getProjectDirectory());
		writer.println("Source = " + cli.getSrc());
		writer.println("Binary = " + cli.getBinary());
		writer.println("Encoding = " + cli.getEncoding());
		writer.println("JavaVersion = " + cli.getJavaVersion());
		writer.println("Datetime = " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		writer.println("ElapsedAnalysisTime = " + result.getElapsedAnalysisTime());
		writer.println("; Elapsed Analysis Time Unit : Minutes ");
		if (cli.getRuleSetFileForPMD() != null && !cli.getRuleSetFileForPMD().equals("")) {
			writer.println("PMD = " + cli.getRuleSetFileForPMD());
		}
		if (cli.getRuleSetFileForFindBugs() != null && !cli.getRuleSetFileForFindBugs().equals("")) {
			writer.println("FindBugs = " + cli.getRuleSetFileForFindBugs());
		}
		if (cli.getRuleSetFileForSonar() != null && !cli.getRuleSetFileForSonar().equals("")) {
			writer.println("Sonar = " + cli.getRuleSetFileForSonar());
		}
		if (!"".equals(cli.getIncludes())) {
			writer.println("includes = " + cli.getIncludes());
		}
		if (!"".equals(cli.getExcludes())) {
			writer.println("excludes = " + cli.getExcludes());
		}
		writer.println("mode = " + result.getIndividualModeString());
		if (result.isDetailAnalysis()) {
			writer.println("detailAnalysis = true");
		}
		if (result.isSeperatedOutput()) {
			writer.println("seperatedOutput = true");
		}
		if (result.isSaveCatalog()) {
			writer.println("saveCatalog = true");
		}
		if (result.isTokenBased()) {
			writer.println("duplication = token");
			writer.println("tokens = " + result.getMinimumTokens());
		}
		writer.println("version = " + result.getVersion());
		writer.println("engineVersion = " + result.getEngineVersion());
		writer.println();
		writer.println();
	}

	@Override
	protected void writeSummary(MeasuredResult result) {
		writer.println("[Summary]");
		if (result.getIndividualMode().isCodeSize()) {
			writer.println("Files = " + result.getFiles());
			writer.println("Directories = " + result.getDirectories());
			writer.println("Classes = " + result.getClasses());
			writer.println("Functions = " + result.getFunctions());
			writer.println("Lines = " + result.getLines());
			writer.println("CommentLines = " + result.getCommentLines());
			writer.println("Ncloc = " + result.getNcloc());
			writer.println("Statements = " + result.getStatements());
			writer.println();
		}
		if (result.getIndividualMode().isDuplication()) {
			writer.println("DuplicatedBlocks = " + result.getDuplicatedBlocks());
			writer.println("DuplicatedLines = " + result.getDuplicatedLines());
			writer.println();
		}
		if (result.getIndividualMode().isComplexity()) {
			writer.println("ComplexityFunctions = " + result.getComplexityFunctions());
			writer.println("ComplexityTotal = " + result.getComplexitySum());
			writer.println("ComplexityOver10 = " + result.getComplexityOver10());
			writer.println("ComplexityOver15 = " + result.getComplexityOver15());
			writer.println("ComplexityOver20 = " + result.getComplexityOver20());
			writer.println("ComplexityEqualOrOver50 = " + result.getComplexityEqualOrOver50());
			writer.println();
		}
		if ((result.getLanguageType() == Language.JAVA && result.getIndividualMode().isSonarJava())
				|| (result.getLanguageType() == Language.JAVA && result.getIndividualMode().isJavascript())
				|| (result.getLanguageType() == Language.JAVASCRIPT && result.getIndividualMode().isSonarJS())) {

			String name = result.getSonarIssueTitle();

			if (result.getLanguageType() == Language.JAVA) {
				if (result.getIndividualMode().isJavascript()) {
					writer.println(name + "Rules = " + (result.getSonarJavaRules() + result.getSonarJSRules()));
					writer.println(name + "Rules-Java = " + result.getSonarJavaRules());
					writer.println(name + "Rules-JS = " + result.getSonarJSRules());
				} else {
					writer.println(name + "Rules = " + result.getSonarJavaRules());
				}
			} else if (result.getLanguageType() == Language.JAVASCRIPT) {
				writer.println(name + "Rules = " + result.getSonarJSRules());
			} else if (result.getLanguageType() == Language.CSHARP) {
                writer.println(name + "Rules = " + result.getSonarCSharpRules());
            } else if (result.getLanguageType() == Language.PYTHON) {
                writer.println(name + "Rules = " + result.getSonarPythonRules());
            }
			writer.println(name + " = " + result.getSonarIssueCountAll());
			writer.println(name + "1Priority = " + result.getSonarIssueCount(1));
			writer.println(name + "2Priority = " + result.getSonarIssueCount(2));
			writer.println(name + "3Priority = " + result.getSonarIssueCount(3));
			writer.println(name + "4Priority = " + result.getSonarIssueCount(4));
			writer.println(name + "5Priority = " + result.getSonarIssueCount(5));
			writer.println();

			writer.println(name + "Bug = " + result.getSonarIssueType(IssueType.BUG.getTypeIndex()));
			writer.println(name + "Vulnerability = " + result.getSonarIssueType(IssueType.VULNERABILITY.getTypeIndex()));
			writer.println(name + "CodeSmell = " + result.getSonarIssueType(IssueType.CODE_SMELL.getTypeIndex()));
			writer.println(name + "NA = " + result.getSonarIssueType(IssueType.NA.getTypeIndex()));
			writer.println();

		}
		if (result.getIndividualMode().isPmd()) {
			writer.println("PMDRules = " + result.getPmdRules());
			writer.println("PMDViolations = " + result.getPmdCountAll());
			writer.println("PMD1Priority = " + result.getPmdCount(1));
			writer.println("PMD2Priority = " + result.getPmdCount(2));
			writer.println("PMD3Priority = " + result.getPmdCount(3));
			writer.println("PMD4Priority = " + result.getPmdCount(4));
			writer.println("PMD5Priority = " + result.getPmdCount(5));
			writer.println();

			writer.println("PMDBug = " + result.getPmdType(IssueType.BUG.getTypeIndex()));
			writer.println("PMDVulnerability = " + result.getPmdType(IssueType.VULNERABILITY.getTypeIndex()));
			writer.println("PMDCodeSmell = " + result.getPmdType(IssueType.CODE_SMELL.getTypeIndex()));
			writer.println("PMDNA = " + result.getPmdType(IssueType.NA.getTypeIndex()));
			writer.println();
		}
		if (result.getIndividualMode().isFindBugs()) {
			writer.println("FindBugsRules = " + result.getFindBugsRules());
			writer.println("FindBugs = " + result.getFindBugsCountAll());
			writer.println("FindBugs1Priority = " + result.getFindBugsCount(1));
			writer.println("FindBugs2Priority = " + result.getFindBugsCount(2));
			writer.println("FindBugs3Priority = " + result.getFindBugsCount(3));
			writer.println("FindBugs4Priority = " + result.getFindBugsCount(4));
			writer.println("FindBugs5Priority = " + result.getFindBugsCount(5));
			writer.println();

			writer.println("FindBugsBug = " + result.getFindBugsType(IssueType.BUG.getTypeIndex()));
			writer.println("FindBugsVulnerability = " + result.getFindBugsType(IssueType.VULNERABILITY.getTypeIndex()));
			writer.println("FindBugsCodeSmell = " + result.getFindBugsType(IssueType.CODE_SMELL.getTypeIndex()));
			writer.println("FindBugsNA = " + result.getFindBugsType(IssueType.NA.getTypeIndex()));
			writer.println();
		}
		if (result.getIndividualMode().isFindSecBugs()) {
			writer.println("FindSecBugsRules = " + result.getFindSecBugsRules());
			writer.println("FindSecBugs = " + result.getFindSecBugsCountAll());
			writer.println();
		}
		if (result.getIndividualMode().isWebResources()) {
			writer.println("SonarJSRules = " + result.getSonarJSRules());
			writer.println("WebResource = " + result.getWebResourceCountAll());
			writer.println("WebResource1Priority = " + result.getWebResourceCount(1));
			writer.println("WebResource2Priority = " + result.getWebResourceCount(2));
			writer.println("WebResource3Priority = " + result.getWebResourceCount(3));
			writer.println("WebResource4Priority = " + result.getWebResourceCount(4));
			writer.println("WebResource5Priority = " + result.getWebResourceCount(5));
			writer.println();

			writer.println("WebResourceBug = " + result.getWebResourceType(IssueType.BUG.getTypeIndex()));
			writer.println("WebResourceVulnerability = " + result.getWebResourceType(IssueType.VULNERABILITY.getTypeIndex()));
			writer.println("WebResourceCodeSmell = " + result.getWebResourceType(IssueType.CODE_SMELL.getTypeIndex()));
			writer.println("WebResourceNA = " + result.getWebResourceType(IssueType.NA.getTypeIndex()));
			writer.println();
		}
		if (result.getIndividualMode().isDependency()) {
			writer.println("CyclicDependencies = " + result.getAcyclicDependencyCount());
			writer.println();
		}
		if (result.getIndividualMode().isUnusedCode()) {
			writer.println("UnusedCode = " + result.getUnusedCodeList().size());
			writer.println();
		}
		if (result.getIndividualMode().isCheckStyle()) {
            writer.println("CheckStyle = " + result.getCheckStyleList().size());
            writer.println();
        }

		writer.println("TechnicalDebt(Total) = " + result.getTechnicalDebt().getTotalDebt() + "MH");
		writer.println("TechnicalDebt(Duplication) = " + result.getTechnicalDebt().getDuplicationDebt() + "MH");
		writer.println("TechnicalDebt(Violation) = " + result.getTechnicalDebt().getViolationDebt() + "MH");
		writer.println("TechnicalDebt(Complexity) = " + result.getTechnicalDebt().getComplexityDebt() + "MH");
		writer.println("TechnicalDebt(AcyclicDependency) = " + result.getTechnicalDebt().getAcyclicDependencyDebt() + "MH");
		writer.println();
		writer.println();
	}

	@Override
	protected void writeFilePathList(List<String> filePathList) {
		writer.println("[FilePath]");

		int count = 0;
		synchronized (filePathList) {
			for (String file : filePathList) {
				writer.print(++count + " = ");
				writer.println(file);
			}
		}
		writer.println();
		writer.println("total = " + count);
		writer.println();
		writer.println();
	}

	@Override
	protected void writeDuplication(List<DuplicationResult> list) {
		if (result.isSeperatedOutput()) {
			csvOutput.writeDuplication(list);
		} else {
			writer.println("[Duplication]");
			writer.println("; path, start line, end line, duplicated path, duplicated start line, duplicated end line");

			int count = 0;
			synchronized (list) {
				for (DuplicationResult result : list) {
					writer.print(++count + " = ");
					writer.print(getStringsWithComma(result.getPath(), getString(result.getStartLine()), getString(result.getEndLine())));
					writer.print(",");
					writer.print(getStringsWithComma(result.getDuplicatedPath(), getString(result.getDuplicatedStartLine()), getString(result.getDuplicatedEndLine())));
					writer.println();
				}
			}
			writer.println();
			writer.println("total = " + count);
			writer.println();
			writer.println();
		}

		if (result.isDetailAnalysis()) {
			writeTopDuplication(result.getTopDuplicationList());
		}
	}

	private void writeTopDuplication(List<Duplication> topDuplicationList) {
		writer.println("[TopDuplication]");
		writer.println("; path, start line, end line, count, total duplicated lines");

		int count = 0;
		synchronized (topDuplicationList) {
			for (Duplication result : topDuplicationList) {
				writer.print(++count + " = ");
				writer.print(getStringsWithComma(result.getPath(), getString(result.getStartLine()), getString(result.getEndLine())));
				writer.print(",");
				writer.print(result.getCount());
				writer.print(",");
				writer.print(result.getTotalDuplicatedLines());
				writer.println();
			}
		}
		writer.println();
		writer.println("total = " + count);
		writer.println();
		writer.println();
	}

	@Override
	protected void writeComplexity(List<ComplexityResult> list) {
		if (result.isSeperatedOutput()) {
			csvOutput.writeComplexity(list);
			return;
		}

		writer.println("[Complexity]");
		writer.println("; path, line, method, complexity");
		writer.println("; only 20 over methods");

		int count = 0;
		synchronized (list) {
			Collections.sort(list, (r1, r2) -> (r2.getComplexity() - r1.getComplexity()));

			for (ComplexityResult result : list) {
				if (result.getComplexity() <= 20) {
					break;
				}
				writer.print(++count + " = ");
				writer.print(getStringsWithComma(result.getPath(), getString(result.getLine()), result.getMethodName(), getString(result.getComplexity())));
				writer.println();
			}
		}
		writer.println();
		writer.println("total = " + count);
		writer.println();
		writer.println();
	}

	@Override
	protected void writeSonarIssue(List<SonarIssueResult> sonarIssueList) {
		String name = result.getSonarIssueTitle();

		if (result.isSeperatedOutput()) {
			csvOutput.writeSonarIssue(sonarIssueList);
		} else {
			writer.println("[" + name +"]");
			writer.println("; lang, type, path, rule, message, priority, start line, start offset, end line, end offset");

			int count = 0;
			synchronized (sonarIssueList) {
				for (SonarIssueResult result : sonarIssueList) {
					writer.print(++count + " = ");
					writer.print(getStringsWithComma(result.getLanguage(), result.getIssueType().toString(),
							result.getPath(), result.getRuleRepository() + ":" + result.getRuleKey(), result.getMsg(),
							getString(result.getSeverity()), getString(result.getStartLine()),
							getString(result.getStartOffset()), getString(result.getEndLine()), getString(result.getEndOffset())));
					writer.println();
				}
			}

			writer.println();
			writer.println("total = " + count);
			writer.println();
			writer.println();
		}

		if (result.isDetailAnalysis()) {
			writeTopInspection(result.getTopSonarIssueList(), "Top" + name + "List");
		}
	}

	@Override
	protected void writePmd(List<PmdResult> list) {
		if (result.isSeperatedOutput()) {
			csvOutput.writePmd(list);
		} else {
			writer.println("[PMD]");
			writer.println("; type, path, line, rule, priority, description");

			int count = 0;
			synchronized (list) {
				for (PmdResult result : list) {
					writer.print(++count + " = ");
					writer.print(getStringsWithComma(result.getIssueType().toString(), result.getPath(), getString(result.getLine()),
							result.getRule(), getString(result.getPriority()), result.getDescription()));
					writer.println();
				}
			}

			writer.println();
			writer.println("total = " + count);
			writer.println();
			writer.println();
		}

		if (result.isDetailAnalysis()) {
			writeTopInspection(result.getTopPmdList(), "TopPmdList");
		}
	}

	@Override
	protected void writeUnusedCode(List<UnusedCodeResult> list) {
		if (result.isSeperatedOutput()) {
			csvOutput.writeUnusedCode(list);
		} else {
			writer.println("[Unused Code]");
			writer.println("; type, package, class, line, name, description");

			int count = 0;
			synchronized (list) {
				for (UnusedCodeResult result : list) {
					writer.print(++count + " = ");
					writer.print(getStringsWithComma(result.getType(), result.getPackageName(), result.getClassName(), getString(result.getLine()), result.getName(), result.getDescription()));
					writer.println();
				}
			}

			writer.println();
			writer.println("total = " + count);
			writer.println();
			writer.println();
		}
	}

	private void writeTopInspection(List<Inspection> topList, String topName) {
		writer.println("[" + topName + "]");
		writer.println("; rule, type, count");

		int count = 0;
		synchronized (topList) {
			for (Inspection result : topList) {
				writer.print(++count + " = ");
				writer.print(getStringsWithComma(result.getRule()));
				writer.print(",");
				writer.print(result.getType());
				writer.print(",");
				writer.print(result.getCount());
				writer.println();
			}
		}
		writer.println();
		writer.println("total = " + count);
		writer.println();
		writer.println();

	}

	@Override
	protected void writeFindBugs(List<FindBugsResult> list) {
		writeFindBugsAndFindSecBugs(list, "FindBugs");

		if (result.isDetailAnalysis()) {
			writeTopInspection(result.getTopFindBugsList(), "TopFindBugsList");
		}
	}

	@Override
	protected void writeFindSecBugs(List<FindBugsResult> list) {
		writeFindBugsAndFindSecBugs(list, "FindSecBugs");
	}

	private void writeFindBugsAndFindSecBugs(List<FindBugsResult> list, String title) {
		if (result.isSeperatedOutput()) {
			csvOutput.writeFindBugsAndFindSecBugs(list, title);
			return;
		}
		writer.println("[" + title + "]");
		writer.println("; type, package, file, start line, end line, pattern key, pattern, priority, class, field, local var, method, message");

		int count = 0;
		synchronized (list) {
			for (FindBugsResult result : list) {
				writer.print(++count + " = ");
				writer.print(getStringsWithComma(result.getIssueType().toString(), result.getPackageName(), result.getFile(),
						getString(result.getStartLine()), getString(result.getEndLine()), result.getPatternKey(), result.getPattern()));
				writer.print(", ");
				writer.print(getStringsWithComma(getString(result.getPriority()), result.getClassName(), result.getField(), result.getLocalVariable(), result.getMethod(), result.getMessage()));
				writer.println();
			}
		}

		writer.println();
		writer.println("total = " + count);
		writer.println();
		writer.println();
	}

	@Override
	protected void writeWebResource(List<WebResourceResult> webResourceList) {
		if (result.isSeperatedOutput()) {
			csvOutput.writeWebResource(webResourceList);
		} else {
			writer.println("[WebResource]");
			writer.println("; lang, type, path, rule, message, priority, start line, start offset, end line, end offset");

			int count = 0;
			synchronized (webResourceList) {
				for (WebResourceResult result : webResourceList) {
					writer.print(++count + " = ");
					writer.print(getStringsWithComma(result.getLanguage(), result.getIssueType().toString()));
					writer.print(",");
					writer.print(getStringsWithComma(result.getPath(), result.getRuleRepository() + ":" + result.getRuleKey(), result.getMsg(),
							getString(result.getSeverity()), getString(result.getStartLine()),
							getString(result.getStartOffset()), getString(result.getEndLine()), getString(result.getEndOffset())));
					writer.println();
				}
			}

			writer.println();
			writer.println("total = " + count);
			writer.println();
			writer.println();
		}
	}

	@Override
	protected void writeAcyclicDependencies(List<String> list) {
		writer.println("[AcyclicDependencies]");

		int count = 0;
		synchronized (list) {
			for (String dependency : list) {
				writer.print(++count + " = ");
				writer.print(dependency);
				writer.println();
			}
		}

		writer.println();
		writer.println("total = " + count);
		writer.println();
		writer.println();

		if (result.isDetailAnalysis()) {
			writeTopMartinMetrics(result.getTopMartinMetrics());
		}
	}

	@Override
	protected void writeCkMetrics(List<CkMetricsResult> list) {
		if (result.isSeperatedOutput()) {
			csvOutput.writeCkMetrics(list);
			return;
		}
		writer.println("[CKMetrics]");
		writer.println("; className, WMC, NOC, RFC, CBO, DIT, LCOM");

		int count = 0;
		synchronized (list) {
			for (CkMetricsResult result : list) {
				writer.print(++count + " = ");
				writer.print(result.getQualifiedClassName());
				writer.print(",");
				writer.print(result.getWmc());
				writer.print(",");
				writer.print(result.getNoc());
				writer.print(",");
				writer.print(result.getRfc());
				writer.print(",");
				writer.print(result.getCbo());
				writer.print(",");
				writer.print(result.getDit());
				writer.print(",");
				writer.print(result.getLcom());
				writer.println();
			}
		}

		writer.println();
		writer.println("total = " + count);
		writer.println();
		writer.println();
	}

	private void writeTopMartinMetrics(List<MartinMetrics> topMartinMetrics) {
		writer.println("[TopMartinMetrics]");
		writer.println("; package, Ca, Ce, A, I, D");

		int count = 0;
		synchronized (topMartinMetrics) {
			for (MartinMetrics result : topMartinMetrics) {
				writer.print(++count + " = ");
				writer.print(result.getPackageName());
				writer.print(",");
				writer.print(result.getAfferentCoupling());
				writer.print(",");
				writer.print(result.getEfferentCoupling());
				writer.print(",");
				writer.print(result.getAbstractness());
				writer.print(",");
				writer.print(result.getInstability());
				writer.print(",");
				writer.print(result.getDistance());
				writer.println();
			}
		}
		writer.println();
		writer.println("total = " + count);
		writer.println();
		writer.println();
	}

    @Override
    protected void writeCheckStyle(List<CheckStyleResult> list) {
        if (result.isSeperatedOutput()) {
            csvOutput.writeCheckStyle(list);
            return;
        }
        writer.println("[CheckStyle]");
        writer.println("; path, line, severity, message, checker");

        int count = 0;
        synchronized (list) {
            for (CheckStyleResult result : list) {
                writer.print(++count + " = ");
                writer.print(result.getPath());
                writer.print(",");
                writer.print(result.getLine());
                writer.print(",");
                writer.print(result.getSeverity());
                writer.print(",");
                writer.print(result.getMessage());
                writer.print(",");
                writer.print(result.getChecker());
                writer.println();
            }
        }
        writer.println();
        writer.println("total = " + count);
        writer.println();
        writer.println();
    }

    @Override
	protected void close(PrintWriter writer) {
		// no-op
	}
}
