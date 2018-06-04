package com.samsungsds.analyst.code.main;

import com.samsungsds.analyst.code.util.FindFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.samsungsds.analyst.code.main.filter.FilePathAbstractFilter.FIXED_PREFIX;

public class SourceFileHandler {
    private static final Logger LOGGER = LogManager.getLogger(SourceFileHandler.class);

    private String projectBaseDir;
    private String[] srcDirectories;

    public SourceFileHandler(String projectBaseDir, String src) {
        if (src.contains(",")) {
            throw new IllegalArgumentException("If src has comma, then use other constructor with string array.");
        }

        this.projectBaseDir = projectBaseDir.replaceAll("/", "\\\\");

        this.srcDirectories = new String[1];
        this.srcDirectories[0] = src.replaceAll("/", "\\\\");
    }

    public SourceFileHandler(String projectBaseDir, String[] srcDirectories) {
        this.projectBaseDir = projectBaseDir.replaceAll("/", "\\\\");

        this.srcDirectories = new String[srcDirectories.length];

        for (int i = 0; i < srcDirectories.length; i++) {
            this.srcDirectories[i] =  srcDirectories[i].replaceAll("/", "\\\\");
        }
    }

    public String getPathStringWithInclude(String includes) {
        StringBuilder ret = new StringBuilder();

        for (String src : srcDirectories) {
            appendStringWithComma(ret, getPathStringWithInclude(includes, src));
        }

        LOGGER.info("Modified Directories or files : {}", ret);

        return ret.toString();
    }

    private String getPathStringWithInclude(String includes, String src) {
        StringBuilder ret = new StringBuilder();

        for (String pattern : includes.split(FindFileUtils.COMMA_SPLITTER)) {
            StringBuilder filePath = new StringBuilder();

            boolean withFixedPrefix = false;

            if (pattern.startsWith(FIXED_PREFIX)) {
                pattern = pattern.substring(FIXED_PREFIX.length());

                withFixedPrefix = true;
            }

            filePath.append(projectBaseDir).append(File.separator).append(src).append(File.separator);

            boolean withoutPattern = true;
            for (String path : pattern.replaceAll("/", "\\\\").split("\\\\")) {
                if (path.contains("*") || path.contains("?")) {
                    withoutPattern = false;
                    break;
                }

                filePath.append(path).append("\\");
            }

            if (filePath.toString().endsWith("\\")) {
                filePath.deleteCharAt(filePath.length() - 1);
            }

            if (ret.indexOf(filePath.toString()) < 0) {
                if (withFixedPrefix && withoutPattern) {
                    if (Files.exists(Paths.get(filePath.toString()))) {
                        appendStringWithComma(ret, filePath.toString());
                    }
                } else {
                    appendStringWithComma(ret, filePath.toString());
                }
            }

        }

        return ret.toString();
    }

    private void appendStringWithComma(StringBuilder builder, final String str) {
        if (builder.length() != 0) {
            builder.append(",");
        }
        builder.append(str);
    }
}
