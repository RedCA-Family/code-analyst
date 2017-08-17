package com.samsungsds.analyst.code.main.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.AntPathMatcher;

public class FilePathExcludeFilter extends FilePathAbstractFilter {
	private static final Logger LOGGER = LogManager.getLogger(FilePathExcludeFilter.class);
	
	public FilePathExcludeFilter(String filterString) {
		super(filterString);
	}
	
	@Override
	public String getFilterName() {
		return "Exclude";
	}

	@Override
	public boolean matched(String filePath, boolean withoutFilename) {
		if (withoutFilename) {
			LOGGER.debug("Exclude filter not supported without filename check mode...");
		}
		AntPathMatcher matcher = new AntPathMatcher();
		
		for (String filter : getFilters()) {
			if (matcher.match(filter, filePath)) {
				return false;
			}
		}
		
		return true;
	}
}
