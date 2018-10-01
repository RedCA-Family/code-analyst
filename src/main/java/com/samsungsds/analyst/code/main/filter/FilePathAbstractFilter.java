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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.util.FindFileUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class FilePathAbstractFilter implements FilePathFilter {
	private static final Logger LOGGER = LogManager.getLogger(FilePathAbstractFilter.class);

	protected static final String PRINT_PATH_FILTER_PROPERTY_KEY = "print.path.filter";

	public static final String FIXED_PREFIX = "fixed:";
	
	private String[] filters;

	private String srcPrefix;
	private String webapp;

	public FilePathAbstractFilter(String filterString, String srcPrefix, String webapp) {
	    this.srcPrefix = srcPrefix;
		this.webapp = webapp;

		setNormalizedFilterString(filterString);
		
		debuggingFilters();
	}
	
	public void setNormalizedFilterString(String filterString) {
		String[] splitFilters = filterString.split(FindFileUtils.COMMA_SPLITTER);

		List<String> list = new ArrayList<>();

		for (int i = 0; i < splitFilters.length; i++) {
			String ret = splitFilters[i].replaceAll("\\\\", "/");

			if (ret.startsWith(FIXED_PREFIX)) {

				if (!srcPrefix.equals("")) {
					String[] srcDirectories = srcPrefix.split(FindFileUtils.COMMA_SPLITTER);

					for (String src : srcDirectories) {
						list.add(src + "/" + ret.substring(FIXED_PREFIX.length()));
					}
				} else if (!webapp.equals("")) {
					list.add(webapp + "/" + ret.substring(FIXED_PREFIX.length()));
				}

				continue;
			}

			if (!ret.startsWith("**/")) {
				if (ret.startsWith("/")) {
					ret = "**" + ret;
				} else {
					ret = "**/" + ret;
				}
			}

			list.add(ret);
		}

		this.filters = list.toArray(new String[0]);
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
