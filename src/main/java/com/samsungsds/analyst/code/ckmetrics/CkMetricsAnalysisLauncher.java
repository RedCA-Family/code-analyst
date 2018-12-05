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
import com.samsungsds.analyst.code.util.FindFileUtils;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class CkMetricsAnalysisLauncher implements CkMetricsAnalysis {
    private static final Logger LOGGER = LogManager.getLogger(CkMetricsAnalysisLauncher.class);

    private String projectBaseDir;
    private String binaryDirectories;

    @Override
    public void setProjectBaseDir(String directory) {
        this.projectBaseDir = directory;
    }

    @Override
    public void setBinaryDirectories(String directories) {
        this.binaryDirectories = directories;
    }

    @Override
    public void run(String instanceKey) {
        MeasuredResult result = MeasuredResult.getInstance(instanceKey);

        List<String> fileListInPath = getFileListInPath(result);

        LOGGER.info("CK Metrics Target Files : {}", fileListInPath.size());

        File jar = getJarFileFrombinaryDirectories();

        MetricsFilter.setRepositoryClassPath(jar.toString());

        ClassMetricsContainer cm = new ClassMetricsContainer();
        for (String targetFile : fileListInPath) {
            targetFile = IOAndFileUtils.getPrefixRemovedPath(targetFile, projectBaseDir);
            for (String target : binaryDirectories.split(FindFileUtils.COMMA_SPLITTER)) {
                targetFile = IOAndFileUtils.getPrefixRemovedPath(targetFile, target);
            }

            MetricsFilter.processClass(cm, jar.toString() + " " + targetFile.replace("\\", "/"));
        }

        CkMetricsResultHandler handler = new CkMetricsResultHandler();
        cm.printMetrics(handler);
        List<CkMetricsResult> ckMetricResults = handler.getCkMetricsResults();

        MeasuredResult.getInstance(instanceKey).setCkMetricsResultList(ckMetricResults);

        LOGGER.info("CK Metrics Total Result : {}", MeasuredResult.getInstance(instanceKey).getCkMetricsResultList().size());

    }

    private File getJarFileFrombinaryDirectories() {
        StringBuffer classpaths = new StringBuffer();
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

    private List<String> getFileListInPath(MeasuredResult result) {
        List<String> fileList = new ArrayList<>();

        for (String target : binaryDirectories.split(FindFileUtils.COMMA_SPLITTER)) {
            ProcessFile fileProcessor = new ProcessFile(target, result);
            try {
                Files.walkFileTree(Paths.get(projectBaseDir + File.separator + target), fileProcessor);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fileList.addAll(fileProcessor.getFileList());
        }

        return fileList;
    }

    private final class ProcessFile extends SimpleFileVisitor<Path> {
        final String target;
        final MeasuredResult result;
        final List<String> fileList = new ArrayList<>();

        ProcessFile(String target, MeasuredResult result) {
            this.target = target;
            this.result = result;
        }

        List<String> getFileList() {
            return fileList;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (file.toString().endsWith(".class") && !file.getFileName().toString().contains("$")) {
                String path = IOAndFileUtils.getPrefixRemovedPath(file.toString(), projectBaseDir);
                path = IOAndFileUtils.getPrefixRemovedPath(path, projectBaseDir);
                path = IOAndFileUtils.getPrefixRemovedPath(path, target);

                if (!result.haveToSkip(path.replace("\\", "/").replace(".class", ".java"), true)) {
                    fileList.add(file.toString());
                }
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
