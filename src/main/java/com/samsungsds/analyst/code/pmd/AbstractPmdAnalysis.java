package com.samsungsds.analyst.code.pmd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.samsungsds.analyst.code.util.IOAndFileUtils;

public abstract class AbstractPmdAnalysis  {
	
	protected File createPmdReportFile() {
		File reportFile = null;
		
		try {
			reportFile = File.createTempFile("pmd", ".csv");
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		reportFile.deleteOnExit();
		
		return reportFile;
	}
	
	protected File saveRuleSetFile(String ruleResourceName) {
		return IOAndFileUtils.saveResourceFile(ruleResourceName, "ruleset", ".xml");
	}
	
	protected List<PmdResult> parseCSV(File reportFile) {
		List<PmdResult> list = new ArrayList<>();
		
		try {
			Reader in = new FileReader(reportFile);
			
			Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
			for (CSVRecord record : records) {
			    String problem = record.get("Problem");
			    String packageName = record.get("Package");
			    String file = record.get("File");
			    String priority = record.get("Priority");
			    String line = record.get("Line");
			    String description = record.get("Description");
			    String ruleSet = record.get("Rule set");
			    String rule = record.get("Rule");
			    
			    PmdResult result = new PmdResult(problem,packageName, file, priority, line, description, ruleSet, rule);
			    
			    list.add(result);			    
			}
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	
		return list;
	}
}
