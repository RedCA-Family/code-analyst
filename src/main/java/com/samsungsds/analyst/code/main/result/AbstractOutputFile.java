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
package com.samsungsds.analyst.code.main.result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.samsungsds.analyst.code.api.Language;
import com.samsungsds.analyst.code.checkstyle.CheckStyleResult;
import com.samsungsds.analyst.code.ckmetrics.CkMetricsResult;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.sonar.DuplicationResult;
import com.samsungsds.analyst.code.sonar.SonarIssueResult;
import com.samsungsds.analyst.code.sonar.WebResourceResult;
import com.samsungsds.analyst.code.unusedcode.UnusedCodeResult;

public abstract class AbstractOutputFile {
	protected PrintWriter writer;

	public void process(File file, CliParser cli, MeasuredResult result) {
		try {
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)));

			open(result);

			writeSeparator();

			writeProjectInfo(cli, result);

			writeSummary(result);

			if (result.isSaveCatalog()) {
				writeFilePathList(result.getFilePathList());
			}

			if (result.getIndividualMode().isDuplication()) {
				writeDuplication(result.getDuplicationList());
			}

			if (result.getIndividualMode().isComplexity()) {
				writeComplexity(result.getComplexityList());
			}

			if ((result.getLanguageType() == Language.JAVA && result.getIndividualMode().isSonarJava())
					|| (result.getLanguageType() == Language.JAVA && result.getIndividualMode().isJavascript())
					|| (result.getLanguageType() == Language.JAVASCRIPT && result.getIndividualMode().isJavascript())
                    || (result.getLanguageType() == Language.CSHARP && result.getIndividualMode().isSonarCSharp())
                    || (result.getLanguageType() == Language.PYTHON && result.getIndividualMode().isSonarPython())) {
				writeSonarIssue(result.getSonarIssueList());
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

			if (result.getIndividualMode().isWebResources()) {
				writeWebResource(result.getWebResourceList());
			}

			if (result.getIndividualMode().isDependency()) {
				writeAcyclicDependencies(result.getAcyclicDependencyList());
			}

			if (result.getIndividualMode().isUnusedCode()) {
				writeUnusedCode(result.getUnusedCodeList());
			}

			if (result.getIndividualMode().isCkMetrics()) {
				writeCkMetrics(result.getCkMetricsResultList());
			}

			if (result.getIndividualMode().isCheckStyle()) {
                writeCheckStyle(result.getCheckStyleList());
            }

			writeSeparator();

			close(writer);

		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			IOAndFileUtils.closeQuietly(writer);
		}
	}

	protected abstract void open(MeasuredResult result);

	protected abstract void writeSeparator();

	protected abstract void writeCkMetrics(List<CkMetricsResult> ckMetricsResultList);

	protected abstract void writeAcyclicDependencies(List<String> acyclicDependencyList);

	protected abstract void writeWebResource(List<WebResourceResult> webResourceList);

	protected abstract void writeFindBugs(List<FindBugsResult> findBugsList);

	protected abstract void writeFindSecBugs(List<FindBugsResult> findSecBugsList);

	protected abstract void writeSonarIssue(List<SonarIssueResult> sonarJavaList);

	protected abstract void writePmd(List<PmdResult> pmdList);

	protected abstract void writeComplexity(List<ComplexityResult> complexityList);

	protected abstract void writeDuplication(List<DuplicationResult> dulicationList);

	protected abstract void writeUnusedCode(List<UnusedCodeResult> unusedCodeList);

	protected abstract void writeCheckStyle(List<CheckStyleResult> checkStyleList);

	protected abstract void writeSummary(MeasuredResult result);

	protected abstract void writeFilePathList(List<String> filePathList);

	protected abstract void writeProjectInfo(CliParser cli, MeasuredResult result);

	protected abstract void close(PrintWriter writer);

}
