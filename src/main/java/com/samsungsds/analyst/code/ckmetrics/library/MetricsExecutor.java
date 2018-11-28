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

Modified from CK metrics calculator(https://github.com/mauricioaniche/ck) under Apache 2.0 license
@author Mauricio Aniche
 */
package com.samsungsds.analyst.code.ckmetrics.library;

import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.Callable;

import com.samsungsds.analyst.code.ckmetrics.library.metric.ClassInfo;
import com.samsungsds.analyst.code.ckmetrics.library.metric.Metric;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

public class MetricsExecutor extends FileASTRequestor {

	private CKReport report;
	private Callable<List<Metric>> metrics;
	
	private static Logger LOGGER = LogManager.getLogger(MetricsExecutor.class);
	
	public MetricsExecutor(Callable<List<Metric>> metrics) {
		this.metrics = metrics;
		this.report = new CKReport();
	}

	@Override
	public void acceptAST(String sourceFilePath, CompilationUnit cu) {
		
		CKNumber result = null;
		
		try {
			ClassInfo info = new ClassInfo();
			cu.accept(info);
			if (info.getClassName() == null) return;
		
			result = new CKNumber(sourceFilePath, info.getClassName(), info.getType());
			
			int loc = new LOCCalculator().calculate(new FileInputStream(sourceFilePath));
			result.setLoc(loc);

			for (Metric visitor : metrics.call()) {
				visitor.execute(cu, result, report);
				visitor.setResult(result);
			}
			LOGGER.debug(result);
			report.add(result);
		} catch(Exception e) {
			if (result != null) result.error();
			LOGGER.error("error in " + sourceFilePath, e);
		}
	}
	
	public CKReport getReport() {
		return report;
	}
	
}
