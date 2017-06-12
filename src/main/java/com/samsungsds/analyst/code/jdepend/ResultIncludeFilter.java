package com.samsungsds.analyst.code.jdepend;

import java.util.ArrayList;
import java.util.List;

public class ResultIncludeFilter {
	private final List<String> includePackageList = new ArrayList<>();

	public boolean include(String packageName) {
		for (String nameToInclude : includePackageList) {
            if (packageName.startsWith(nameToInclude)) {
                return true;
            }
		}
		return false;
	}
	
	public void addIncludePackage(String packageName) {
		if (!includePackageList.contains(packageName)) {
			includePackageList.add(packageName);
		}
	}
}
