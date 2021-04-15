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
package com.samsungsds.analyst.code.ckmetrics;

import com.samsungsds.analyst.code.ckmetrics.gr.spinellis.ckjm.ClassMetricsContainer;
import com.samsungsds.analyst.code.ckmetrics.gr.spinellis.ckjm.MetricsFilter;
import com.samsungsds.analyst.code.ckmetrics.utils.JarUtils;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.main.subject.TargetFile;
import com.samsungsds.analyst.code.main.subject.TargetManager;
import com.samsungsds.analyst.code.util.FindFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

public class CkMetricsAnalysisLauncher implements CkMetricsAnalysis {
    private static final Logger LOGGER = LogManager.getLogger(CkMetricsAnalysisLauncher.class);

    private String projectBaseDir;
    private String sourceDirectories;
    private String binaryDirectories;

    @Override
    public void setProjectBaseDir(String directory) {
        this.projectBaseDir = directory;
    }

    @Override
    public void setSourceDirectories(String directories) {
        this.sourceDirectories = directories;
    }

    @Override
    public void setBinaryDirectories(String directories) {
        this.binaryDirectories = directories;
    }

    @Override
    public void run(String instanceKey) {
        MeasuredResult result = MeasuredResult.getInstance(instanceKey);

        List<TargetFile> targetFileList = TargetManager.getInstance(instanceKey).getTargetFileList(projectBaseDir, sourceDirectories, binaryDirectories, result);

        LOGGER.info("CK Metrics Target Files : {}", targetFileList.size());

        File jar = getJarFileFromBinaryDirectories();
        result.addTempFileToBeDeleted(jar);

        MetricsFilter.setRepositoryClassPath(jar.toString());

        ClassMetricsContainer cm = new ClassMetricsContainer();
        for (TargetFile targetFile : targetFileList) {
            if (targetFile.isBinaryFileExists()) {
                MetricsFilter.processClass(cm, jar.toString() + " " + targetFile.getBinaryFilePath().replace("\\", "/"));
            } else {
                LOGGER.warn("No binary file : {}", targetFile.getSourceFilePath());
            }
        }

        CkMetricsResultHandler handler = new CkMetricsResultHandler();
        cm.printMetrics(handler);
        List<CkMetricsResult> ckMetricResults = handler.getCkMetricsResults();

        MeasuredResult.getInstance(instanceKey).setCkMetricsResultList(ckMetricResults);

        LOGGER.info("CK Metrics Total Result : {}", MeasuredResult.getInstance(instanceKey).getCkMetricsResultList().size());

        MetricsFilter.close();

        jar.delete();
    }

    private File getJarFileFromBinaryDirectories() {
        StringBuilder classpaths = new StringBuilder();
        for (String target : binaryDirectories.split(FindFileUtils.COMMA_SPLITTER)) {
            if (classpaths.length() != 0) {
                classpaths.append(System.getProperty("path.separator"));
            }
            classpaths.append(projectBaseDir + File.separator + target);
        }
        File jar = JarUtils.getJarFromClasspathDirectories(classpaths.toString());
        LOGGER.info("Jar file : {}", jar);

        return jar;
    }

}
