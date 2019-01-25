package com.samsungsds.analyst.code.main;

public interface FileSkipChecker {
    /**
     * Check path (without adding source prefix and with filename).
     * Path example: src\main\java\com\samsungsds\Test.java
    */
    boolean haveToSkip(String path);

    /**
     * Check path including specifying whether to add source prefix (with filename).
     * Path example (w/ addSrcPrefix is true) : com\samsungsds\Test.java
     */
    boolean haveToSkip(String path, boolean addSrcPrefix);

    /**
     * Check path including specifying whether to add source prefix and whether to have filename.
     * When checked, "/*.java" is added on the matching pattern.
     * Path example (w/ addSrcPrefix is true and w/o filename) : com\samsungsds\*.java
     */
    boolean haveToSkip(String path, boolean addSrcPrefix, boolean withoutFilename);
}
