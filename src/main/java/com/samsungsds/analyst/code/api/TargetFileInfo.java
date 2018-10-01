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

public class TargetFileInfo implements AbstractFileInfo {
	private boolean includeSubPackage = true;
	private List<String> fileList = new ArrayList<>();

	public boolean isIncludeSubPackage() {
		return includeSubPackage;
	}

	public void setIncludeSubPackage(boolean includeSubPackage) {
		this.includeSubPackage = includeSubPackage;
	}

	public void addPackage(String packageName) {
        if (!packageName.equals("")) {
            packageName += "/";
        }

		if (includeSubPackage) {
			fileList.add(FilePathAbstractFilter.FIXED_PREFIX + packageName + "**/*.java");
		} else {
			fileList.add(FilePathAbstractFilter.FIXED_PREFIX + packageName + "*.java");
		}
	}

	/**
	 * @deprecated  This method has to be replaced by {@link #addFileExactly(String, String)}
	 */
	@Deprecated
	public void addFile(String packageName, String file) {
        if (!packageName.equals("")) {
            packageName += "/";
        }
		fileList.add(FilePathAbstractFilter.FIXED_PREFIX + "**/" + packageName + file);
	}

	public void addFileExactly(String packageName, String file) {
		if (!packageName.equals("")) {
			packageName += "/";
		}
		fileList.add(FilePathAbstractFilter.FIXED_PREFIX + packageName + file);
	}

	@Override
	public String[] getFiles() {
		return fileList.toArray(new String[0]);
	}

	@Override
	public boolean isPackageExpression() {
		return true;
	}
}