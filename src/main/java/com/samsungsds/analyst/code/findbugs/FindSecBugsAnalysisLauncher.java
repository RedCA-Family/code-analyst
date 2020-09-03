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
package com.samsungsds.analyst.code.findbugs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

import edu.umd.cs.findbugs.FindBugs2;

public class FindSecBugsAnalysisLauncher extends FindBugsAnalysisLauncher {
	private static final Logger LOGGER = LogManager.getLogger(FindSecBugsAnalysisLauncher.class);

	private static final String PLUGIN_FILE = "/statics/findsecbugs-plugin-1.10.1.jar";
	private static final String BUG_SEC_RULESET_FILE = "/statics/FindSecBugs-include-filter_81.xml";

	private static boolean firstRun = true;

	private File reportFile = null;

	@Override
	public void run(String instanceKey) {
		if (firstRun) {
			addOption("-pluginList", IOAndFileUtils.saveResourceFile(PLUGIN_FILE, "plugin", ".jar").toString());
		}
		addOption("-include", IOAndFileUtils.saveResourceFile(BUG_SEC_RULESET_FILE, "include", ".xml").toString());

		addOption("-xml", "");

		try {
			reportFile = File.createTempFile("findsecbugs", ".xml");
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		reportFile.deleteOnExit();

		addOption("-output", reportFile.toString());

		LOGGER.debug("FindSecBugs Result File : {}", reportFile.toString());

		addOption("-onlyAnalyze", getTargetPackages(instanceKey));
		addOption("-nested:false", "");

		addOption(getTargetDirectory(), "");

		try {
			FindBugs2.main(getArg().toArray(new String[0]));
		} catch (IOException ioe) {
			if (ioe.getMessage().equals("No files to analyze could be opened")) {
				LOGGER.warn("There are no class files to be analyzed via FindBugs");
				return;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			firstRun = false;
		}

		List<FindBugsResult> resultList = parseXML(reportFile);

		MeasuredResult.getInstance(instanceKey).putFindSecBugsList(resultList);
	}
}
