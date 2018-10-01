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
	
	protected List<PmdResult> parseCSV(File reportFile, String instanceKey) {
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
			    
			    PmdResult result = new PmdResult(problem,packageName, file, priority, line, description, ruleSet, rule, instanceKey);
			    
			    list.add(result);			    
			}
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	
		return list;
	}
}
