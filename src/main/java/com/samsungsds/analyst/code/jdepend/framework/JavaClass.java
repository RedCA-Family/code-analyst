package com.samsungsds.analyst.code.jdepend.framework;

import java.util.*;

/**
 * The <code>JavaClass</code> class represents a Java 
 * class or interface.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class JavaClass {

    private String className;
    private String packageName;
    private boolean isAbstract;
    private HashMap<String, JavaPackage> imports;
    private String sourceFile;


    public JavaClass(String name) {
        className = name;
        packageName = "default";
        isAbstract = false;
        imports = new HashMap<>();
        sourceFile = "Unknown";
    }

    public void setName(String name) {
        className = name;
    }

    public String getName() {
        return className;
    }

    public void setPackageName(String name) {
        packageName = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setSourceFile(String name) {
        sourceFile = name;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public Collection<JavaPackage> getImportedPackages() {
        return imports.values();
    }

    public void addImportedPackage(JavaPackage jPackage) {
        if (!jPackage.getName().equals(getPackageName())) {
            imports.put(jPackage.getName(), jPackage);
        }
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void isAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public boolean equals(Object other) {

        if (other instanceof JavaClass) {
            JavaClass otherClass = (JavaClass) other;
            return otherClass.getName().equals(getName());
        }

        return false;
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public static class ClassComparator implements Comparator<JavaClass> {

        public int compare(JavaClass c1, JavaClass c2) {
            return c1.getName().compareTo(c2.getName());
        }
    }
}
