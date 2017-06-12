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
		
		if (files.length == 0) {
			return;
		}

		int directoryCount = getNumberOfDirectories(files);
		
		if (directoryCount == 0) {
			list.add(subPackage.replaceAll("\\\\", "/").replaceAll("/", "."));
		} else if (directoryCount == files.length) {
			for (File dir : files) {
				addProjectPackages(list, baseDir, subPackage.equals("") ? dir.getName() : subPackage + File.separator + dir.getName());
			}
		} else {
			list.add(subPackage.replaceAll("\\\\", "/").replaceAll("/", "."));
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
