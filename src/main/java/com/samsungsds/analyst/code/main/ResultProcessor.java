package com.samsungsds.analyst.code.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.sonar.DuplicationResult;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

public class ResultProcessor {
	protected static NumberFormat noFormatter = NumberFormat.getInstance();
	
	protected static String getFormattedNumber(int number) {
		return noFormatter.format(number);
	}
	
	protected static void printSeparator() {
		System.out.println("================================================================================");
	}
	
	protected static void printTitle() {
		printSeparator();
		//System.out.println("");
		//System.out.println("              ██████╗ ███████╗███████╗██╗   ██╗██╗  ████████╗");
		//System.out.println("              ██╔══██╗██╔════╝██╔════╝██║   ██║██║  ╚══██╔══╝");
		//System.out.println("              ██████╔╝█████╗  ███████╗██║   ██║██║     ██║");   
		//System.out.println("              ██╔══██╗██╔══╝  ╚════██║██║   ██║██║     ██║");  
		//System.out.println("              ██║  ██║███████╗███████║╚██████╔╝███████╗██║");  
		//System.out.println("              ╚═╝  ╚═╝╚══════╝╚══════╝ ╚═════╝ ╚══════╝╚═╝");
		System.out.println("              ______    _______  _______  __   __  ___      _______"); 
		System.out.println("             |    _ |  |       ||       ||  | |  ||   |    |       |");
		System.out.println("             |   | ||  |    ___||  _____||  | |  ||   |    |_     _|");
		System.out.println("             |   |_||_ |   |___ | |_____ |  |_|  ||   |      |   |  ");
		System.out.println("             |    __  ||    ___||_____  ||       ||   |___   |   |  ");
		System.out.println("             |   |  | ||   |___  _____| ||       ||       |  |   |  ");
		System.out.println("             |___|  |_||_______||_______||_______||_______|  |___|");  
		printSeparator();
	}
	
	protected static void printCommon(MeasuredResult result) {
		System.out.println("Files : " + getFormattedNumber(result.getFiles()));
		System.out.println("Dir. : " + getFormattedNumber(result.getDirectories()));
		System.out.println("Classes : " + getFormattedNumber(result.getClasses()));
		System.out.println("Functions : " + getFormattedNumber(result.getFunctions()));
		System.out.println("lines : " + getFormattedNumber(result.getLines()));
		System.out.println("Comment Lines : " + getFormattedNumber(result.getFunctions()));
		System.out.println("Ncloc : " + getFormattedNumber(result.getNcloc()));
		System.out.println("Statements : " + getFormattedNumber(result.getStatements()));
		System.out.println();
		System.out.println("Duplicated lines : " + getFormattedNumber(result.getDuplicatedLines()));
		System.out.println("Duplication % : " + result.getDuplicatedLinesPercent() );
		System.out.println();
	}
	
	protected static void printComplexitySummary(MeasuredResult result) {
		System.out.println("Complexity functions : " + getFormattedNumber(result.getComplexityFunctions()));
		System.out.println("Complexity Total : " + getFormattedNumber(result.getComplexitySum()));
		System.out.println("Complexity Over 10(%) : " + result.getComplexityOver10Percent() + " (" + getFormattedNumber(result.getComplexityOver10()) + ")");
		System.out.println("Complexity Over 15(%) : " + result.getComplexityOver15Percent() + " (" + getFormattedNumber(result.getComplexityOver15()) + ")");
		System.out.println("Complexity Over 20(%) : " + result.getComplexityOver20Percent() + " (" + getFormattedNumber(result.getComplexityOver20()) + ")");
		System.out.println("Complexity Equal Or Over 50(%) : " + result.getComplexityEqualOrOver50Percent() + " (" + getFormattedNumber(result.getComplexityEqualOrOver50()) + ")");
		System.out.println();
	}
	
	protected static void printPmdSummary(MeasuredResult result) {
		System.out.println("PMD violations : " + getFormattedNumber(result.getPmdCountAll()));
		System.out.println("PMD 1 priority : " + getFormattedNumber(result.getPmdCount(1)));
		System.out.println("PMD 2 priority : " + getFormattedNumber(result.getPmdCount(2)));
		System.out.println("PMD < 3 priority : " + getFormattedNumber(result.getPmdCount(3) + result.getPmdCount(4) + result.getPmdCount(5)));
		System.out.println();
	}
	
	protected static void printFindBugsSummary(MeasuredResult result) {
		System.out.println("FindBugs bugs : " + getFormattedNumber(result.getFindBugsCountAll()));
		System.out.println("FindBugs 1 priority : " + getFormattedNumber(result.getFindBugsCount(1)));
		System.out.println("FindBugs 2 priority : " + getFormattedNumber(result.getFindBugsCount(2)));
		System.out.println("FindBugs < 3 priority : " + getFormattedNumber(result.getFindBugsCount(3) + result.getFindBugsCount(4) + result.getFindBugsCount(5)));
		System.out.println();
	}
	
