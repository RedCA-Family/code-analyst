package com.samsungsds.analyst.code.main.result;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.sonar.DuplicationResult;

public class TextOutputFile extends AbstractOutputFile {

	@Override
	protected void open(MeasuredResult result) {
		// no-op
	}
	
	@Override
	protected void writeSeparator() {
		writer.println(";===============================================================================");
	}
	
	@Override
	protected void writeProjectInfo(CliParser cli, MeasuredResult result) {
		writer.println("[Project]");
		writer.println("Target = " + result.getProjectDirectory());
		writer.println("Source = " + cli.getSrc());
		writer.println("Binary = " + cli.getBinary());
		writer.println("Encoding = " + cli.getEncoding());
		writer.println("JavaVersion = " + cli.getJavaVersion());
		writer.println("Datetime = " + new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
		if (cli.getRuleSetFileForPMD() != null && !cli.getRuleSetFileForPMD().equals("")) {
			writer.println("PMD = " + cli.getRuleSetFileForPMD());
		}
		if (cli.getRuleSetFileForFindBugs() != null && !cli.getRuleSetFileForFindBugs().equals("")) {
			writer.println("FindBugs = " + cli.getRuleSetFileForFindBugs());
		}
		if (!"".equals(cli.getIncludes())) {
			writer.println("includes = " + cli.getIncludes());
		}
		if (!"".equals(cli.getExcludes())) {
			writer.println("excludes = " + cli.getExcludes());
		}
		writer.println("mode = " + result.getIndividualModeString());
		writer.println("version = " + result.getVersion());
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
			writer.println("lines = " + result.getLines());
			writer.println("CommentLines = " + result.getCommentLines());
			writer.println("Ncloc = " + result.getNcloc());
			writer.println("Statements = " + result.getStatements());
			writer.println();
		}
		if (result.getIndividualMode().isDuplication()) {
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
		if (result.getIndividualMode().isPmd()) {
			writer.println("PMDViolations = " + result.getPmdCountAll());
			writer.println("PMD1Priority = " + result.getPmdCount(1));
			writer.println("PMD2Priority = " + result.getPmdCount(2));
			writer.println("PMD3Priority = " + result.getPmdCount(3));
			writer.println("PMD4Priority = " + result.getPmdCount(4));
			writer.println("PMD5Priority = " + result.getPmdCount(5));
			writer.println();
		}
		if (result.getIndividualMode().isFindBugs()) {
			writer.println("FindBugsBugs = " + result.getFindBugsCountAll());
			writer.println("FindBugs1Priority = " + result.getFindBugsCount(1));
			writer.println("FindBugs2Priority = " + result.getFindBugsCount(2));
			writer.println("FindBugs3Priority = " + result.getFindBugsCount(3));
			writer.println("FindBugs4Priority = " + result.getFindBugsCount(4));
			writer.println("FindBugs5Priority = " + result.getFindBugsCount(5));
			writer.println();
		}
		if (result.getIndividualMode().isFindSecBugs()) {
			writer.println("FindSecBugs = " + result.getFindSecBugsCountAll());
			writer.println();
		}
		if (result.getIndividualMode().isDependency()) {
			writer.println("AcyclicDependecies = " + result.getAcyclicDependencyCount());
			writer.println();
		}
		writer.println();
	}

	@Override
	protected void writeDuplication(List<DuplicationResult> list) {
		writer.println("[Duplication]");
		writer.println("; path, start line, end line, duplicated path, duplicated start line, duplicated end line");
		
		int count = 0;
		synchronized (list) {
			for (DuplicationResult result : list) {
				writer.print(++count + " = ");
				writer.print(getStringsWithComma(result.getPath(), getString(result.getStartLine()), getString(result.getEndLine())));
				writer.print(", ");
				writer.print(getStringsWithComma(result.getDuplicatedPath(), getString(result.getDuplicatedStartLine()), getString(result.getDuplicatedEndLine())));
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
		writer.println("[Complexity]");
		writer.println("; path, line, method, complexity");
		writer.println("; only 10 over methods"); 

		int count = 0;
		synchronized (list) {
			Collections.sort(list, (r1, r2) -> (r2.getComplexity() - r1.getComplexity()));

			for (ComplexityResult result : list) {
				if (result.getComplexity() <= 10) {
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
	protected void writePmd(List<PmdResult> list) {
		writer.println("[PMD]");
		writer.println("; path, line, rule, priority, description");
		
		int count = 0;
		synchronized (list) {
			for (PmdResult result : list) {
				writer.print(++count + " = ");
				writer.print(getStringsWithComma(result.getPath(), getString(result.getLine()), result.getRule(), getString(result.getPriority()), result.getDescription()));
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
	}
	
	@Override
	protected void writeFindSecBugs(List<FindBugsResult> list) {
		writeFindBugsAndFindSecBugs(list, "FindSecBugs");
	}
	
	private void writeFindBugsAndFindSecBugs(List<FindBugsResult> list, String title) {
		writer.println("[" + title + "]");
		writer.println("; package, file, start line, end line, pattern key, pattern, priority, class, field, local var, method, message");
		
		int count = 0;
		synchronized (list) {
			for (FindBugsResult result : list) {
				writer.print(++count + " = ");
				writer.print(getStringsWithComma(result.getPackageName(), result.getFile(), getString(result.getStartLine()), getString(result.getEndLine()), result.getPatternKey(), result.getPattern()));
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
	}
	
	@Override
	protected void close(PrintWriter writer) {
		// no-op
	}
}
