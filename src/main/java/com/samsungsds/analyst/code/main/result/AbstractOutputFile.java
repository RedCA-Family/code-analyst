package com.samsungsds.analyst.code.main.result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.sonar.DuplicationResult;
import com.samsungsds.analyst.code.util.CSVUtil;

public abstract class AbstractOutputFile {
	protected PrintWriter writer;
	
	public String getString(int number) {
		return Integer.toString(number);
	}
	
	public String getStringsWithComma(String... strings) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < strings.length; i++) {
			if (i != 0) {
				builder.append(", ");
			}
			builder.append(CSVUtil.getCSVStyleString(strings[i]));
		}
		
		return builder.toString();
	}

	public void process(File file, CliParser cli, MeasuredResult result) {
		try {
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))));
			
			open(result);
			
			writeSeparator();
			
			writeProjectInfo(cli, result);
			
			writeSummary(result);
			
			if (result.getIndividualMode().isDuplication()) {
				writeDuplication(result.getDulicationList());
			}
			
			if (result.getIndividualMode().isComplexity()) {
				writeComplexity(result.getComplexityList());
			}
			
			if (result.getIndividualMode().isPmd()) {
				writePmd(result.getPmdList());
			}
			
			if (result.getIndividualMode().isFindBugs()) {
				writeFindBugs(result.getFindBugsList());
			}
			
			if (result.getIndividualMode().isFindSecBugs()) {
				writeFindSecBugs(result.getFindSecBugsList());
			}
			
			if (result.getIndividualMode().isDependency()) {
				writeAcyclicDependencies(result.getAcyclicDependencyList());
			}
			
			writeSeparator();
			
			close(writer);
			
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
	protected abstract void open(MeasuredResult result);
	
	protected abstract void writeSeparator();

	protected abstract void writeAcyclicDependencies(List<String> acyclicDependencyList);

	protected abstract void writeFindBugs(List<FindBugsResult> findBugsList);
	
	protected abstract void writeFindSecBugs(List<FindBugsResult> findSecBugsList);

	protected abstract void writePmd(List<PmdResult> pmdList);

	protected abstract void writeComplexity(List<ComplexityResult> complexityList);

	protected abstract void writeDuplication(List<DuplicationResult> dulicationList);

	protected abstract void writeSummary(MeasuredResult result);

	protected abstract void writeProjectInfo(CliParser cli, MeasuredResult result);
	
	protected abstract void close(PrintWriter writer);

}
