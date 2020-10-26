package com.samsungsds.analyst.code.main.subject;

import java.io.File;

public class FirstFileFinder {
    private final String where;
    private final String extension;

    public FirstFileFinder(String where, String extension) {
        this.where = where;
        this.extension = extension.toLowerCase();
    }

    public String getPath() {
        String path = findRecursiveFile(new File(where));

        if (path == null) {
            throw new IllegalStateException("There are no '" + extension + "' files in that directory : " + where);
        }

        return path;
    }

    private String findRecursiveFile(File dir) {
        File[] files = dir.listFiles((d, name) -> !name.contains("$") && name.toLowerCase().endsWith("." + extension));

        if (files == null) {
            throw new IllegalStateException("Directory error : " + dir);
        }
        if (files.length > 0) {
            return files[0].getAbsolutePath();
        }

        File[] subDirectories = dir.listFiles(pathname -> pathname.isDirectory());

        for (File sub : subDirectories) {
            String result = findRecursiveFile(sub);
            if (result != null) {
                return result;
            }
        }

        return null;
    }
}
