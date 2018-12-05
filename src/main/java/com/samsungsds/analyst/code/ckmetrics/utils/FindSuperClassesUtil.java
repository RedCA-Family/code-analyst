package com.samsungsds.analyst.code.ckmetrics.utils;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Utility;

public class FindSuperClassesUtil {
    private String errorMessage;
    private int count = 1;

    public int find(JavaClass jc) {
        String superClassName = jc.getSuperclassName();

        if ("java.lang.Object".equals(superClassName)) {
            return count;
        }

        try {
            JavaClass superJc = Repository.lookupClass(superClassName);

            count++;

            return find(superJc);
        } catch (ClassNotFoundException e) {
            errorMessage = "Error obtaining all superclasses of " + jc.getClassName() + " extends " + Utility.compactClassName(jc.getSuperclassName(), false);

            return count + 1;
        }
    }

    public boolean hasError() {
        return errorMessage != null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
