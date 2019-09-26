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
package com.samsungsds.analyst.code.main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.samsungsds.analyst.code.main.detailed.Duplication;
import com.samsungsds.analyst.code.util.LogUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.ini4j.Wini;

import com.samsungsds.analyst.code.sonar.DuplicationResult;
import com.samsungsds.analyst.code.util.FindFileUtils;

import static com.samsungsds.analyst.code.util.CSVUtil.getString;
import static com.samsungsds.analyst.code.util.CSVUtil.getStringsWithComma;

public class DuplicationApp {
	private enum Type {
		OUT, CSV, CPD_CSV
	}

	private String outputFile;
	private static final String INSTANCE_KEY = DuplicationApp.class.getName();
	private Type type;
	private boolean saveCSVFile = false;

	public DuplicationApp(Type type, String outputFile) {
		this.outputFile = outputFile;
		this.type = type;
	}
	
	public void process() {
		checkInput();

		LogUtils.unsetDebugLevel();

		List<String> list;
		if (type == Type.OUT) {
			list = getDuplicationListFromIni();
		} else if (type == Type.CSV) {
			list = getDuplicationListFromCSV();
		} else {
			list = getDuplicationListFromCPD();
		}

		//IndividualMode individualMode = new IndividualMode();
		MeasuredResult.getInstance(INSTANCE_KEY).initialize(true, false);

		System.out.println("Duplicated lines : " + getDuplicatedLines(list));

		System.out.println();
		System.out.println("Duplicated file list : ");
		System.out.println(MeasuredResult.getInstance(INSTANCE_KEY).getDuplicatedBlockDebugInfo());

		if (saveCSVFile) {
			System.out.println();
			System.out.println("Save CSV File : duplication.csv");

			String csvFile = "duplication-result.csv";

			saveDuplicationListToCSV(csvFile);
		} else {
			// call this method for get top duplication and ignore returned list 
			MeasuredResult.getInstance(INSTANCE_KEY).getDuplicationList();
		}
		System.out.println();

		printTopDuplicationList();
	}

	private void saveDuplicationListToCSV(String csvFile) {
		try (PrintWriter csvWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile))))) {
            csvWriter.println("No,Path,Start line,End line,Duplicated path,Start line,End line");

            int count = 0;

			for (DuplicationResult result : MeasuredResult.getInstance(INSTANCE_KEY).getDuplicationList()) {
				csvWriter.print(++count + ",");
				csvWriter.print(getStringsWithComma(result.getPath(), getString(result.getStartLine()), getString(result.getEndLine())));
				csvWriter.print(",");
				csvWriter.print(getStringsWithComma(result.getDuplicatedPath(), getString(result.getDuplicatedStartLine()), getString(result.getDuplicatedEndLine())));
				csvWriter.println();
			}

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
	}

	private void printTopDuplicationList() {
		System.out.println("[TopDuplication]");
		System.out.println("path, start line, end line, count, total duplicated lines");

		int count = 0;
		for (Duplication result : MeasuredResult.getInstance(INSTANCE_KEY).getTopDuplicationList()) {
			System.out.print(++count + " = ");
			System.out.print(getStringsWithComma(result.getPath(), getString(result.getStartLine()), getString(result.getEndLine())));
			System.out.print(",");
			System.out.print(result.getCount());
			System.out.print(",");
			System.out.print(result.getTotalDuplicatedLines());
			System.out.println();
		}
		System.out.println();
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
				int diff = 0;
				if (record.size() == 8) {			// with project key
					diff = 1;
				} else if (record.size() == 7) {	// without project key
					diff = 0;
				} else {
					throw new IllegalStateException("CSV's record error");
				}
				//String no = record.get(0);
				//String project = record.get(0 + diff);
				String path = record.get(1 + diff);
				String startLine = record.get(2 + diff);
				String endLine = record.get(3 + diff);
				String duplicatedPath = record.get(4 + diff);
				String dupStartLine = record.get(5 + diff);
				String dupEndLine = record.get(6 + diff);

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
			//int index = 1;
			for (CSVRecord record : records) {
				if (record.get(0).equals("")) {
					continue;
				}
				//System.out.println("index : " + index++);

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
				if (args[index].equals("-save")) {
					app.saveCSVFile = true;
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
		System.out.println("\tjava .. com.samsungsds.analyst.code.main.DuplicationApp -type [out|csv|cpd-csv] \"output file\" [-include AntStyle.java] [-exclude AntStyle.java] -save");
		System.out.println("\t\ttype can be 'out', 'csv' or 'cpd-csv' file");
		System.out.println("\t\t* The parameter sequence have to be in the specified order");

		System.out.println();
	}
}

