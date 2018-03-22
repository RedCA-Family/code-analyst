package com.samsungsds.analyst.code.main.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.util.FindFileUtils;

public abstract class FilePathAbstractFilter implements FilePathFilter {
	private static final Logger LOGGER = LogManager.getLogger(FilePathAbstractFilter.class);

	protected static final String PRINT_PATH_FILTER_PROPERTY_KEY = "print.path.filter";

	public static final String FIXED_PREFIX = "fixed:";
	
	private String[] filters;

	private String srcPrefix;

	public FilePathAbstractFilter(String filterString, String srcPrefix) {
	    this.srcPrefix = srcPrefix;

		setNormalizedFilterString(filterString);
		
		debuggingFilters();
	}
	
	public void setNormalizedFilterString(String filterString) {
		String[] splittedFilters = filterString.split(FindFileUtils.COMMA_SPLITTER);
		
		this.filters = new String[splittedFilters.length];
		
		for (int i = 0; i < splittedFilters.length; i++) {
			String ret = splittedFilters[i].replaceAll("\\\\", "/");

			if (ret.startsWith(FIXED_PREFIX)) {
				this.filters[i] = srcPrefix + "/" + ret.substring(FIXED_PREFIX.length());
				continue;
			}

			if (!ret.startsWith("**/")) {
				if (ret.startsWith("/")) {
					ret = "**" + ret;
				} else {
					ret = "**/" + ret;
				}
			}
			
			this.filters[i] = ret;
		}
	}
	
	private void debuggingFilters() {
		if (LOGGER.isDebugEnabled()) {
			for (String filter : this.filters) {
				LOGGER.debug("- File filter ({}) : {}", getFilterName(), filter);
			}
		}
	}

	protected String[] getFilters() {
		return filters;
	}
	
	public String getNormalizedFilterString() {
		StringBuilder ret = new StringBuilder();
		
		for (String filter : filters) {
			if (ret.length() != 0) {
				ret.append(",");
			}
			
			ret.append(filter);
		}
		
		return ret.toString();
	}
	
	@Override
	public abstract String getFilterName();

	@Override
	public abstract boolean matched(String filePath, boolean withoutFilename);
	
	@Override
	public boolean matched(String filePath) {
		return matched(filePath, false);
	}

	@Override
    public String getSrcPrefix() {
	    return srcPrefix;
    }
}
