package com.samsungsds.analyst.code.ckmetrics.utils;

import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class JarUtils {
    public static File getJarFromClasspathDirectories(String classpaths) {
        List<File> targetDirectories = new ArrayList<>();
        String[] paths = classpaths.split(Pattern.quote(System.getProperty("path.separator")));
        for (int i = 0; i < paths.length; i++) {
            File a = new File(paths[i].trim());
            targetDirectories.addAll(Arrays.asList(a.listFiles()));
        }

        File jar;
        try {
            jar = File.createTempFile("tmp", ".jar");
            jar.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            JarUtils.compress(jar.toString(), targetDirectories.toArray(new File[0]));
        } catch (IOException e) {
            throw new RuntimeException("Error making classpath jar", e);
        }

        return jar;
    }

    public static void compress(String name, File... files) throws IOException {
        try (JarArchiveOutputStream out = new JarArchiveOutputStream(new FileOutputStream(name))) {
            for (File file : files) {
                addToArchiveCompression(out, file, ".");
            }
        }
    }

    private static void addToArchiveCompression(JarArchiveOutputStream out, File file, String dir) throws IOException {
        String name = dir + File.separator + file.getName();
        if (dir.equals(".")) {
            name = file.getName();
        }

        if (file.isFile()) {
            JarArchiveEntry entry = new JarArchiveEntry(name);
            out.putArchiveEntry(entry);
            entry.setSize(file.length());
            try (FileInputStream input = new FileInputStream(file)) {
                IOUtils.copy(input, out);
                out.closeArchiveEntry();
            }
        } else if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null){
                for (File child : children){
                    addToArchiveCompression(out, child, name);
                }
            }
        } else {
            System.out.println(file.getName() + " is not supported");
        }
    }
}
