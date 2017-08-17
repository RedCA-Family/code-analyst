package com.samsungsds.analyst.code.api.test;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import com.samsungsds.analyst.code.api.AnalysisMode;
import com.samsungsds.analyst.code.api.ArgumentInfo;
import com.samsungsds.analyst.code.api.CodeAnalyst;
import com.samsungsds.analyst.code.api.TargetFileInfo;
import com.samsungsds.analyst.code.api.impl.CodeAnalystImpl;

public class ApiExample {
	private static final String TEMP_DIRECTORY = "C:\\Temp";
	
	private static final NumberFormat numberFormatter = NumberFormat.getInstance();

	public static void main(String[] args) {
		CodeAnalyst analyst = new CodeAnalystImpl();
		
		analyst.addProgressObserver(progress -> {
			System.out.println("++++++++++++++++++++++++++++++++++++++++");
			System.out.print("Event : " + progress.getProgressEvent() + ", Current : " + progress.getCompletedPercent() + "%");
			System.out.println(", " + numberFormatter.format(progress.getElapsedTimeInMillisecond()) + " elapsed ms");
			System.out.println("++++++++++++++++++++++++++++++++++++++++");
		});
		
		ArgumentInfo argument = new ArgumentInfo();
		try {
			argument.setProject(new File(".").getCanonicalPath());
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		argument.setSrc("src");
		argument.setBinary("target\\classes");
		
		AnalysisMode mode = new AnalysisMode();
		mode.setCodeSize(true);
		mode.setDuplication(true);
		mode.setComplexity(true);
		mode.setPmd(true);
		mode.setFindBugs(true);
		mode.setFindSecBugs(false);
		mode.setDependency(true);
		
		argument.setMode(mode);
		
		argument.setExclude("JDepend.java,com/samsungsds/analyst/code/main/filter/*.java");
		
		TargetFileInfo targetFile = new TargetFileInfo("com.samsungsds.analyst.code.main");
		targetFile.addFile("MeasuredResult.java");
		targetFile.addFile("ResultProcessor.java");
		
		File temp = new File(TEMP_DIRECTORY);
		if (!temp.exists()) {
			temp.mkdirs();
		}
		
		String resultFile = analyst.analyze(TEMP_DIRECTORY, argument, targetFile);
		
		System.out.println("Result File : " + resultFile);
	}
}
