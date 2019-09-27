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
			argument.setProject(new File(".").getCanonicalPath().replace("\\", "/"));
			//argument.setProject(new File(".").getCanonicalPath());
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		argument.setSrc("/src/main/java,/src/test/java");
		argument.setBinary("target/classes,target/test-classes");
		//argument.setSrc("src/main/java");
		//argument.setBinary("target/classes");

		argument.setEncoding("UTF-8"); // default
		argument.setJavaVersion("1.8"); // default

		//argument.setDebug(true);

		AnalysisMode mode = new AnalysisMode();
		mode.setCodeSize(true);
		mode.setDuplication(false);
		mode.setComplexity(false);
		mode.setSonarJava(false);
		mode.setPmd(false);
		mode.setFindBugs(false);
		mode.setFindSecBugs(false);
		mode.setDependency(false);
		mode.setJavascript(false);
		mode.setCss(false);
		mode.setHtml(false);
		mode.setUnusedCode(false);
		mode.setCkMetrics(true);
		mode.setCheckStyle(true);

		//System.setProperty("print.path.filter", "true");

		argument.setMode(mode);

		argument.setDetailAnalysis(true);

		//argument.setWebapp("src/main/webapp");

		// argument.setDebug(true);

		argument.setExclude("com/samsungsds/analyst/code/main/filter/*");
        //argument.setExclude("@./include-files.txt");

		//argument.setInclude("**/com/samsungsds/**");
		// include와 TargetFileInfo가 같이 들어오면 "and"로 처리됨

		TargetFileInfo targetFile = new TargetFileInfo();

		// addPackage() 또는 addFile()로 점검 대상 지정 (or 조건으로 처리됨)
		// - addPackage()는 선택된 패키지의 소스 전체
		// - addFile()은 선택된 소스

		// targetFile.addPackage("com.samsungsds.analyst.code.main"); // include sub-packages
		targetFile.addPackage("com.samsungsds.analyst.code"); // include sub-packages

		// targetFile.addFile("com.samsungsds.analyst.code.main", "MeasuredResult.java");
		// targetFile.addFile("com.samsungsds.analyst.code.main", "ResultProcessor.java");

        // Default package에 소스가 있는 경우 테스트
		//targetFile.setIncludeSubPackage(false);
		//targetFile.addPackage("");

		//argument.setExclude("**/FileManager.java");

        // Default package 파일만 추가하는 경우 테스트
		//targetFile.addFile("", "Test.java");

		// src가 상위인 경우 addFile 테스트
		//argument.setSrc("src");
        //targetFile.addFile("com.samsungsds.analyst.code.test", "Test.java");

		// addFileExactly 테스트
		//targetFile.addFileExactly("com.samsungsds.analyst.code.test", "Test.java");
		//targetFile.addFileExactly("com.samsungsds.analyst.code.api.impl", "CodeAnalystImpl.java");
		//targetFile.addFileExactly("com.samsungsds.analyst.code.pmd", "ComplexityAnalysisLauncherTest.java");

		argument.setSaveCatalog(true);

		File temp = new File(TEMP_DIRECTORY);
		if (!temp.exists()) {
			temp.mkdirs();
		}

		String resultFile = analyst.analyze(TEMP_DIRECTORY, argument, targetFile);

		System.out.println("Result File : " + resultFile);

		// ResultInfo result = analyst.analyzeWithSeparatedResult(TEMP_DIRECTORY, argument, targetFile);

		// System.out.println("Result File : " + result.getOutputFile());
	}

}
