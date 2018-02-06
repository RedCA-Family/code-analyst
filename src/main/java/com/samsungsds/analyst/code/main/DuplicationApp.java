package com.samsungsds.analyst.code.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.util.LogUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.ini4j.Wini;

import com.samsungsds.analyst.code.sonar.DuplicationResult;
import com.samsungsds.analyst.code.util.FindFileUtils;

public class DuplicationApp {
	private enum Type {
		OUT, CSV, CPD_CSV;
	}

	private String outputFile;
	private static final String INSTANCE_KEY = DuplicationApp.class.getName();
	private Type type;

	public DuplicationApp(Type type, String outputFile) {
		this.outputFile = outputFile;
		this.type = type;
	}
	
	public void process() {
		checkInput();

		LogUtils.unsetDebugLevel();

		List<String> list = null;
		if (type == Type.OUT) {
			list = getDuplicationListFromIni();
		} else if (type == Type.CSV) {
			list = getDuplicationListFromCSV();
		} else {
			list = getDuplicationListFromCPD();
		}

		MeasuredResult.getInstance(INSTANCE_KEY).initialize(true, false);

		System.out.println("Duplicated lines : " + getDuplicatedLines(list));

		System.out.println();
		System.out.println("Duplicated file list : ");
		System.out.println(MeasuredResult.getInstance(INSTANCE_KEY).getDuplicatedBlockDebugInfo());
	}

	private void checkInput() {
		File file = new File(outputFile);
		
		if (!file.exists()) {
			throw new IllegalArgumentException("File not found : " + outputFile);
		}
	}
	
	private int getDuplicatedLines(List<String> list) {
		for (String line : list) {
			String[] data = line.split(FindFileUtils.COMMA_SPLITTER);
			
			DuplicationResult result = null;
			
			if (data.length == 6) {
				result = new DuplicationResult(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]),
						data[3], Integer.parseInt(data[4]), Integer.parseInt(data[5]));
			} else if (data.length == 5) {
				result = new DuplicationResult(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]),
						DuplicationResult.DUPLICATED_FILE_SAME_MARK, Integer.parseInt(data[3]), Integer.parseInt(data[4]));
			} else {
				System.out.println("Error line(skipped) : " + line);
				continue;
			}
			
			MeasuredResult.getInstance(INSTANCE_KEY).addDuplicationResult(result);
		}
		
		return MeasuredResult.getInstance(INSTANCE_KEY).getDuplicatedLines();
	}

	private List<String> getDuplicationListFromIni() {
		Wini ini;
		try {
			ini = new Wini(new File(outputFile));
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		
		int total = ini.get("Duplication", "total", Integer.class);
		List<String> list = new ArrayList<>();
		
		for (int i = 1; i <= total; i++) {
			list.add(ini.get("Duplication", Integer.toString(i)));
		}
		
		return list;
	}

	private List<String> getDuplicationListFromCSV() {

		List<String> list = new ArrayList<>();

		try (Reader in = new FileReader(outputFile)) {

			StringBuilder builder = new StringBuilder();
			//Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
			Iterable<CSVRecord> records = CSVFormat.RFC4180.withSkipHeaderRecord().parse(in);
			boolean isFirst = true;
			for (CSVRecord record : records) {
				if (isFirst) {
					isFirst = false;
					continue;
				}
				String no = record.get(0);
				String path = record.get(1);
				String startLine = record.get(2);
				String endLine = record.get(3);
				String duplicatedPath = record.get(4);
				String dupStartLine = record.get(5);
				String dupEndLine = record.get(6);

				//builder.append(no).append(",");
				builder.append(path).append(",");
				builder.append(startLine).append(",");
				builder.append(endLine).append(",");
				builder.append(duplicatedPath).append(",");
				builder.append(dupStartLine).append(",");
				builder.append(dupEndLine);

				list.add(builder.toString());
				builder.setLength(0);
			}
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}

		return list;
	}

	private List<String> getDuplicationListFromCPD() {

		List<String> list = new ArrayList<>();

		try (Reader in = new FileReader(outputFile)) {

			StringBuilder builder = new StringBuilder();
			Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
			int index = 1;
			for (CSVRecord record : records) {
				if (record.get(0).equals("")) {
					continue;
				}
				System.out.println("index : " + index++);

				int lines = Integer.parseInt(record.get(0));
				//int tokens = Integer.parseInt(record.get(1));
				int occurrences = Integer.parseInt(record.get(2));
				int startLine = Integer.parseInt(record.get(3));
				String path = record.get(4);

				for (int i = 5; i < 5 + (occurrences - 1) * 2; i+=2) {
					int targetStartLine = Integer.parseInt(record.get(i));
					String targetPath = record.get(i+1);

					builder.append(path.replaceAll(",", ":")).append(",");
					builder.append(startLine).append(",");
					builder.append(startLine + lines - 1).append(",");

					builder.append(targetPath.replaceAll(",",":")).append(",");
					builder.append(targetStartLine).append(",");
					builder.append(targetStartLine + lines - 1);

					list.add(builder.toString());
					//System.out.println(builder.toString());
					builder.setLength(0);
				}
			}
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}

		return list;
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Parameter is needed...");
			showHelp();
			return;
		}

		if (!args[0].equalsIgnoreCase("-type")) {
			System.out.println("type parameter needed...");
			showHelp();
			return;
		}

		Type type = null;
		if (args[1].equalsIgnoreCase("out")) {
			type = Type.OUT;
		} else if (args[1].equalsIgnoreCase("csv")) {
			type = Type.CSV;
		} else if (args[1].equalsIgnoreCase("cpd-csv")) {
			type = Type.CPD_CSV;
		} else {
			System.out.println("Type can be 'out', 'csv' or 'cpd-csv' file...");
			showHelp();
			return;
		}

		DuplicationApp app = new DuplicationApp(type, args[2]);
		
		String includeFilters = "";
		String excludeFilters = "";
		if (args.length > 3) {
			
			for (int index = 3; index < args.length; index++) {
				
				if (args[index].equals("-include")) {
					includeFilters = args[++index];
				} 
				if (args[index].equals("-exclude")) {
					excludeFilters = args[++index];
				}
			}
		}
		
		if (!includeFilters.equals("")) {
			System.out.println("Include : " + includeFilters);
		
			MeasuredResult.getInstance(INSTANCE_KEY).setIncludeFilters(includeFilters);
		}
		
		if (!excludeFilters.equals("")) {
			System.out.println("Exclude : " + excludeFilters);
			
			MeasuredResult.getInstance(INSTANCE_KEY).setExcludeFilters(excludeFilters);
		}
		
		try {
			app.process();
		} catch (IllegalArgumentException iae) {
			System.out.println(iae.getMessage());
		}
	}

	private static void showHelp() {
		System.out.println("\tjava .. com.samsungsds.analyst.code.main.DuplicationApp -type [out|csv|cpd-csv] \"output file\" [-include AntStyle.java] [-exclude AntStyle.java]");
		System.out.println("\t\ttype can be 'out', 'csv' or 'cpd-csv' file");
		System.out.println("\t\t* The parameter sequence have to be in the specified order");

		System.out.println();
	}
}

