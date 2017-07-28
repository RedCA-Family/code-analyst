package com.samsungsds.analyst.code.main.filter;

import org.springframework.util.AntPathMatcher;

public class FilePathExcludeFilter extends FilePathAbstractFilter {
	
	public FilePathExcludeFilter(String filterString) {
		super(filterString);
	}
	
	@Override
	public String getFilterName() {
		return "Exclude";
	}

	@Override
	public boolean matched(String filePath) {
		AntPathMatcher matcher = new AntPathMatcher();
		
		for (String filter : getFilters()) {
			if (matcher.match(filter, filePath)) {
				return false;
			}
		}
		
		return true;
	}
}
