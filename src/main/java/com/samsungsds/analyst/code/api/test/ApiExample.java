package com.samsungsds.analyst.code.api.test;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import com.samsungsds.analyst.code.api.AnalysisMode;
import com.samsungsds.analyst.code.api.ArgumentInfo;
import com.samsungsds.analyst.code.api.CodeAnalyst;
import com.samsungsds.analyst.code.api.CodeAnalystFactory;
import com.samsungsds.analyst.code.api.TargetFileInfo;

public class ApiExample {

	private static final String TEMP_DIRECTORY = "C:\\Temp";

	private static final NumberFormat numberFormatter = NumberFormat.getInstance();

	public static void main(String[] args) {
		CodeAnalyst analyst = CodeAnalystFactory.create();

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
		argument.setSrc("src/main/java");
		argument.setBinary("target/classes");

		argument.setEncoding("UTF-8"); // default
		argument.setJavaVersion("1.8"); // default

		AnalysisMode mode = new AnalysisMode();
		mode.setCodeSize(true);
		mode.setDuplication(true);
		mode.setComplexity(true);
		mode.setSonarJava(true);
		mode.setPmd(true);
		mode.setFindBugs(true);
		mode.setFindSecBugs(true);
		mode.setDependency(true);
		mode.setWebResource(true);
		mode.setUnusedCode(true);

		argument.setMode(mode);

		argument.setDetailAnalysis(true);

		// argument.setDebug(true);

		// argument.setExclude("com/samsungsds/analyst/code/main/filter/*");

		TargetFileInfo targetFile = new TargetFileInfo();

		// addPackage() 또는 addFile()로 점검 대상 지정 (or 조건으로 처리됨)
		// - addPackage()는 선택된 패키지의 소스 전체
		// - addFile()은 선택된 소스

		// targetFile.addPackage("com.samsungsds.analyst.code.main"); // include sub-packages

		// targetFile.addFile("com.samsungsds.analyst.code.main", "MeasuredResult.java");
		// targetFile.addFile("com.samsungsds.analyst.code.main", "ResultProcessor.java");

        // Default package에 소스가 있는 경우 테스트
		//targetFile.setIncludeSubPackage(false);
		//targetFile.addPackage("");

		//argument.setExclude("**/FileManager.java");

        // Default package 파일만 추가하는 경우 테스트
		//targetFile.addFile("", "Test.java");

		// src가 상위인 경우 addFile 테스트
		argument.setSrc("src");
        targetFile.addFile("com.samsungsds.analyst.code.test", "Test.java");

		File temp = new File(TEMP_DIRECTORY);
		if (!temp.exists()) {
			temp.mkdirs();
		}

		String resultFile = analyst.analyze(TEMP_DIRECTORY, argument, targetFile);

		System.out.println("Result File : " + resultFile);

		// ResultInfo result = analyst.analyzeWithSeperatedResult(TEMP_DIRECTORY, argument, targetFile);

		// System.out.println("Result File : " + result.getOutputFile());
	}

}
