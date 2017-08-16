package com.samsungsds.analyst.code.api.test;

import java.text.NumberFormat;

import com.samsungsds.analyst.code.api.AnalysisMode;
import com.samsungsds.analyst.code.api.ArgumentInfo;
import com.samsungsds.analyst.code.api.CodeAnalyst;
import com.samsungsds.analyst.code.api.impl.CodeAnalystImpl;

public class APITest {
	public static void main(String[] args) {
		CodeAnalyst analyst = new CodeAnalystImpl();
		
		NumberFormat numberFormatter = NumberFormat.getInstance();
		
		analyst.addProgressObserver(progress -> {
			
			System.out.println("++++++++++++++++++++++++++++++++++++++++");
			System.out.print("Event : " + progress.getProgressEvent() + ", Current : " + progress.getCompletedPercent() + "%");
			System.out.println(", " + numberFormatter.format(progress.getElapsedTimeInMillisecond()) + " elapsed ms");
			System.out.println("++++++++++++++++++++++++++++++++++++++++");
			
		});
		
		ArgumentInfo argument = new ArgumentInfo();
		argument.setProject("D:\\workspace for tools\\code-analyst");
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
		
		String resultFile = analyst.analyze("C:\\Temp", argument);
		
		System.out.println("Result File : " + resultFile);
	}
}
