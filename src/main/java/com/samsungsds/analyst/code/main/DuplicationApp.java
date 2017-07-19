package com.samsungsds.analyst.code.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.ini4j.Wini;

import com.samsungsds.analyst.code.sonar.DuplicationResult;

public class DuplicationApp {
	private String outputFile;

	public DuplicationApp(String outputFile) {
		this.outputFile = outputFile;
	}
	
	public void process() {
		checkInput();
		
		unsetDebugLevel();
		
		List<String> list = getDuplicationList();
		
		System.out.println("Duplicated lines : " + getDuplicatedLines(list));
	}
	
	private void unsetDebugLevel() {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration conf = ctx.getConfiguration();
		conf.getLoggerConfig("com.samsungsds.analyst.code").setLevel(Level.INFO);
		conf.getLoggerConfig("org.sonar").setLevel(Level.INFO);
		ctx.updateLoggers(conf);
	}

	private void checkInput() {
		File file = new File(outputFile);
		
		if (!file.exists()) {
			throw new IllegalArgumentException("File not found : " + outputFile);
		}
	}
	
	private int getDuplicatedLines(List<String> list) {
		for (String line : list) {
			String[] data = line.split("\\s*,\\s*");
			
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
			
			MeasuredResult.getInstance().addDuplicationResult(result);
		}
		
		return MeasuredResult.getInstance().getDuplicatedLines();
	}

	private List<String> getDuplicationList() {
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

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Parameter is needed...");
			System.out.println("\tjava .. com.samsungsds.analyst.code.main.DuplicationApp \"output file\"");
			System.out.println();
			return;
		}
		
		DuplicationApp app = new DuplicationApp(args[0]);
		
		try {
			app.process();
		} catch (IllegalArgumentException iae) {
			System.out.println(iae.getMessage());
		}
	}
}
