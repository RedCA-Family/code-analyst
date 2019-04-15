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
package com.samsungsds.analyst.code.sonar;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import com.samsungsds.analyst.code.api.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sonar.api.utils.ZipUtils;
import org.sonar.core.util.CloseableIterator;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReport.Component;
import org.sonar.scanner.protocol.output.ScannerReport.Component.ComponentType;
import org.sonar.scanner.protocol.output.ScannerReport.Duplicate;
import org.sonar.scanner.protocol.output.ScannerReport.Metadata;
import org.sonar.scanner.protocol.output.ScannerReportReader;

import com.google.common.io.Files;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

public class ReportFileReader implements Closeable {
	private static final Logger LOGGER = LogManager.getLogger(ReportFileReader.class);

	private static final String METRIC_CLASSES = "classes";
	private static final String METRIC_COMMENT_LINES = "comment_lines";
	private static final String METRIC_FUNCTIONS = "functions";
	private static final String METRIC_NCLOC = "ncloc";
	private static final String METRIC_STATEMENTS = "statements";

	private File zipFile = null;
	private File toDir = Files.createTempDir();

	private ScannerReportReader reader = null;

	private String instanceKey;

	public ReportFileReader(File zipFile, String instanceKey) {
		this.zipFile = zipFile;
		this.instanceKey = instanceKey;
	}

