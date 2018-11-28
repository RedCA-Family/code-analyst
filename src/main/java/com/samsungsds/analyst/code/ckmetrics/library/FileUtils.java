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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	public static String[] getAllDirs(String path) {
		List<String> dirs = new ArrayList<>();
		getAllDirs(path, dirs);
		
		return dirs.toArray(new String[0]);
	}
	
	private static void getAllDirs(String path, List<String> dirs) {
		
		File f = new File(path);
		if (f.getName().equals(".git")) return;

		for (File inside : f.listFiles()) {
			if (inside.isDirectory()) {
				String newDir = inside.getAbsolutePath();
				dirs.add(newDir);
				getAllDirs(newDir, dirs);
			}
		}
	}

	public static String[] getAllJavaFiles(String path) {
		List<String> files = new ArrayList<>();
		getAllJavaFiles(path, files);

		return files.toArray(new String[0]);
	}
	
	private static void getAllJavaFiles(String path, List<String> files) {
		
		File f = new File(path);
		if (f.getName().equals(".git")) return;
		
		for (File inside : f.listFiles()) {
			if (inside.isDirectory()) {
				String newDir = inside.getAbsolutePath();
				getAllJavaFiles(newDir, files);
			} else if (inside.getAbsolutePath().toLowerCase().endsWith(".java")) {
				files.add(inside.getAbsolutePath());
			}
		}
	}
}
