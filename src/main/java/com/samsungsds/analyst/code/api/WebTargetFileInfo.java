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
package com.samsungsds.analyst.code.api;

import com.samsungsds.analyst.code.main.filter.FilePathAbstractFilter;

import java.util.ArrayList;
import java.util.List;

public class WebTargetFileInfo implements AbstractFileInfo {
    private boolean includeSubDirectory = true;
    private List<String> fileList = new ArrayList<>();

    public boolean isIncludeSubDirectory() {
        return includeSubDirectory;
    }

    public void setIncludeSubDirectory(boolean includeSubDirectory) {
        this.includeSubDirectory = includeSubDirectory;
    }

    public void addDirectory(String directory) {
        if (!directory.equals("")) {
            directory += "/";
        }

        if (includeSubDirectory) {
            fileList.add(FilePathAbstractFilter.FIXED_PREFIX + directory + "**/*.*");
        } else {
            fileList.add(FilePathAbstractFilter.FIXED_PREFIX + directory + "*.*");
        }
    }

    public void addFile(String filePath) {
        fileList.add(FilePathAbstractFilter.FIXED_PREFIX + filePath);
    }

    @Override
    public String[] getFiles() {
        return fileList.toArray(new String[0]);
    }

    @Override
    public boolean isPackageExpression() {
        return false;
    }

    @Override
    public boolean isWebResourceAnalysis() {
        return true;
    }
}
