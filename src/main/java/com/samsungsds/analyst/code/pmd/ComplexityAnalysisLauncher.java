package com.samsungsds.analyst.code.pmd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.main.MeasuredResult;

import net.sourceforge.pmd.PMD;

public class ComplexityAnalysisLauncher extends AbstractPmdAnalysis implements ComplexityAnalysis  {
	private static final Logger LOGGER = LogManager.getLogger(ComplexityAnalysisLauncher.class);
	
	private static final String COMPLEXITY_RULESET_FILE = "/statics/PMD_ruleset_complexity.xml";
	
	private List<String> arg = new ArrayList<>();
	private File reportFile = null; 
	
	@Override
	public void addOption(String option, String value) {
		arg.add(option);
		
		if (value != null && !value.equals("")) {
			arg.add(value);
		}
	}
	
	@Override
	public void run() {
		
		addOption("-rulesets", saveRuleSetFile(COMPLEXITY_RULESET_FILE).toString());
		addOption("-format", "csv");
		addOption("-showsuppressed", "");
		
		try {
			reportFile = File.createTempFile("pmd", ".csv");
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		reportFile.deleteOnExit();
		
		addOption("-reportfile", reportFile.toString());
		
		LOGGER.debug("Complexity Result File : {}", reportFile.toString());

		PMD.run(arg.toArray(new String[0]));
		
		List<PmdResult> resultList = parseCSV(reportFile);
		
		List<ComplexityResult> complexitList = ComplexityResult.convertComplexitResult(resultList);
		
		MeasuredResult.getInstance().putComplexityList(complexitList);
	}
}