	protected static void pirntAcyclicDependSummary(MeasuredResult result) {
		System.out.println("Acyclic Dependencies : " + getFormattedNumber(result.getAcyclicDependencyCount()));
		System.out.println();
	}
	
	protected static void printBottom() {
		printSeparator();
	}
	
	public static void printSummary(MeasuredResult result) {
		printTitle();
		printCommon(result);
		
		if (result.getMode() == MeasurementMode.DefaultMode) {
			printComplexitySummary(result);
			printPmdSummary(result);
			printFindBugsSummary(result);
			pirntAcyclicDependSummary(result);
		} else if (result.getMode() == MeasurementMode.ComplexityMode) {
			printComplexity(result.getComplexityList());	
		}
		
		printBottom();
	}

	public static void saveResultOutputFile(File file, CliParser cli, MeasuredResult result) {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))))) {
			writer.println(";===============================================================================");
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
			writer.println();
			writer.println();
			writer.println("[Summary]");
			writer.println("Files = " + result.getFiles());
			writer.println("Directories = " + result.getDirectories());
			writer.println("Classes = " + result.getClasses());
			writer.println("Functions = " + result.getFunctions());
			writer.println("lines = " + result.getLines());
			writer.println("CommentLines = " + result.getFunctions());
			writer.println("Ncloc = " + result.getNcloc());
			writer.println("Statements = " + result.getStatements());
			writer.println();
			writer.println("DuplicatedLines = " + result.getDuplicatedLines());
			writer.println();
			writer.println("ComplexityFunctions = " + result.getComplexityFunctions());
			writer.println("ComplexityTotal = " + result.getComplexitySum());
			writer.println("ComplexityOver10 = " + result.getComplexityOver10());
			writer.println("ComplexityOver15 = " + result.getComplexityOver15());
			writer.println("ComplexityOver20 = " + result.getComplexityOver20());
			writer.println("ComplexityEqualOrOver50 = " + result.getComplexityEqualOrOver50());
			writer.println();
			writer.println("PMDViolations = " + result.getPmdCountAll());
			writer.println("PMD1Priority = " + result.getPmdCount(1));
			writer.println("PMD2Priority = " + result.getPmdCount(2));
			writer.println("PMD3Priority = " + result.getPmdCount(3));
			writer.println("PMD4Priority = " + result.getPmdCount(4));
			writer.println("PMD5Priority = " + result.getPmdCount(5));
			writer.println();
			writer.println("FindBugsBugs = " + result.getFindBugsCountAll());
			writer.println("FindBugs1Priority = " + result.getFindBugsCount(1));
			writer.println("FindBugs2Priority = " + result.getFindBugsCount(2));
			writer.println("FindBugs3Priority = " + result.getFindBugsCount(3));
			writer.println("FindBugs4Priority = " + result.getFindBugsCount(4));
			writer.println("FindBugs5Priority = " + result.getFindBugsCount(5));
			writer.println();
			writer.println();
			
			printDuplication(writer, result.getDulicationList());
			
			printComplexity(writer, result.getComplexityList());
			
			printPmd(writer, result.getPmdList());
			
			printFindBugs(writer, result.getFindBugsList());
			
			printAcyclicDependencies(writer, result.getAcyclicDependencyList());
			
			writer.println(";===============================================================================");
			
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void printDuplication(PrintWriter writer, List<DuplicationResult> list) {
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
	
	public static void printComplexity(PrintWriter writer, List<ComplexityResult> list) {
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
	
	public static void printComplexity(List<ComplexityResult> list) {
		StringBuffer buffer = new StringBuffer();
		
		synchronized (list) {
			//Collections.sort(list, (r1, r2) -> (r2.getComplexity() - r1.getComplexity()));
			
			for (ComplexityResult result : list) {
				buffer.append(" - ").append(result.getPath()).append(" (").append(result.getMethodName()).append(") = ");
				buffer.append(result.getComplexity()).append(IOAndFileUtils.CR_LF);
			}
		}
		System.out.println(buffer.toString());
	}
	
	public static void printPmd(PrintWriter writer, List<PmdResult> list) {
		writer.println("[PMD]");
		writer.println("; path, line, rule, priority,  description");
		
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
	
	public static void printFindBugs(PrintWriter writer, List<FindBugsResult> list) {
		writer.println("[FindBugs]");
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
	
	private static void printAcyclicDependencies(PrintWriter writer, List<String> list) {
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
	
	public static String getStringsWithComma(String ... strings) {
		StringBuilder builder = new StringBuilder();
		
		for (String str : strings) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(str);
		}
		
		return builder.toString();
	}
	
	public static String getString(int number) {
		return Integer.toString(number);
	}
}
