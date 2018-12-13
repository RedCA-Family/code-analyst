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

import java.io.File;

public class TargetFile {
    public final static String PATH_SEPARATOR = System.getProperty("path.separator");
    public final static String PATH_SEPARATOR_FOR_DISPLAY = "/";

    private String projectBaseDir;
    private String sourceDirectory;
    private String sourceFilePath;
    private String binaryDirectory;
    private String binaryFilePath;

    private boolean binaryFileExists;

    public String getProjectBaseDir() {
        return projectBaseDir;
    }

    public void setProjectBaseDir(String projectBaseDir) {
        this.projectBaseDir = projectBaseDir;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public String getBinaryDirectory() {
        return binaryDirectory;
    }

    public void setBinaryDirectory(String binaryDirectory) {
        this.binaryDirectory = binaryDirectory;
    }

    public String getBinaryFilePath() {
        return binaryFilePath;
    }

    public void setBinaryFilePath(String binaryFilePath) {
        this.binaryFilePath = binaryFilePath;
    }

    public void setBinaryFileExists(boolean binaryFileExists) {
        this.binaryFileExists = binaryFileExists;
    }

    public boolean isBinaryFileExists() {
        return binaryFileExists;
    }

    public File getSourceFile() {
        StringBuilder builder = new StringBuilder();

        builder.append(projectBaseDir).append(PATH_SEPARATOR);
        builder.append(sourceDirectory).append(PATH_SEPARATOR);
        builder.append(sourceFilePath);

        return new File(builder.toString());
    }

    public File getBinaryFile() {
        StringBuilder builder = new StringBuilder();

        builder.append(projectBaseDir).append(PATH_SEPARATOR);
        builder.append(binaryDirectory).append(PATH_SEPARATOR);
        builder.append(binaryFilePath);

        return new File(builder.toString());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("TargetFile [");
        builder.append(projectBaseDir).append(PATH_SEPARATOR_FOR_DISPLAY);
        builder.append(sourceDirectory).append(PATH_SEPARATOR_FOR_DISPLAY);
        builder.append(sourceFilePath).append(", ");
        if (binaryFileExists) {
            builder.append(projectBaseDir).append(PATH_SEPARATOR_FOR_DISPLAY);
            builder.append(binaryDirectory).append(PATH_SEPARATOR_FOR_DISPLAY);
            builder.append(binaryFilePath);
        } else {
            builder.append("<No Binary File>");
        }
        builder.append("]");

        return builder.toString().replace("\\", PATH_SEPARATOR_FOR_DISPLAY);
    }
}
