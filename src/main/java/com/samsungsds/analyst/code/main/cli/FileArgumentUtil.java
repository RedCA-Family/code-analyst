package com.samsungsds.analyst.code.main.cli;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.List;

public class FileArgumentUtil {
    protected static String getFileArgument(String argument) {
        if (argument.startsWith("@")) {
            StringBuilder builder = new StringBuilder();

            try {
                List<String> lines = FileUtils.readLines(new File(argument.substring(1)), Charset.defaultCharset());

                for (String line : lines) {
                    line = line.trim();
                    if (line.equals("") || line.equals(",")) {
                        continue;
                    }
                    if (line.endsWith(",")) {
                        builder.append(line);
                    } else {
                        builder.append(line).append(",");
                    }
                }

                return builder.substring(0, builder.length() - 1);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        } else {
            return argument;
        }
    }
}
