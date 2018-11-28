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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.samsungsds.analyst.code.ckmetrics.library.metric.*;
import com.samsungsds.analyst.code.main.MeasuredResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import com.google.common.collect.Lists;

public class CK {
	private static Logger LOGGER = LogManager.getLogger(CK.class);

	private static final int MAX_AT_ONCE;
	
	static {
		String jdtMax = System.getProperty("jdt.max");
		if (jdtMax != null) {
			MAX_AT_ONCE = Integer.parseInt(jdtMax);
		} else {
			long maxMemory = Runtime.getRuntime().maxMemory() / (1 << 20); // in MiB

			if (maxMemory >= 2000) MAX_AT_ONCE = 400;
			else if (maxMemory >= 1500) MAX_AT_ONCE = 300;
			else if (maxMemory >= 1000) MAX_AT_ONCE = 200;
			else if (maxMemory >= 500) MAX_AT_ONCE = 100;
			else MAX_AT_ONCE = 25;
		}
	}

    private final NOCExtras extras;

    public List<Callable<Metric>> pluggedMetrics;

	public CK() {
		this.pluggedMetrics = new ArrayList<>();
		this.extras = new NOCExtras();
	}
	
	public CK plug(Callable<Metric> metric) {
		this.pluggedMetrics.add(metric);
		return this;
	}
	
	public CKReport calculate(String path, String instanceKey) {
		String[] srcDirs = FileUtils.getAllDirs(path);
		String[] javaFiles = FileUtils.getAllJavaFiles(path);

		LOGGER.info("Found " + javaFiles.length + " java files");

		MeasuredResult result = MeasuredResult.getInstance(instanceKey);
		javaFiles = Arrays.stream(javaFiles).filter(filePath -> !MeasuredResult.getInstance(instanceKey).haveToSkip(result.getConvertedFilePath(filePath))).toArray(String[]::new);

		LOGGER.info("Filtered files : {}", javaFiles.length);

		MetricsExecutor storage = new MetricsExecutor(() -> metrics());
		
		List<List<String>> partitions = Lists.partition(Arrays.asList(javaFiles), MAX_AT_ONCE);
		LOGGER.info("Max partition size: " + MAX_AT_ONCE + ", total partitions=" + partitions.size());

		for (List<String> partition : partitions) {
			LOGGER.info("Next partition");
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			
			parser.setResolveBindings(true);
			parser.setBindingsRecovery(true);
			
			Map<String, String> options = JavaCore.getOptions();
			JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
			parser.setCompilerOptions((options));
			parser.setEnvironment(null, srcDirs, null, true);
			parser.createASTs(partition.toArray(new String[partition.size()]), null, new String[0], storage, null);
		}
		
		LOGGER.info("Finished parsing");
        CKReport report = storage.getReport();
        extras.update(report);
        return report;
    }
	
	private List<Metric> metrics() {
		List<Metric> all = defaultMetrics();
		all.addAll(userMetrics());
		
		return all;
	}

	private List<Metric> defaultMetrics() {
		return new ArrayList<>(Arrays.asList(new DIT(), new NOC(extras), new WMC(), new CBO(), new LCOM(), new RFC(), new NOM(),
				new NOF(), new NOPF(), new NOSF(),
				new NOPM(), new NOSM(), new NOSI()));
	}

	private List<Metric> userMetrics() {
		try {
			List<Metric> userMetrics = new ArrayList<Metric>();

			for (Callable<Metric> metricToBeCreated : pluggedMetrics) {
				userMetrics.add(metricToBeCreated.call());
			}

			return userMetrics;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
