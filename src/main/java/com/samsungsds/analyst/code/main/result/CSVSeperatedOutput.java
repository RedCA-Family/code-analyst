package com.samsungsds.analyst.code.main.result;

import static com.samsungsds.analyst.code.util.CSVUtil.getString;
import static com.samsungsds.analyst.code.util.CSVUtil.getStringsWithComma;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.sonar.DuplicationResult;
import com.samsungsds.analyst.code.sonar.SonarJavaResult;
import com.samsungsds.analyst.code.sonar.WebResourceResult;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

public class CSVSeperatedOutput {
	private static final Logger LOGGER = LogManager.getLogger(CSVSeperatedOutput.class);

	private MeasuredResult result;

	public CSVSeperatedOutput(MeasuredResult result) {
		this.result = result;
	}

	public void writeDuplication(List<DuplicationResult> list) {
		String csvFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-duplication.csv";

		try (PrintWriter csvWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile))))) {
			csvWriter.println("No,Path,Start line,End line,Duplicated path,Start line,End line");

			int count = 0;
			synchronized (list) {
				for (DuplicationResult result : list) {
					csvWriter.print(++count + ",");
					csvWriter.print(getStringsWithComma(result.getPath(), getString(result.getStartLine()), getString(result.getEndLine())));
					csvWriter.print(",");
					csvWriter.print(getStringsWithComma(result.getDuplicatedPath(), getString(result.getDuplicatedStartLine()), getString(result.getDuplicatedEndLine())));
					csvWriter.println();
				}
			}

		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		LOGGER.info("Result seperated file saved : {}", csvFile);
	}

	public void writeComplexity(List<ComplexityResult> list) {
		String csvFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-complexity.csv";

		try (PrintWriter csvWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile))))) {
			csvWriter.println("No,Path,Line,Method,Complexity");

			int count = 0;
			synchronized (list) {
				Collections.sort(list, (r1, r2) -> (r2.getComplexity() - r1.getComplexity()));

				for (ComplexityResult result : list) {
					if (result.getComplexity() <= 20) {
						break;
					}
					csvWriter.print(++count + ",");
					csvWriter.print(getStringsWithComma(result.getPath(), getString(result.getLine()), result.getMethodName(), getString(result.getComplexity())));
					csvWriter.println();
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		LOGGER.info("Result seperated file saved : {}", csvFile);
	}

	public void writeSonarJava(List<SonarJavaResult> list) {
		String csvFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-sonarjava.csv";

		try (PrintWriter csvWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile))))) {
			csvWriter.println("No,Type,Path,Rule,Message,Priority,Start line,Start offset,End line,End offset");

			int count = 0;
			synchronized (list) {
				for (SonarJavaResult result : list) {
					csvWriter.print(++count + ",");
					csvWriter.print(getStringsWithComma(result.getIssueType().toString(),
							result.getPath(), result.getRuleKey(), result.getMsg(), getString(result.getSeverity()), getString(result.getStartLine()),
							getString(result.getStartOffset()), getString(result.getEndLine()), getString(result.getEndOffset())));
					csvWriter.println();
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		LOGGER.info("Result seperated file saved : {}", csvFile);
	}

	public void writePmd(List<PmdResult> list) {
		String csvFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-pmd.csv";

		try (PrintWriter csvWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile))))) {
			csvWriter.println("No,Type,Path,Line,Rule,Priority,Description");

			int count = 0;
			synchronized (list) {
				for (PmdResult result : list) {
					csvWriter.print(++count + ",");
					csvWriter.print(getStringsWithComma(result.getIssueType().toString(), result.getPath(), getString(result.getLine()),
							result.getRule(), getString(result.getPriority()), result.getDescription()));
					csvWriter.println();
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		LOGGER.info("Result seperated file saved : {}", csvFile);
	}

	public void writeFindBugsAndFindSecBugs(List<FindBugsResult> list, String title) {
		String csvFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-" + title.toLowerCase() + ".csv";

		try (PrintWriter csvWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile))))) {
			csvWriter.println("No,Type,Package,File,Start line,End line,Pattern key,Pattern,Priority,Class,Field,Local var,Method,Message");

			int count = 0;
			synchronized (list) {
				for (FindBugsResult result : list) {
					csvWriter.print(++count + ",");
					csvWriter.print(getStringsWithComma(result.getIssueType().toString(), result.getPackageName(), result.getFile(),
							getString(result.getStartLine()), getString(result.getEndLine()), result.getPatternKey(), result.getPattern()));
					csvWriter.print(", ");
					csvWriter.print(getStringsWithComma(getString(result.getPriority()), result.getClassName(), result.getField(), result.getLocalVariable(), result.getMethod(), result.getMessage()));
					csvWriter.println();
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		LOGGER.info("Result seperated file saved : {}", csvFile);
	}

	public void writeWebResource(List<WebResourceResult> list) {
		String csvFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-webresource.csv";

		try (PrintWriter csvWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile))))) {
			csvWriter.println("No,Path,Rule,Message,Priority,Start line,Start offset,End line,End offset");

			int count = 0;
			synchronized (list) {
				for (WebResourceResult result : list) {
					csvWriter.print(++count + ",");
					csvWriter.print(getStringsWithComma(result.getPath(), result.getRuleKey(), result.getMsg(), getString(result.getSeverity()), getString(result.getStartLine()),
							getString(result.getStartOffset()), getString(result.getEndLine()), getString(result.getEndOffset())));
					csvWriter.println();
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		LOGGER.info("Result seperated file saved : {}", csvFile);
	}

}
