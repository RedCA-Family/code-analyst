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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.samsungsds.analyst.code.util.JavaLogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.main.MeasuredResult;

import net.sourceforge.pmd.PMD;

public class PmdAnalysisLauncher extends AbstractPmdAnalysis implements PmdAnalysis {
	private static final Logger LOGGER = LogManager.getLogger(PmdAnalysisLauncher.class);

	private static final String CODE_QUALITY_RULESET_FILE = "/statics/PMD_ruleset_SDS_Standard_20180302.xml";

	private List<String> arg = new ArrayList<>();

	@Override
	public void addOption(String option, String value) {
		arg.add(option);

		if (value != null && !value.equals("")) {
			arg.add(value);
		}
	}

	@Override
	public void run(String instanceKey) {

		if (!arg.contains("-rulesets")) {
			addOption("-rulesets", saveRuleSetFile(CODE_QUALITY_RULESET_FILE).toString());
		}
		addOption("-format", "csv");

		File reportFile = createPmdReportFile();

		addOption("-reportfile", reportFile.toString());

		LOGGER.debug("Pmd Result File : {}", reportFile.toString());

		if (!MeasuredResult.getInstance(instanceKey).isDebug()) {
            JavaLogUtils.setPmdLogLevelFilter(Level.SEVERE);
        }

		PMD.run(arg.toArray(new String[0]));

		List<PmdResult> resultList = parseCSV(reportFile, instanceKey);

		MeasuredResult.getInstance(instanceKey).putPmdList(resultList);
	}
}
