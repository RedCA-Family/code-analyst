package com.samsungsds.analyst.code.sonar;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

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

import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

public class ReportFileReader implements Closeable {
	private static final Logger LOGGER = LogManager.getLogger(ReportFileReader.class);
	
	private static final String METRIC_CLASSES = "classes";
	private static final String METRIC_COMMENT_LINES = "comment_lines";
	private static final String METRIC_FUNCTIONS = "functions";
	private static final String METRIC_LINES = "lines";
	private static final String METRIC_NCLOC = "ncloc";
	private static final String METRIC_STATEMENTS = "statements";
	
	private File zipFile = null;
	private File toDir = new File(".tmp");
	
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
		
		if (component.getType().equals(ComponentType.DIRECTORY)) {
			MeasuredResult.getInstance(instanceKey).addDirectories(1);
		} else if (component.getType().equals(ComponentType.FILE)) {
			MeasuredResult.getInstance(instanceKey).addFiles(1);

			try (CloseableIterator<ScannerReport.Measure> it = reader.readComponentMeasures(component.getRef())) {
				while (it.hasNext()) {
					ScannerReport.Measure measure = it.next();

					if (measure.getMetricKey().equals(METRIC_CLASSES)) {
						MeasuredResult.getInstance(instanceKey).addClasses(measure.getIntValue().getValue());
					} else if (measure.getMetricKey().equals(METRIC_COMMENT_LINES)) {
						MeasuredResult.getInstance(instanceKey).addCommentLines(measure.getIntValue().getValue());
					} else if (measure.getMetricKey().equals(METRIC_FUNCTIONS)) {
						MeasuredResult.getInstance(instanceKey).addFunctions(measure.getIntValue().getValue());
					} else if (measure.getMetricKey().equals(METRIC_LINES)) {
						MeasuredResult.getInstance(instanceKey).addLines(measure.getIntValue().getValue());
					} else if (measure.getMetricKey().equals(METRIC_NCLOC)) {
						MeasuredResult.getInstance(instanceKey).addNcloc(measure.getIntValue().getValue());
					} else if (measure.getMetricKey().equals(METRIC_STATEMENTS)) {
						MeasuredResult.getInstance(instanceKey).addStatements(measure.getIntValue().getValue());
					}
				}
			} catch (Exception e) {
				throw new IllegalStateException("Can't read measures for " + component, e);
			}
			
			if (reader.hasCoverage(component.getRef())) {
				try (CloseableIterator<ScannerReport.Duplication> it = reader.readComponentDuplications(component.getRef())) {
					while (it.hasNext()) {
						ScannerReport.Duplication dup = it.next();
						
						String path = component.getPath();
						int startLine = dup.getOriginPosition().getStartLine();
						int endLine = dup.getOriginPosition().getEndLine();
						
						for (Duplicate d : dup.getDuplicateList()) {
							String duplicatedPath = null;
							try {
								duplicatedPath = reader.readComponent(d.getOtherFileRef()).getPath();
							} catch (IllegalStateException ise) {	// Unable to find report for component #...
								duplicatedPath = DuplicationResult.DUPLICATED_FILE_SAME_MARK;
							}
							DuplicationResult result = new DuplicationResult(path, startLine, endLine, duplicatedPath, d.getRange().getStartLine(), d.getRange().getEndLine());
							
							MeasuredResult.getInstance(instanceKey).addDuplicationResult(result);
						}						
					}
				} catch (Exception e) {
					throw new IllegalStateException("Can't read duplications for " + component, e);
				}
			}
		}
		
		for (int ref : component.getChildRefList()) {			
			Component child = reader.readComponent(ref);

			readComponent(child);
		}
	}

	@Override
	public void close() throws IOException {
		IOAndFileUtils.deleteDirectory(toDir);
	}
}
