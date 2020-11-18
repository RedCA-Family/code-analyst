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
package com.samsungsds.analyst.code.main.subject;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.samsungsds.analyst.code.main.FileSkipChecker;
import com.samsungsds.analyst.code.util.FindFileUtils;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class TargetManager {
    private static final Logger LOGGER = LogManager.getLogger(TargetManager.class);

    private static Map<String, TargetManager> instances = new HashMap<>();

    private String parameters = null;
    private String projectBaseDir = null;
    private String[] sourceDirectories = null;
    private String[] binaryDirectories = null;
    private FileSkipChecker skipChecker = null;

    private List<TargetFile> targetFileList = new ArrayList<>();

    private boolean directoriesChanged = false;
    private AtomicBoolean initialized = new AtomicBoolean();

    public static TargetManager getInstance(String instanceKey) {
        if (!instances.containsKey(instanceKey)) {
            synchronized (TargetManager.class) {
                if (!instances.containsKey(instanceKey)) {
                    instances.put(instanceKey, new TargetManager());
                }
            }
        }

        return instances.get(instanceKey);
    }

    private void initializeAndGetTargetFileList() {
        LOGGER.info("Target Manager initialization start...");

        targetFileList.clear();

        int noBinaryFileCount = 0;

        for (String target : sourceDirectories) {
            target = IOAndFileUtils.getNormalizedPath(target);

            ProcessFile fileProcessor = new ProcessFile(target, skipChecker, "java");
            try {
                Files.walkFileTree(Paths.get(projectBaseDir + File.separator + target), fileProcessor);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (String source : fileProcessor.getFileList()) {
                TargetFile targetFile = new TargetFile();

                targetFile.setProjectBaseDir(projectBaseDir);
                targetFile.setSourceDirectory(target);
                targetFile.setSourceFilePath(source.replace(projectBaseDir + File.separator + target + File.separator, ""));

                for (String binary : binaryDirectories) {
                    binary = IOAndFileUtils.getNormalizedPath(binary);

                    File classPath = new File(projectBaseDir + File.separator + binary, targetFile.getSourceFilePath().replace(".java", ".class"));

                    if (classPath.exists()) {
                        targetFile.setBinaryFileExists(true);
                        targetFile.setBinaryDirectory(binary);
                        targetFile.setBinaryFilePath(classPath.toString().replace(projectBaseDir + File.separator + binary + File.separator, ""));

                        break;
                    }
                }

                if (!targetFile.isBinaryFileExists()) {
                    noBinaryFileCount++;
                }

                targetFileList.add(targetFile);
            }
        }

        if (noBinaryFileCount > 0) {
            LOGGER.warn("{} file(s) has no binary file", noBinaryFileCount);
        }
    }

    public List<TargetFile> getTargetFileList(String projectBaseDir, String sourceDirectories, String binaryDirectories, FileSkipChecker skipChecker) {
        if (initialized.get()) {
            return targetFileList;
        }

        projectBaseDir = IOAndFileUtils.getNormalizedPath(projectBaseDir);

        String parameters = projectBaseDir + ":" + sourceDirectories + ":" + binaryDirectories;

        if (parameters.equals(this.parameters)) {
            return targetFileList;
        } else {
            this.parameters = parameters;
            this.projectBaseDir = projectBaseDir;
            this.sourceDirectories = sourceDirectories.split(FindFileUtils.COMMA_SPLITTER);
            this.binaryDirectories = binaryDirectories.split(FindFileUtils.COMMA_SPLITTER);
            this.skipChecker = skipChecker;

            checkAndModifyDirectories();

            initializeAndGetTargetFileList();

            initialized.set(true);

            return targetFileList;
        }
    }

    private void checkAndModifyDirectories() {
        File project = new File(projectBaseDir);

        projectBaseDir = project.getAbsolutePath();

        changeSourceDirectories();
        changeBinaryDirectories();
    }

    private void changeSourceDirectories() {
        for (int i = 0; i < sourceDirectories.length; i++) {
            String src = IOAndFileUtils.getNormalizedPath(sourceDirectories[i]);

            FirstFileFinder find = new FirstFileFinder(projectBaseDir + File.separator + src, "java");

            String path = find.getPath();

            JavaParser javaParser = new JavaParser();
            ParseResult<CompilationUnit> result = null;
            try {
                result = javaParser.parse(new FileInputStream(path));
                String packageName = "";
                if (result.getResult().get().getPackageDeclaration().isPresent()) {
                    packageName = result.getResult().get().getPackageDeclaration().get().getNameAsString();
                }

                path = path.substring(0, path.lastIndexOf(File.separatorChar)); // 파일명 부분 삭제
                path = path.replace(IOAndFileUtils.getNormalizedPath(projectBaseDir), "");  // 앞 프로젝트 부분 삭제
                if (path.startsWith(File.separator)) {
                    path = path.substring(1);
                }

                String packageDir = packageName.replace(".", File.separator);

                if (path.endsWith(packageDir)) {
                    path = path.substring(0, path.length() - packageDir.length());
                } else {
                    throw new IllegalStateException("src option error : " + src);
                }


                if (path.endsWith(File.separator)) {
                    path = path.substring(0, path.length() - 1);
                }

                if (!src.equals(path)) {
                    LOGGER.info("Source Directory changed : {} -> {}", src, path);
                    sourceDirectories[i] = path;
                    directoriesChanged = true;
                }

            } catch (FileNotFoundException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    private void changeBinaryDirectories() {
        for (int i = 0; i < binaryDirectories.length; i++) {
            String bin = IOAndFileUtils.getNormalizedPath(binaryDirectories[i]);

            FirstFileFinder find = new FirstFileFinder(projectBaseDir + File.separator + bin, "class");

            String path = find.getPath();

            ClassParser parser = new ClassParser(path);

            try {
                JavaClass javaClass = parser.parse();
                String packageName = javaClass.getPackageName();

                path = path.substring(0, path.lastIndexOf(File.separatorChar));
                path = path.replace(IOAndFileUtils.getNormalizedPath(projectBaseDir), "");
                if (path.startsWith(File.separator)) {
                    path = path.substring(1);
                }
                String packageDir = packageName.replace(".", File.separator);

                if (path.endsWith(packageDir)) {
                    path = path.substring(0, path.length() - packageDir.length());
                } else {
                    throw new IllegalStateException("binary option error : " + bin);
                }

                if (path.endsWith(File.separator)) {
                    path = path.substring(0, path.length() - 1);
                }

                if (!bin.equals(path)) {
                    LOGGER.info("Binary Directory changed : {} -> {}", bin, path);
                    binaryDirectories[i] = path;
                    directoriesChanged = true;
                }
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    public boolean isDirectoriesChanged() {
        return directoriesChanged;
    }

    public String getSourceOption() {
        return String.join(",", sourceDirectories);
    }

    public String getBinaryOption() {
        return String.join(",", binaryDirectories);
    }

    private final class ProcessFile extends SimpleFileVisitor<Path> {
        final String target;
        final FileSkipChecker skipChecker;
        final List<String> fileList = new ArrayList<>();
        final String ext;

        ProcessFile(String target, FileSkipChecker skipChecker, String ext) {
            this.target = target;
            this.skipChecker = skipChecker;
            this.ext = ext;
        }

        List<String> getFileList() {
            return fileList;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (file.toString().endsWith(ext) && !file.getFileName().toString().contains("$")) {
                String path = IOAndFileUtils.getPrefixRemovedPath(file.toString(), projectBaseDir);
                path = IOAndFileUtils.getPrefixRemovedPath(path, projectBaseDir);
                path = IOAndFileUtils.getPrefixRemovedPath(path, target);

                if (!skipChecker.haveToSkip(path.replace("\\", "/"), true)) {
                    fileList.add(file.toString());
                }
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
