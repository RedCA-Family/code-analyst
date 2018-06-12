package com.samsungsds.analyst.code.main.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.AntPathMatcher;

public class FilePathExcludeFilter extends FilePathAbstractFilter {
	private static final Logger LOGGER = LogManager.getLogger(FilePathExcludeFilter.class);

	private boolean printPathFilter = false;
	
	public FilePathExcludeFilter(String filterString) {
		super(filterString, "", "");

		if (System.getProperty(PRINT_PATH_FILTER_PROPERTY_KEY, "false").equalsIgnoreCase("true")) {
			printPathFilter = true;
		}
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
				if (printPathFilter) {
					System.out.println("[FilePathFilter] <" + filter + ", " + filePath + "> : exclude");
				}
				return false;
			}
		}

		if (printPathFilter) {
			System.out.println("[FilePathFilter] <" + filePath + "> : not exclude");
		}

		return true;
	}
}