	public void read() throws IOException {
		ZipUtils.unzip(zipFile, toDir);

		reader = new ScannerReportReader(toDir);

		Metadata metadata = reader.readMetadata();
		int rootComponentRef = metadata.getRootComponentRef();

		Component project = reader.readComponent(rootComponentRef);

		readComponent(project);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("--------------------------------------------------------------------------------");
			LOGGER.debug("Duplicated lines : {}{}", IOAndFileUtils.CR_LF, MeasuredResult.getInstance(instanceKey).getDuplicatedBlockDebugInfo());
			LOGGER.debug("--------------------------------------------------------------------------------");
		}
	}

	protected void readComponent(Component component) {
		LOGGER.debug("Component : {}", component.getPath());

		MeasuredResult instance = MeasuredResult.getInstance(instanceKey);

		if (component.getType().equals(ComponentType.DIRECTORY)) {
			instance.addDirectories(1);
		} else if (component.getType().equals(ComponentType.FILE)) {

			instance.addFilePathList(component.getPath());

			// js의 경우 *.js, *.jsx, *.vue 파일 분석이 되나, language는 "js"만 리턴됨
			if ((instance.getLanguageType() == Language.JAVA && "java".equals(component.getLanguage()))
					|| (instance.getLanguageType() == Language.JAVASCRIPT && "js".equals(component.getLanguage()))) {

				if (instance.getIndividualMode().isCodeSize()) {
					calculateCodeSize(component);
				}
				if (instance.getIndividualMode().isDuplication()
						&& reader.hasCoverage(component.getRef())
						&& !instance.isTokenBased()) {
					try (CloseableIterator<ScannerReport.Duplication> it = reader.readComponentDuplications(component.getRef())) {
						while (it.hasNext()) {
							ScannerReport.Duplication dup = it.next();
							
							instance.addDuplicatedBlocks();
							
							String path = component.getPath();
							int startLine = dup.getOriginPosition().getStartLine();
							int endLine = dup.getOriginPosition().getEndLine();
							
							for (Duplicate d : dup.getDuplicateList()) {
								String duplicatedPath = null;
								try {
									duplicatedPath = reader.readComponent(d.getOtherFileRef()).getPath();
								} catch (IllegalStateException ise) { // Unable to find report for component #...
									duplicatedPath = DuplicationResult.DUPLICATED_FILE_SAME_MARK;
								}
								DuplicationResult result = new DuplicationResult(path, startLine, endLine, duplicatedPath, d.getRange().getStartLine(), d.getRange().getEndLine());
								
								instance.addDuplicationResult(result);
							}
						}
					}
				}
				if ((instance.getLanguageType() == Language.JAVA && instance.getIndividualMode().isSonarJava())
						|| (instance.getLanguageType() == Language.JAVASCRIPT && instance.getIndividualMode().isJavascript())) {
					try (CloseableIterator<ScannerReport.Issue> it = reader.readComponentIssues(component.getRef())) {
						while (it.hasNext()) {
							ScannerReport.Issue issue = it.next();
							SonarIssueResult sonarJavaResult = new SonarIssueResult(component.getPath(), issue.getRuleRepository(), issue.getRuleKey(), issue.getMsg(), reverseSeverity(issue.getSeverityValue()), issue.getTextRange().getStartLine(), issue.getTextRange().getStartOffset(), issue.getTextRange().getEndLine(), issue.getTextRange().getEndOffset());
							instance.addSonarIssueResult(sonarJavaResult);
						}
					} catch (Exception e) {
						throw new IllegalStateException("Can't read issues for " + component, e);
					}
				}
			}
			if (instance.getLanguageType() == Language.JAVA) {
				if ("js".equals(component.getLanguage()) || "web".equals(component.getLanguage()) || "css".equals(component.getLanguage()) || "less".equals(component.getLanguage()) || "scss".equals(component.getLanguage())) {
					if ("js".equals(component.getLanguage()) && instance.getIndividualMode().isWebResourcesOnly()) {
						calculateCodeSize(component);
					}
					try (CloseableIterator<ScannerReport.Issue> it = reader.readComponentIssues(component.getRef())) {
						while (it.hasNext()) {
							ScannerReport.Issue issue = it.next();
							WebResourceResult webResourceResult = new WebResourceResult(component.getLanguage(), component.getPath(), issue.getRuleRepository(), issue.getRuleKey(), issue.getMsg(), reverseSeverity(issue.getSeverityValue()), issue.getTextRange().getStartLine(), issue.getTextRange().getStartOffset(), issue.getTextRange().getEndLine(), issue.getTextRange().getEndOffset());
							instance.addWebResourceResult(webResourceResult);
						}
					} catch (Exception e) {
						throw new IllegalStateException("Can't read issues for " + component, e);
					}
				}
			}
		}

		for (int ref : component.getChildRefList()) {
			Component child = reader.readComponent(ref);

			readComponent(child);
		}
	}

	private void calculateCodeSize(Component component) {
		MeasuredResult.getInstance(instanceKey).addFiles(1);
		MeasuredResult.getInstance(instanceKey).addLines(component.getLines());

		try (CloseableIterator<ScannerReport.Measure> it = reader.readComponentMeasures(component.getRef())) {
			while (it.hasNext()) {
				ScannerReport.Measure measure = it.next();

				if (measure.getMetricKey().equals(METRIC_CLASSES)) {
					MeasuredResult.getInstance(instanceKey).addClasses(measure.getIntValue().getValue());
				} else if (measure.getMetricKey().equals(METRIC_COMMENT_LINES)) {
					MeasuredResult.getInstance(instanceKey).addCommentLines(measure.getIntValue().getValue());
				} else if (measure.getMetricKey().equals(METRIC_FUNCTIONS)) {
					MeasuredResult.getInstance(instanceKey).addFunctions(measure.getIntValue().getValue());
				} else if (measure.getMetricKey().equals(METRIC_NCLOC)) {
					MeasuredResult.getInstance(instanceKey).addNcloc(measure.getIntValue().getValue());
				} else if (measure.getMetricKey().equals(METRIC_STATEMENTS)) {
					MeasuredResult.getInstance(instanceKey).addStatements(measure.getIntValue().getValue());
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException("Can't read measures for " + component, e);
		}
	}

	private int reverseSeverity(int severityValue) {
		switch (severityValue) {
		case 1:
			return 5;
		case 2:
			return 4;
		case 3:
			return 3;
		case 4:
			return 2;
		case 5:
			return 1;
		default:
			return 0;
		}
	}

	@Override
	public void close() throws IOException {
		IOAndFileUtils.deleteDirectory(toDir);
	}
}
