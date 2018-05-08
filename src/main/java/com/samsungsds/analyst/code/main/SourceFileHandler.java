package com.samsungsds.analyst.code.main;

import com.samsungsds.analyst.code.util.FindFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static com.samsungsds.analyst.code.main.filter.FilePathAbstractFilter.FIXED_PREFIX;

public class SourceFileHandler {
    private static final Logger LOGGER = LogManager.getLogger(SourceFileHandler.class);

    private String projectBaseDir;
    private String src;

    public SourceFileHandler(String projectBaseDir, String src) {
        this.projectBaseDir = projectBaseDir.replaceAll("/", "\\\\");;
        this.src = src.replaceAll("/", "\\\\");;
    }

    public String getPathStringWithInclude(String includes) {
        StringBuilder ret = new StringBuilder();

        for (String pattern : includes.split(FindFileUtils.COMMA_SPLITTER)) {

            if (ret.length() != 0) {
                ret.append(",");
            }

            if (pattern.startsWith(FIXED_PREFIX)) {
                pattern = pattern.substring(FIXED_PREFIX.length());
            }

            ret.append(projectBaseDir).append(File.separator).append(src).append(File.separator);

            for (String path : pattern.replaceAll("/", "\\\\").split("\\\\")) {
                if (path.indexOf("*") >= 0 || path.indexOf("?") >= 0) {
                    break;
                }

                ret.append(path).append("\\");
            }
        }

        String directoriesOrFiles = ret.toString();

        if (directoriesOrFiles.endsWith("\\")) {
            directoriesOrFiles = directoriesOrFiles.substring(0, directoriesOrFiles.length() - 1);
        }

        LOGGER.info("Modified Directories or files : {}", directoriesOrFiles);

        return directoriesOrFiles;
    }
}
