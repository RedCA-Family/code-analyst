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

import com.samsungsds.analyst.code.api.Language;
import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.main.MeasurementMode;
import com.samsungsds.analyst.code.main.Version;
import com.samsungsds.analyst.code.main.result.OutputFileFormat;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class CliParseProcessorForJava extends AbstractCliParseProcessor {
    private static final Logger LOGGER = LogManager.getLogger(CliParseProcessorForJava.class);

    @Override
    public String getDefaultSrcOption() {
        return "src" + File.separator  + "main" + File.separator  + "java";
    }

    @Override
    public String getDefaultBinaryOption() {
        return "target" + File.separator + "classes";
    }

    @Override
    public void setOptions(CliParser cliParser, Options options) {
        options.addOption("l", "language", true, "specify the language to analyze. ('Java', 'JavaScript', 'C#' or 'Python', default : \"Java\")");

        options.addOption("h", "help", false, "show help.");
        options.addOption("p", "project", true, "specify project base directory. (default: \".\")");
        options.addOption("s", "src", true, "specify source directories with comma separated. (default: \"${project}" + File.separator + getDefaultSrcOption() + "\")");
        options.addOption("b", "binary", true, "specify binary directories with comma separated. (default: \"${project}" + File.separator + getDefaultBinaryOption() + "\")");
        options.addOption("library", true, "specify library directory, jar files contained.");
        options.addOption("d", "debug", false, "debug mode.");
        options.addOption("e", "encoding", true, "encoding of the source code. (default: UTF-8)");
        options.addOption("j", "java", true, "specify java version. (default: 1.8)");

        options.addOption("pmd", true, "specify PMD ruleset xml file.");
        options.addOption("findbugs", true, "specify FindBugs ruleset(include filter) xml file.");
        options.addOption("sonar", true, "specify SonarQube issue ruleset(exclude filter) xml file." +
                "\nex:" +
                "\n<SonarIssueFilter>" +
                "\n    <Exclude key=\"common-java:DuplicatedBlocks\"/>" +
                "\n</SonarIssueFilter>");
        options.addOption("checkstyle", true, "specify CheckStyle configuration xml file.");

        options.addOption("o", "output", true, "specify result output file. (default : \"result-[yyyyMMddHHmmss].[out|json]\")");
        options.addOption("f", "format", true, "specify result output file format(json, text, none). (default : text)");
        options.addOption("v", "version", false, "display version info.");
        options.addOption("t", "timeout", true, "specify internal ws timeout. (default : 100 min.)");

        options.addOption("c", "complexity", true, "specify class name(glob pattern) to be measured. (Cyclomatic Complexity Measurement mode)");

        options.addOption("w", "webapp", true, "specify webapp root directory to be inspected. " +
                "If it's not specified, 'javascript', 'css', 'html' analysis items will be disabled." +
                "\n※ webapp directory should not overlap the src directories.");

        options.addOption("include", true, "specify include pattern(Ant-style) with comma separated. (e.g.: com/sds/**/*.java)");
        options.addOption("exclude", true, "specify exclude pattern(Ant-style) with comma separated. (e.g.: com/sds/**/*VO.java)" +
                "\n※ If 'include' or 'exclude' option starts with '@' and has file name, the option value is read from the file");

        options.addOption("m", "mode", true, "specify analysis items with comma separated. If '-' specified in each mode, the mode is excluded. " +
                "(code-size, duplication, complexity, sonarjava, pmd, findbugs, findsecbugs, javascript, css, html, dependency, unusedcode, ckmetrics, checkstyle)" +
                "\n※ 'javascript', 'css' and 'html' will be disabled when 'webapp' option isn't set, and 'css' and 'html' are disabled by default");

        options.addOption("a", "analysis", false, "detailed analysis mode. (required more memory. If OOM exception occurred, use JVM '-Xmx' option like '-Xmx1024m')");

        options.addOption("r", "rerun", true, "specify previous output file to rerun with same options. "
                + "('project', 'src', 'binary', 'encoding', 'java', 'pmd', 'findbugs', 'sonar', 'include', 'exclude', 'mode', 'analysis', 'seperated', 'catalog', 'duplication', 'token' and 'webapp')");

        options.addOption("seperated", false, "specify seperated output mode.");

        options.addOption("catalog", false, "specify file catalog saving mode.");

        options.addOption("duplication", true,"specify duplication detection mode. ('statement' or 'token', default : statement)");
        options.addOption("tokens", true, "specify the minimum number of tokens when token-based duplication detection mode. (default : 100)");
    }

    @Override
    public void setDefaultIndividualModeAfterParsing(CliParsedValueObject parsedValue) {
        parsedValue.getIndividualMode().setLanguageType(Language.JAVA);

        // exclude SonarCSharp & SonarPython
        parsedValue.getIndividualMode().setSonarCSharp(false);
        parsedValue.getIndividualMode().setSonarPython(false);
    }

    @Override
    public boolean parseAndSaveParsedValue(CliParser cliParser, Options options, String[] args, CliParsedValueObject parsedValue) {
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd;

        preprocessArgs(args);

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("l")) {
                parsedValue.setLanguage(cmd.getOptionValue("l"));
            }

            if (cmd.hasOption("h")) {
                help(options, cmd);
                return false;
            }

            if (cmd.hasOption("v")) {
                Version.printVersionInfo();
                return false;
            }

            if (cmd.hasOption("p")) {
                parsedValue.setProjectBaseDir(cmd.getOptionValue("p"));

                if (checkProjectBaseDir(parsedValue)) {
                    System.out.println(parsedValue.getErrorMessage());
                    return false;
                }
            }

            if (cmd.hasOption("s")) {
                parsedValue.setSrc(cmd.getOptionValue("s"));
            }

            if (cmd.hasOption("b")) {
                parsedValue.setBinary(cmd.getOptionValue("b"));
            }

            if (cmd.hasOption("library")) {
                parsedValue.setLibrary(cmd.getOptionValue("library"));
            }

            if (cmd.hasOption("w")) {
                parsedValue.setWebapp(cmd.getOptionValue("w"));
            }

            if (cmd.hasOption("d")) {
                parsedValue.setDebug(true);
            }

            if (cmd.hasOption("e")) {
                parsedValue.setEncoding(cmd.getOptionValue("e"));
            }

            if (cmd.hasOption("j")) {
                parsedValue.setJavaVersion(cmd.getOptionValue("j"));
            }

            if (cmd.hasOption("pmd")) {
                parsedValue.setRuleSetFileForPMD(cmd.getOptionValue("pmd"));
            }

            if (cmd.hasOption("findbugs")) {
                parsedValue.setRuleSetFileForFindBugs(cmd.getOptionValue("findbugs"));
            }

            if (cmd.hasOption("sonar")) {
                parsedValue.setRuleSetFileForSonar(cmd.getOptionValue("sonar"));
            }

            if (cmd.hasOption("checkstyle")) {
                parsedValue.setRuleSetFileForCheckStyle(cmd.getOptionValue("checkstyle"));
            }

            if (cmd.hasOption("o")) {
                parsedValue.setOutput(cmd.getOptionValue("o"));
            }

            if (cmd.hasOption("f")) {
                String formatValue = cmd.getOptionValue("f");

                if (formatValue.equalsIgnoreCase("text") || formatValue.equalsIgnoreCase("json") || formatValue.equalsIgnoreCase("none")) {
                    parsedValue.setFormat(OutputFileFormat.valueOf(formatValue.toUpperCase()));
                } else {
                    parsedValue.setErrorMessage("Option Error : 'format' option's value has to be 'json', 'text' or 'none'");
                    System.out.println(parsedValue.getErrorMessage());
                    help(options, cmd);
                    return false;
                }
            }

            if (cmd.hasOption("t")) {
                parsedValue.setTimeout(cmd.getOptionValue("t"));
            }

            if (cmd.hasOption("c")) {
                parsedValue.setMode(MeasurementMode.ComplexityMode);
                parsedValue.setClassForCCMeasurement(cmd.getOptionValue("c"));
            }

            if (cmd.hasOption("include")) {
                parsedValue.setIncludes(FileArgumentUtil.getFileArgument(cmd.getOptionValue("include")));
            }

            if (cmd.hasOption("exclude")) {
                parsedValue.setExcludes(FileArgumentUtil.getFileArgument(cmd.getOptionValue("exclude")));
            }

            if (cmd.hasOption("m")) {
                if (cmd.hasOption("c")) {
                    parsedValue.setErrorMessage("Option Error : 'mode' option and 'complexity' can not be specified together");
                    System.out.println(parsedValue.getErrorMessage());
                    help(options, cmd);
                    return false;
                }
                String analysisModeValue = cmd.getOptionValue("m").replaceAll(EXCLUSION_PREFIX, "");

                if (!settingAnalysisMode(parsedValue, analysisModeValue)) {
                    System.out.println(parsedValue.getErrorMessage());
                    help(options, cmd);
                    return false;
                }
            } else {
                parsedValue.getIndividualMode().setDefault();
            }

            setDefaultIndividualModeAfterParsing(parsedValue);

            checkWebAppOption(parsedValue);

            checkAndModifyDirectories(parsedValue);

            if (cmd.hasOption("a")) {
                parsedValue.setDetailAnalysis(true);
            }

            if (cmd.hasOption("seperated")) {
                parsedValue.setSeperatedOutput(true);
            }

            if (cmd.hasOption("r")) {
                getOptionsFromOutFile(parsedValue, cmd.getOptionValue("r"));
            }

            if (cmd.hasOption("catalog")) {
                parsedValue.setSaveCatalog(true);
            }

            if (checkDuplicationType(options, parsedValue, cmd)) {
                return false;
            }

            if (checkSourceDuplication(parsedValue)) {
                parsedValue.setErrorMessage("Source Directories(include webapp dir.) overlapped. : " +
                        parsedValue.getSrc() + (parsedValue.getWebapp().equals("") ? "" : "," + parsedValue.getWebapp()));
                System.out.println(parsedValue.getErrorMessage());
                help(options, cmd);
                return false;
            }

            return true;
        } catch (ParseException pe) {
            parsedValue.setErrorMessage("Failed to parse command line");
            LOGGER.error(parsedValue.getErrorMessage(), pe);
            help(options);
            return false;
        }
    }

    @Override
    public String getModeErrorMessage() {
        return "'mode' option can only have 'code-size', 'duplication', 'complexity', 'sonarjava', 'pmd', 'findbugs', 'findsecbugs', " +
            "'javascript'(sonarjs), 'css', 'html', 'dependency', 'unusedcode', 'ckmetrics', and 'checkstyle' (with or without '-')";
    }
}
