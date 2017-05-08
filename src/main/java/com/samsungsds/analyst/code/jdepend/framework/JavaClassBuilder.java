package com.samsungsds.analyst.code.jdepend.framework;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

/**
 * The <code>JavaClassBuilder</code> builds <code>JavaClass</code> 
 * instances from .class, .jar, .war, or .zip files.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class JavaClassBuilder {

    private AbstractParser parser;
    private FileManager fileManager;

    
    public JavaClassBuilder() {
        this(new ClassFileParser(), new FileManager());
    }

    public JavaClassBuilder(FileManager fm) {
        this(new ClassFileParser(), fm);
    }

    public JavaClassBuilder(AbstractParser parser, FileManager fm) {
        this.parser = parser;
        this.fileManager = fm;
    }

    public int countClasses() {
        AbstractParser counter = new AbstractParser() {

            public JavaClass parse(InputStream is) {
                return new JavaClass("");
            }
        };

        JavaClassBuilder builder = new JavaClassBuilder(counter, fileManager);
        Collection<JavaClass> classes = builder.build();
        
        return classes.size();
    }

    /**
     * Builds the <code>JavaClass</code> instances.
     * 
     * @return Collection of <code>JavaClass</code> instances.
     */
    public Collection<JavaClass> build() {

        Collection<JavaClass> classes = new ArrayList<>();

        for (File nextFile : fileManager.extractFiles()) {

            try {

                classes.addAll(buildClasses(nextFile));

            } catch (IOException ioe) {
                System.err.println("\n" + ioe.getMessage());
            }
        }

        return classes;
    }

    /**
     * Builds the <code>JavaClass</code> instances from the 
     * specified file.
     * 
     * @param file Class or Jar file.
     * @return Collection of <code>JavaClass</code> instances.
     */
    public Collection<JavaClass> buildClasses(File file) throws IOException {

        if (fileManager.acceptClassFile(file)) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
                JavaClass parsedClass = parser.parse(is);
                Collection<JavaClass> javaClasses = new ArrayList<>();
                javaClasses.add(parsedClass);
                return javaClasses;
            }
        } else if (fileManager.acceptJarFile(file)) {

            JarFile jarFile = new JarFile(file);
            Collection<JavaClass> result = buildClasses(jarFile);
            jarFile.close();
            return result;

        } else {
            throw new IOException("File is not a valid .class, .jar, .war, or .zip file: " + file.getPath());
        }
    }

    /**
     * Builds the <code>JavaClass</code> instances from the specified 
     * jar, war, or zip file.
     * 
     * @param file Jar, war, or zip file.
     * @return Collection of <code>JavaClass</code> instances.
     */
    public Collection<JavaClass> buildClasses(JarFile file) throws IOException {

        Collection<JavaClass> javaClasses = new ArrayList<>();

        Enumeration<JarEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            ZipEntry e = (ZipEntry) entries.nextElement();
            if (fileManager.acceptClassFileName(e.getName())) {
                try (InputStream is = new BufferedInputStream(file.getInputStream(e))) {
                    JavaClass jc = parser.parse(is);
                    javaClasses.add(jc);
                }
            }
        }

        return javaClasses;
    }
}
