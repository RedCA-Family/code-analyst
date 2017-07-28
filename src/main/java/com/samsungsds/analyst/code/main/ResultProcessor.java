package com.samsungsds.analyst.code.main;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.main.result.AbstractOutputFile;
import com.samsungsds.analyst.code.main.result.JsonOutputFile;
import com.samsungsds.analyst.code.main.result.OutputFileFormat;
import com.samsungsds.analyst.code.main.result.TextOutputFile;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

public class ResultProcessor {
	private static final Logger LOGGER = LogManager.getLogger(ResultProcessor.class);
	
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
		System.out.println("Comment Lines : " + getFormattedNumber(result.getCommentLines()));
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
		System.out.println("- The complexity is calculated by PMD's Modified Cyclomatic Complexity method");
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
	
	protected static void printComplexity(List<ComplexityResult> list) {
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
	
	private static AbstractOutputFile createOutputFile(OutputFileFormat format) {
		if (format == OutputFileFormat.TEXT) {
			LOGGER.info("Text Output");
			return new TextOutputFile();
		} else if (format == OutputFileFormat.JSON) {
			LOGGER.info("JSON Output");
			return new JsonOutputFile();
		} else {
			throw new IllegalStateException("OutputFileFormat isn't 'json', 'text', nor 'none'");
		}
	}
	
	public static void saveResultOutputFile(File file, CliParser cli, MeasuredResult result) {
		if (cli.getFormat() != OutputFileFormat.NONE) {
			result.setOutputFile(file);
			
			AbstractOutputFile output = createOutputFile(cli.getFormat());
			
			LOGGER.info("Result file saved : {}", file);
			
			output.process(file, cli, result);
		} else {
			LOGGER.info("No output file");
		}
	}
}
