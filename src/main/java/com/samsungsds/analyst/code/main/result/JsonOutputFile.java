package com.samsungsds.analyst.code.main.result;

import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.sonar.DuplicationResult;

public class JsonOutputFile extends AbstractOutputFile {
	private MeasuredResult result;
	
	@Override
	protected void open(MeasuredResult result) {
		this.result = result;
	}
	
	@Override
	protected void writeSeparator() {
		// no-op
	}

	@Override
	protected void writeAcyclicDependencies(List<String> acyclicDependencyList) {
		// no-op
	}

	@Override
	protected void writeFindBugs(List<FindBugsResult> findBugsList) {
		// no-op	
	}
	
	@Override
	protected void writeFindSecBugs(List<FindBugsResult> findSecBugsList) {
		// no-op	
	}

	@Override
	protected void writePmd(List<PmdResult> pmdList) {
		// no-op
	}

	@Override
	protected void writeComplexity(List<ComplexityResult> complexityList) {
		// no-op
	}

	@Override
	protected void writeDuplication(List<DuplicationResult> dulicationList) {
		// no-op
	}

	@Override
	protected void writeSummary(MeasuredResult result) {
		// no-op
	}

	@Override
	protected void writeProjectInfo(CliParser cli, MeasuredResult result) {
		// no-op
	}

	@Override
	protected void close(PrintWriter writer) {
		final GsonBuilder builder = new GsonBuilder();
	    builder.excludeFieldsWithoutExposeAnnotation();
	    final Gson gson = builder.create();
	    
	    String json = gson.toJson(result);
	    
	    writer.print(json);
	}
}
