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
package com.samsungsds.analyst.code.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileLineFinder implements AutoCloseable {
    private Map<FileLine, String> lineData = new HashMap<>();

    public String getLine(String filePath, int line) {
        FileLine fileLine = new FileLine(filePath, line);

        if (lineData.containsKey(fileLine)) {
            return lineData.get(fileLine);
        } else {
            return addFileLinesToAndGetLine(filePath, line);
        }
    }

    private String addFileLinesToAndGetLine(String filePath, int line) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            FileLine fileLine = null;
            for (int i = 0; i < line; i++) {
                String lineString = br.readLine();

                fileLine = new FileLine(filePath, i + 1);

                if (!lineData.containsKey(fileLine)) {
                    lineData.put(fileLine, lineString);
                }
            }

            return lineData.get(fileLine);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public void close() {
        lineData.clear();
    }
}

class FileLine {
    private final String filePath;
    private final int line;

    public FileLine(String filePath, int line) {
        this.filePath = filePath;
        this.line = line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileLine fileLine = (FileLine) o;

        if (line != fileLine.line) return false;
        return filePath.equals(fileLine.filePath);
    }

    @Override
    public int hashCode() {
        int result = filePath.hashCode();
        result = 31 * result + line;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FileLine{");
        sb.append(filePath).append('\'');
        sb.append(", ").append(line);
        sb.append('}');
        return sb.toString();
    }
}
