package com.samsungsds.analyst.code.main.filter;

import org.springframework.util.AntPathMatcher;

public class FilePathIncludeFilter extends FilePathAbstractFilter {
	private boolean printPathFilter = false;
	
	public FilePathIncludeFilter(String filterString, String srcPrefix) {
		super(filterString, srcPrefix);

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
