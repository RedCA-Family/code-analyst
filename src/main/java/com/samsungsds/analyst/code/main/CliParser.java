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
package com.samsungsds.analyst.code.main;

import com.samsungsds.analyst.code.api.Language;
import com.samsungsds.analyst.code.main.cli.CliParseProcessor;
import com.samsungsds.analyst.code.main.cli.CliParseProcessorFactory;
import com.samsungsds.analyst.code.main.cli.CliParsedValueObject;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.main.result.OutputFileFormat;

public class CliParser {
	private static final Logger LOGGER = LogManager.getLogger(CliParser.class);

	private final CliParseProcessor parseProcessor;

	private final String[] args;
	private final Options options = new Options();

	private final Language languageType;

	private final CliParsedValueObject parsedValue = new CliParsedValueObject();

	/**
	 * @deprecated  This constructor has to be replaced by {@link #CliParser(String[], Language)}
	 */
	@Deprecated
	public CliParser(String[] args) {
		this(args, Language.JAVA);
	}

	public CliParser(String[] args, Language language) {
		this.args = args;
		this.languageType = language;

		parseProcessor = CliParseProcessorFactory.getCliParseProcessor(language);

		parsedValue.setSrc(parseProcessor.getDefaultSrcOption());
		parsedValue.setBinary(parseProcessor.getDefaultBinaryOption());

		parseProcessor.setOptions(this, options);
	}

	public boolean parse() {
		return parseProcessor.parseAndSaveParsedValue(this, options, args, parsedValue);
	}

	public void setInstanceKey(String instanceKey) {
		parsedValue.setInstanceKey(instanceKey);
	}

	public Language getLanguageType() {
		return languageType;
	}

	public String getLanguage() {
		return parsedValue.getLanguage();
	}

	public String getSrc() {
		return parsedValue.getSrc();
	}

	public String getBinary() {
		return parsedValue.getBinary();
	}

	public boolean isDebug() {
		return parsedValue.isDebug();
	}

	public String getEncoding() {
		return parsedValue.getEncoding();
	}

	public String getProjectBaseDir() {
		return parsedValue.getProjectBaseDir();
	}

	public String getLibrary() {
		return parsedValue.getLibrary();
	}

	public String getJavaVersion() {
		return parsedValue.getJavaVersion();
	}

	public String getJavaVersionWithoutDot() {
	    if (parsedValue.getJavaVersion().contains(".")) {
	        return parsedValue.getJavaVersion().replace("1.", "");
        } else {
	        return parsedValue.getJavaVersion();
        }
    }

    public String getJavaVersionWithDot() {
        if (parsedValue.getJavaVersion().contains(".")) {
            return parsedValue.getJavaVersion();
        } else {
            return "1." + parsedValue.getJavaVersion();
        }
    }

	public String getRuleSetFileForPMD() {
		return parsedValue.getRuleSetFileForPMD();
	}

	public String getRuleSetFileForFindBugs() {
		return parsedValue.getRuleSetFileForFindBugs();
	}

	public String getRuleSetFileForSonar() {
		return parsedValue.getRuleSetFileForSonar();
	}

	public String getRuleSetFileForCheckStyle() {
	    return parsedValue.getRuleSetFileForCheckStyle();
    }

	public String getOutput() {
		return parsedValue.getOutput();
	}

	public String getTimeout() {
		return parsedValue.getTimeout();
	}

	public MeasurementMode getMode() {
		return parsedValue.getMode();
	}

	public String getClassForCCMeasurement() {
		return parsedValue.getClassForCCMeasurement();
	}

	public OutputFileFormat getFormat() {
		return parsedValue.getFormat();
	}

	public String getIncludes() {
		return parsedValue.getIncludes();
	}

	public String getExcludes() {
		return parsedValue.getExcludes();
	}

	public IndividualMode getIndividualMode() {
		return parsedValue.getIndividualMode();
	}

	public String getAnalysisMode() {
		return parsedValue.getAnalysisMode();
	}

    public String getWebapp() {
        return parsedValue.getWebapp();
    }

    public String getErrorMessage() {
		return parsedValue.getErrorMessage();
	}

	public String getInstanceKey() {
		if ("".equals(parsedValue.getInstanceKey())) {
			LOGGER.warn("No instance key!!!");
		}

		return parsedValue.getInstanceKey();
	}

	public boolean isDetailAnalysis() {
		return parsedValue.isDetailAnalysis();
	}

	public boolean isSeperatedOutput() {
		return parsedValue.isSeperatedOutput();
	}

	public boolean isSaveCatalog() {
		return parsedValue.isSaveCatalog();
	}

	public boolean isTokenBased() {
		return parsedValue.isTokenBased();
	}

	public int getMinimumTokens() {
		return parsedValue.getMinimumTokens();
	}

    public void setSrc(String sourceOption) {
	    parsedValue.setSrc(sourceOption);
    }

    public void setBinary(String binaryOption) {
	    parsedValue.setBinary(binaryOption);
    }
}
