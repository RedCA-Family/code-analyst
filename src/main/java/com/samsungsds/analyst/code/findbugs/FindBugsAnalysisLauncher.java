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
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;

import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.SortedBugCollection;

public class FindBugsAnalysisLauncher implements FindBugsAnalysis {
	private static final Logger LOGGER = LogManager.getLogger(FindBugsAnalysisLauncher.class);

	private static final String BUG_RULESET_FILE = "/statics/FindBugs-include-filter_215.xml";

	private List<String> arg = new ArrayList<>();
	private File reportFile = null;

	private String targetDirectory = null;

	@Override
	public void setTarget(String directory) {
		LOGGER.debug("FindBugs Target Directory : {}", directory);
		this.targetDirectory = directory;
	}

	@Override
	public void addOption(String option, String value) {
		arg.add(option);

		if (value != null && !value.equals("")) {
			arg.add(value);
		}
	}

	@Override
	public void run(String instanceKey) {

		if (!arg.contains("-include")) {
			addOption("-include", IOAndFileUtils.saveResourceFile(BUG_RULESET_FILE, "include", ".xml").toString());
		}
		addOption("-xml", "");

		try {
			reportFile = File.createTempFile("findbugs", ".xml");
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		reportFile.deleteOnExit();

		addOption("-output", reportFile.toString());

		LOGGER.debug("FindBugs Result File : {}", reportFile.toString());

		addOption("-onlyAnalyze", getTargetPackages(instanceKey));
		addOption("-nested:false", "");

		addOption(targetDirectory, "");

		try {
			FindBugs2.main(arg.toArray(new String[0]));
		} catch (IOException ioe) {
			if (ioe.getMessage().equals("No files to analyze could be opened")) {
				LOGGER.warn("There are no class files to be analyzed via FindBugs");
				return;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		List<FindBugsResult> resultList = parseXML(reportFile);

		MeasuredResult.getInstance(instanceKey).putFindBugsList(resultList);
	}

	protected List<FindBugsResult> parseXML(File reportFile) {
		List<FindBugsResult> list = new ArrayList<>();

		BugCollection bugCollection = new SortedBugCollection();

		try {
			bugCollection.readXML(reportFile.toString());
		} catch (IOException | DocumentException ex) {
			throw new RuntimeException(ex);
		}

		for (BugInstance bug : bugCollection) {
			list.add(new FindBugsResult(bug));
		}

		return list;
	}

	protected String getTargetPackages(String instanceKey) {
		StringBuilder builder = new StringBuilder();

		for (String packageName : MeasuredResult.getInstance(instanceKey).getPackageList()) {
			if (packageName.equals("")) {
				LOGGER.warn("The project has class(es) with no package... FindBugs/FindSecBugs can't check with no package class(es)");
				MeasuredResult.getInstance(instanceKey).setWithDefaultPackageClasses(true);
				continue;
			}

			if (builder.length() != 0) {
				builder.append(",");
			}

			builder.append(packageName).append(".*");
			// Replace .* with .- to also analyze all subpackages.
		}

		LOGGER.info("target packages : {}", builder.toString());

		return builder.toString();
	}

	protected List<String> getArg() {
		return arg;
	}

	protected String getTargetDirectory() {
		return targetDirectory;
	}
}
