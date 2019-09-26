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
package com.samsungsds.analyst.code.main.cli;

import com.samsungsds.analyst.code.main.CliParser;
import org.apache.commons.cli.Options;

/**
 * Command line option processing logic by target language
 */
public interface CliParseProcessor {
    /**
     * Default source directory by language
     * @return default source directory
     */
    String getDefaultSrcOption();

    /**
     * Default binary directory by language
     * @return default binary directory
     */
    String getDefaultBinaryOption();

    /**
     * Set declared Options object
     * @param cliParser CLiParser
     * @param options Options
     */
    void setOptions(CliParser cliParser, Options options);

    /**
     * Set individual model by language
     * @param parsedValue CliParsedValueObject
     */
    void setDefaultIndividualModeAfterParsing(CliParsedValueObject parsedValue);

    /**
     * Parse arguments and save parsed values to CliParsedValueObject
     * @param cliParser CliParser
     * @param options Options
     * @param args Program Arguments
     * @param parsedValue Parsed Value Object to be saved
     * @return whether parsing success
     */
    boolean parseAndSaveParsedValue(CliParser cliParser, Options options, String[] args, CliParsedValueObject parsedValue);

    /**
     * Get error message when mode has wrong value
     */
    String getModeErrorMessage();
}
