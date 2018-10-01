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
package com.samsungsds.analyst.code.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PackageUtils {	
	public static List<String> getProjectPackages(String rootBinaryDir) {
		List<String> list = new ArrayList<>();
		
		addProjectPackages(list, rootBinaryDir, "");
		
		return list;
	}

	private static void addProjectPackages(List<String> list, String baseDir, String subPackage) {
		File currentDir = new File(baseDir + File.separator + subPackage);
		
		if (!currentDir.exists()) {
			return;
		}
		
		File[] files = currentDir.listFiles((File path) -> {
			return path.isDirectory() || path.getName().endsWith(".class");
		});
		
		if (files == null || files.length == 0) {
			return;
		}

		int directoryCount = getNumberOfDirectories(files);
		
		if (directoryCount == 0) {	// has java files..
			list.add(subPackage.replaceAll("\\\\", "/").replaceAll("/", "."));
		} else if (directoryCount == files.length) {	// has only directories
			for (File dir : files) {
				addProjectPackages(list, baseDir, subPackage.equals("") ? dir.getName() : subPackage + File.separator + dir.getName());
			}
		} else {	// has java files and directories
			list.add(subPackage.replaceAll("\\\\", "/").replaceAll("/", "."));
			for (File dir : files) {
				addProjectPackages(list, baseDir, subPackage.equals("") ? dir.getName() : subPackage + File.separator + dir.getName());
			}
		}
	}

	private static int getNumberOfDirectories(File[] files) {
		int count = 0;
		
		for (File file : files) {
			if (file.isDirectory()) {
				count++;
			}
		}
		
		return count;
	}
}
