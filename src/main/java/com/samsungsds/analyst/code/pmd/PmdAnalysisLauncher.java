package com.samsungsds.analyst.code.pmd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.main.MeasuredResult;

import net.sourceforge.pmd.PMD;

public class PmdAnalysisLauncher extends AbstractPmdAnalysis implements PmdAnalysis {
	private static final Logger LOGGER = LogManager.getLogger(PmdAnalysisLauncher.class);
	
	private static final String CODE_QUALITY_RULESET_FILE = "/statics/PMD_ruleset_SDS_Standard_20160826.xml";
	
	private List<String> arg = new ArrayList<>();
	
	@Override
	public void addOption(String option, String value) {
		arg.add(option);
		
		if (value != null && !value.equals("")) {
			arg.add(value);
		}
	}
	
	@Override
	public void run() {
		
		if (!arg.contains("-rulesets")) {
			addOption("-rulesets", saveRuleSetFile(CODE_QUALITY_RULESET_FILE).toString());
		}
		addOption("-format", "csv");
		
		File reportFile = createPmdReportFile();
		
		addOption("-reportfile", reportFile.toString());
		
		LOGGER.debug("Pmd Result File : {}", reportFile.toString());
		
		PMD.run(arg.toArray(new String[0]));
		
		List<PmdResult> resultList = parseCSV(reportFile);
		
		MeasuredResult.getInstance().putPmdList(resultList);
	}
}
