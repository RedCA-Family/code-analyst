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

import com.samsungsds.analyst.code.main.Constants;
import com.samsungsds.analyst.code.main.Version;
import com.samsungsds.analyst.code.util.FindFileUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

public abstract class AbstractCliParseProcessor implements CliParseProcessor {
    private static final Logger LOGGER = LogManager.getLogger(AbstractCliParseProcessor.class);

    protected static final String EXCLUSION_PREFIX = "exclusion:";

    protected void preprocessArgs(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("-m") || args[i].equals("--mode")) {
                if (args[i+1].startsWith("-")) {
                    args[i+1] = EXCLUSION_PREFIX + args[i+1];
                }
                break;
            }
        }
    }

    protected void help(Options options) {
        HelpFormatter formatter = new HelpFormatter();

        formatter.setWidth(120);
        formatter.setOptionComparator(null); // no order

        formatter.printHelp("java -jar " + Version.APPLICATION_JAR, options);
    }

    protected void help(Options options, CommandLine cmd) {
        HelpFormatter formatter = new HelpFormatter();

        formatter.setWidth(120);
        formatter.setOptionComparator(null); // no order

        if (cmd.hasOption("l")) {
            formatter.printHelp("java -jar " + Version.APPLICATION_JAR, options);
        } else {
            formatter.printHelp("java -jar " + Version.APPLICATION_JAR + "\n" + " ※ To see individual language-specific option usages, specify the '-l' or '--language' option", options);
        }
    }

    protected boolean checkProjectBaseDir(CliParsedValueObject parsedValue) {
        if (parsedValue.getProjectBaseDir().contains(",")) {
            parsedValue.setErrorMessage("The 'project' directory contains a comma.\n" +
                    "In this case, go to the project directory and run again without the 'project' option.");

            return true;
        }

        return false;
    }

    protected boolean settingAnalysisMode(CliParsedValueObject parsedValue, String analysisModeValue) {
        String[] modes = analysisModeValue.split(FindFileUtils.COMMA_SPLITTER);

        try {
            parseIndividualMode(parsedValue, modes);
        } catch (IllegalArgumentException iae) {
            parsedValue.setErrorMessage("Option Error : " + iae.getMessage());

            return false;
        }

        parsedValue.setAnalysisMode(analysisModeValue);

        return true;
    }

    private void parseIndividualMode(CliParsedValueObject parsedValue, String[] modes) {
        boolean hasMinusOption = false;
        for (String mode : modes) {
            if (mode.startsWith("-")) {
                hasMinusOption = true;
                break;
            }
        }

        if (hasMinusOption) {
            parsedValue.getIndividualMode().setDefault();
        } else {
            parsedValue.getIndividualMode().setUnsetAll();
        }

        for (String mode : modes) {
            boolean includeOrExclude = true;

            if (mode.startsWith("-")) {
                includeOrExclude = false;
                mode = mode.substring(1);
            }

            if (mode.equalsIgnoreCase("code-size") || mode.equalsIgnoreCase("codesize")) {
                parsedValue.getIndividualMode().setCodeSize(includeOrExclude);
            } else if (mode.equalsIgnoreCase("duplication")) {
                parsedValue.getIndividualMode().setDuplication(includeOrExclude);
            } else if (mode.equalsIgnoreCase("complexity")) {
                parsedValue.getIndividualMode().setComplexity(includeOrExclude);
            } else if (mode.equalsIgnoreCase("sonarjava")) {
                parsedValue.getIndividualMode().setSonarJava(includeOrExclude);
            } else if (mode.equalsIgnoreCase("pmd")) {
                parsedValue.getIndividualMode().setPmd(includeOrExclude);
            } else if (mode.equalsIgnoreCase("findbugs")) {
                parsedValue.getIndividualMode().setFindBugs(includeOrExclude);
            } else if (mode.equalsIgnoreCase("findsecbugs")) {
                parsedValue.getIndividualMode().setFindSecBugs(includeOrExclude);
            } else if (mode.equalsIgnoreCase("javascript") || mode.equalsIgnoreCase("sonarjs")) {
                parsedValue.getIndividualMode().setJavascript(includeOrExclude);
            } else if (mode.equalsIgnoreCase("css")) {
                parsedValue.getIndividualMode().setCss(includeOrExclude);
            } else if (mode.equalsIgnoreCase("html")) {
                parsedValue.getIndividualMode().setHtml(includeOrExclude);
            } else if (mode.equalsIgnoreCase("sonarcsharp")) {
                parsedValue.getIndividualMode().setSonarCSharp(includeOrExclude);
            } else if (mode.equalsIgnoreCase("sonarpython")) {
                parsedValue.getIndividualMode().setSonarPython(includeOrExclude);
            } else if (mode.equalsIgnoreCase("dependency")) {
                parsedValue.getIndividualMode().setDependency(includeOrExclude);
            } else if (mode.equalsIgnoreCase("unusedcode")) {
                parsedValue.getIndividualMode().setUnusedCode(includeOrExclude);
            } else if (mode.equalsIgnoreCase("ckmetrics")) {
                parsedValue.getIndividualMode().setCkMetrics(includeOrExclude);
            } else if (mode.equalsIgnoreCase("checkstyle")) {
                parsedValue.getIndividualMode().setCheckStyle(includeOrExclude);
            } else {
                throw new IllegalArgumentException(getModeErrorMessage());
            }
        }
    }

    protected void checkWebAppOption(CliParsedValueObject parsedValue) {
        if (parsedValue.getWebapp().equals("") &&
                (parsedValue.getIndividualMode().isJavascript() || parsedValue.getIndividualMode().isWebResources())) {
            LOGGER.info("webapp option not found => disable Web Resources inspection");
            parsedValue.getIndividualMode().setJavascript(false);
            parsedValue.getIndividualMode().setCss(false);
            parsedValue.getIndividualMode().setHtml(false);
        }
    }

    protected void checkAndModifyDirectories(CliParsedValueObject parsedValue) {
        parsedValue.setSrc(getModifiedDirectories(parsedValue.getSrc()));
        parsedValue.setBinary(getModifiedDirectories(parsedValue.getBinary()));
    }

    private String getModifiedDirectories(String directories) {
        StringBuilder builder = new StringBuilder();

        for (String dir : directories.split(FindFileUtils.COMMA_SPLITTER)) {
            if (builder.length() != 0) {
                builder.append(",");
            }
            if (dir.startsWith("\\") || dir.startsWith("/")) {
                builder.append(dir.substring(1));
            } else {
                builder.append(dir);
            }
        }

        return builder.toString();
    }

    protected void getOptionsFromOutFile(CliParsedValueObject parsedValue, String outputFile) {
        Wini ini;
        try {
            ini = new Wini(new File(outputFile));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        String lang = getCheckedString(ini, "Project", "Language", true);

        if (!lang.equals("")) {
            parsedValue.setLanguage(lang);
        }

        parsedValue.setProjectBaseDir(getCheckedString(ini, "Project", "Target"));
        parsedValue.setSrc(getCheckedString(ini, "Project", "Source"));
        parsedValue.setBinary(getCheckedString(ini, "Project", "Binary"));
        parsedValue.setEncoding(getCheckedString(ini, "Project", "Encoding"));
        parsedValue.setJavaVersion(getCheckedString(ini, "Project", "JavaVersion"));
        parsedValue.setRuleSetFileForPMD(getCheckedString(ini, "Project", "PMD", true));
        parsedValue.setRuleSetFileForFindBugs(getCheckedString(ini, "Project", "FindBugs", true));
        parsedValue.setRuleSetFileForSonar(getCheckedString(ini, "Project", "Sonar", true));
        parsedValue.setIncludes(getCheckedString(ini, "Project", "includes", true));
        parsedValue.setExcludes(getCheckedString(ini, "Project", "excludes", true));
        parsedValue.setWebapp(getCheckedString(ini, "Project", "webapp", true));

        String mode = getCheckedString(ini, "Project", "mode");
        if (!mode.equals(Constants.DEFAULT_ANALYSIS_MODE)) {
            settingAnalysisMode(parsedValue, mode);
        }

        checkWebAppOption(parsedValue);

        String analysis = getCheckedString(ini, "Project", "detailAnalysis", true);

        if (analysis.equals("true")) {
            parsedValue.setDetailAnalysis(true);
        }

        String seperated = getCheckedString(ini, "Project", "seperatedOutput", true);

        if (seperated.equals("true")) {
            parsedValue.setSeperatedOutput(true);
        }

        String catalog = getCheckedString(ini, "Project", "saveCatalog", true);

        if (catalog.equals("true")) {
            parsedValue.setSaveCatalog(true);
        }

        String duplication = getCheckedString(ini, "Project", "duplication", true);

        if (duplication.equals("token")) {
            parsedValue.setTokenBased(true);
        }

        String tokens = getCheckedString(ini, "Project", "tokens", true);

        if (!tokens.equals("")) {
            parsedValue.setMinimumTokens(Integer.parseInt(tokens));
        }

        LOGGER.info("Rerun with following options");
        if (!parsedValue.getLanguage().equals("")) {
            LOGGER.info(" - language : {}", parsedValue.getLanguage());
        }

        if (parsedValue.getLanguage().equalsIgnoreCase("java")) {
            LOGGER.info(" - project : {}", parsedValue.getProjectBaseDir());
            LOGGER.info(" - src : {}", parsedValue.getSrc());
            LOGGER.info(" - binary : {}", parsedValue.getBinary());
            LOGGER.info(" - encoding : {}", parsedValue.getEncoding());
            LOGGER.info(" - java : {}", parsedValue.getJavaVersion());
            if (!parsedValue.getRuleSetFileForPMD().equals("")) {
                LOGGER.info(" - pmd : {}", parsedValue.getRuleSetFileForPMD());
            }
            if (!parsedValue.getRuleSetFileForFindBugs().equals("")) {
                LOGGER.info(" - findbugs : {}", parsedValue.getRuleSetFileForFindBugs());
            }
            if (!parsedValue.getRuleSetFileForSonar().equals("")) {
                LOGGER.info(" - sonar : {}", parsedValue.getRuleSetFileForSonar());
            }
            if (!parsedValue.getWebapp().equals("")) {
                LOGGER.info(" - webapp : {}", parsedValue.getWebapp());
            }
            if (!parsedValue.getIncludes().equals("")) {
                LOGGER.info(" - include : {}", parsedValue.getIncludes());
            }
            if (!parsedValue.getExcludes().equals("")) {
                LOGGER.info(" - exclude : {}", parsedValue.getExcludes());
            }
            if (!mode.equals(Constants.DEFAULT_ANALYSIS_MODE)) {
                LOGGER.info(" - mode : {}", mode);
            }
            if (analysis.equals("true")) {
                LOGGER.info(" - detailAnalysis = true");
            }
            if (seperated.equals("true")) {
                LOGGER.info(" - seperatedOutput = true");
            }
            if (catalog.equals("true")) {
                LOGGER.info("- saveCatalog = true");
            }
            if (!duplication.equals("")) {
                LOGGER.info("- duplication = {}", duplication);
            }
            if (!tokens.equals("")) {
                LOGGER.info("- tokens = {}", tokens);
            }
        } else if (parsedValue.getLanguage().equalsIgnoreCase("javascript")) {
            LOGGER.info(" - project : {}", parsedValue.getProjectBaseDir());
            LOGGER.info(" - src : {}", parsedValue.getSrc());
            LOGGER.info(" - encoding : {}", parsedValue.getEncoding());
            if (!parsedValue.getRuleSetFileForSonar().equals("")) {
                LOGGER.info(" - sonar : {}", parsedValue.getRuleSetFileForSonar());
            }
            if (!parsedValue.getIncludes().equals("")) {
                LOGGER.info(" - include : {}", parsedValue.getIncludes());
            }
            if (!parsedValue.getExcludes().equals("")) {
                LOGGER.info(" - exclude : {}", parsedValue.getExcludes());
            }
            if (!mode.equals(Constants.DEFAULT_ANALYSIS_MODE)) {
                LOGGER.info(" - mode : {}", mode);
            }
            if (analysis.equals("true")) {
                LOGGER.info(" - detailAnalysis = true");
            }
            if (seperated.equals("true")) {
                LOGGER.info(" - seperatedOutput = true");
            }
            if (catalog.equals("true")) {
                LOGGER.info("- saveCatalog = true");
            }
        } else if (parsedValue.getLanguage().equalsIgnoreCase("csharp") ||
                parsedValue.getLanguage().equalsIgnoreCase("c#")) {
            LOGGER.info(" - project : {}", parsedValue.getProjectBaseDir());
            LOGGER.info(" - src : {}", parsedValue.getSrc());
            // LOGGER.info(" - binary : {}", parsedValue.getBinary()); // TODO check!
            LOGGER.info(" - encoding : {}", parsedValue.getEncoding());
            if (!parsedValue.getRuleSetFileForSonar().equals("")) {
                LOGGER.info(" - sonar : {}", parsedValue.getRuleSetFileForSonar());
            }
            if (!parsedValue.getIncludes().equals("")) {
                LOGGER.info(" - include : {}", parsedValue.getIncludes());
            }
            if (!parsedValue.getExcludes().equals("")) {
                LOGGER.info(" - exclude : {}", parsedValue.getExcludes());
            }
            if (!mode.equals(Constants.DEFAULT_ANALYSIS_MODE)) {
                LOGGER.info(" - mode : {}", mode);
            }
            if (analysis.equals("true")) {
                LOGGER.info(" - detailAnalysis = true");
            }
            if (seperated.equals("true")) {
                LOGGER.info(" - seperatedOutput = true");
            }
            if (catalog.equals("true")) {
                LOGGER.info("- saveCatalog = true");
            }
        } else {    // python
            LOGGER.info(" - project : {}", parsedValue.getProjectBaseDir());
            LOGGER.info(" - src : {}", parsedValue.getSrc());
            LOGGER.info(" - encoding : {}", parsedValue.getEncoding());
            if (!parsedValue.getRuleSetFileForSonar().equals("")) {
                LOGGER.info(" - sonar : {}", parsedValue.getRuleSetFileForSonar());
            }
            if (!parsedValue.getIncludes().equals("")) {
                LOGGER.info(" - include : {}", parsedValue.getIncludes());
            }
            if (!parsedValue.getExcludes().equals("")) {
                LOGGER.info(" - exclude : {}", parsedValue.getExcludes());
            }
            if (!mode.equals(Constants.DEFAULT_ANALYSIS_MODE)) {
                LOGGER.info(" - mode : {}", mode);
            }
            if (analysis.equals("true")) {
                LOGGER.info(" - detailAnalysis = true");
            }
            if (seperated.equals("true")) {
                LOGGER.info(" - seperatedOutput = true");
            }
            if (catalog.equals("true")) {
                LOGGER.info("- saveCatalog = true");
            }
        }
    }

    private String getCheckedString(Wini ini, String sectionName, String optionName) {
        return getCheckedString(ini, sectionName, optionName, false);
    }

    private String getCheckedString(Wini ini, String sectionName, String optionName, boolean canBeNull) {
        String value = ini.get(sectionName, optionName);

        if (value == null) {
            if (canBeNull) {
                return "";
            } else {
                throw new IllegalArgumentException("[" + sectionName + "]'s " + optionName + " parsedValue null!");
            }
        }

        return value.trim();
    }

    protected boolean checkSourceDuplication(CliParsedValueObject parsedValue) {
        String[] srcDirectories;
        String[] targets;

        if (parsedValue.getSrc().equals("")) {
            srcDirectories = new String[0];
        } else {
            srcDirectories = parsedValue.getSrc().split(FindFileUtils.COMMA_SPLITTER);
        }

        if (parsedValue.getWebapp().equals("")) {
            targets = new String[srcDirectories.length];
        } else {
            targets = new String[srcDirectories.length + 1];
        }

        for (int i = 0; i < srcDirectories.length; i++) {
            targets[i] = srcDirectories[i].replaceAll("\\\\", "/");
            if (!targets[i].endsWith("/")) {
                targets[i] += "/";
            }
        }

        if (!parsedValue.getWebapp().equals("")) {
            targets[srcDirectories.length] = parsedValue.getWebapp().replaceAll("\\\\", "/");
            if (!targets[srcDirectories.length].endsWith("/")) {
                targets[srcDirectories.length] += "/";
            }
        }

        for (int i = 0; i < targets.length; i++) {
            for (int j = 0; j < targets.length; j++) {
                if (i == j) {
                    continue;
                }

                if (targets[j].startsWith(targets[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean checkDuplicationType(Options options, CliParsedValueObject parsedValue, CommandLine cmd) {
        if (cmd.hasOption("duplication")) {
            String duplicationMode = cmd.getOptionValue("duplication");
            if (duplicationMode.equalsIgnoreCase("statement")) {
                parsedValue.setTokenBased(false);
            } else if (duplicationMode.equalsIgnoreCase("token")) {
                parsedValue.setTokenBased(true);
            } else {
                parsedValue.setErrorMessage("Option Error : 'duplication' option's value has to be 'statement' or 'token'");
                System.out.println(parsedValue.getErrorMessage());
                help(options, cmd);
                return true;
            }
        }

        if (cmd.hasOption("tokens")) {
            parsedValue.setMinimumTokens(Integer.parseInt(cmd.getOptionValue("tokens")));
        }
        return false;
    }
}
