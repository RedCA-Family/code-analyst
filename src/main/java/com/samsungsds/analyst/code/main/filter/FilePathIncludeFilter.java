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
package com.samsungsds.analyst.code.main.filter;

import org.springframework.util.AntPathMatcher;

public class FilePathIncludeFilter extends FilePathAbstractFilter {
	private boolean printPathFilter = false;
	
	public FilePathIncludeFilter(String filterString, String srcPrefix, String webapp) {
		super(filterString, srcPrefix, webapp);

		if (System.getProperty(PRINT_PATH_FILTER_PROPERTY_KEY, "false").equalsIgnoreCase("true")) {
			printPathFilter = true;
		}
	}
	
	@Override
	public String getFilterName() {
		return "Include";
	}
	
	@Override
	public boolean matched(String filePath, boolean withoutFilename) {
		AntPathMatcher matcher = new AntPathMatcher();
		
		for (String filter : getFilters()) {
			if (printPathFilter) {
				System.out.println("[FilePathFilter] <<" + filter + ">>");
			}
			if (withoutFilename) {
				filter = filter.substring(0, filter.lastIndexOf("/")) + "/*.java";
			}

			if (matcher.match(filter, filePath)) {
				if (printPathFilter) {
					System.out.println("[FilePathFilter] <" + filter + ", " + filePath + "> : include");
				}
				return true;
			}
		}

		if (printPathFilter) {
			System.out.println("[FilePathFilter] <" + filePath + "> : not include");
		}

		return false;
	}
}
