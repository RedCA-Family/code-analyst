package com.samsungsds.analyst.code.main.filter;

import org.springframework.util.AntPathMatcher;

public class FilePathIncludeFilter extends FilePathAbstractFilter {
	
	public FilePathIncludeFilter(String filterString) {
		super(filterString);
	}
	
	@Override
	public String getFilterName() {
		return "Include";
	}
	
	@Override
	public boolean matched(String filePath) {
		AntPathMatcher matcher = new AntPathMatcher();
		
		for (String filter : getFilters()) {
			if (matcher.match(filter, filePath)) {
				return true;
			}
		}
		
		return false;
	}
}
