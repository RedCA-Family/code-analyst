package com.samsungsds.analyst.code.python;

import org.junit.Test;

import java.util.regex.Matcher;

import static org.junit.Assert.*;

public class PythonRuntimeTest {

    @Test
    public void testForAnacondaVersion() {
        // arrange
        String versionString = "Python 3.6.5 :: Anaconda, Inc.";

        // act
        int majorVersion = 0;
        int minorVersion = 0;
        int patchVersion = 0;

        Matcher matcher = PythonRuntime.PYTHON_VERSION_PATTERN.matcher(versionString);
        if (matcher.lookingAt()) {
            majorVersion = Integer.parseInt(matcher.group(1));
            minorVersion = Integer.parseInt(matcher.group(2));
            patchVersion = Integer.parseInt(matcher.group(3));
        } else {
            fail("Paring error");
        }

        // assert
        assertEquals(3, majorVersion);
        assertEquals(6, minorVersion);
        assertEquals(5, patchVersion);
    }

    @Test
    public void testForGeneralVersion() {
        // arrange
        String versionString = "Python 3.6.5";

        // act
        int majorVersion = 0;
        int minorVersion = 0;
        int patchVersion = 0;

        Matcher matcher = PythonRuntime.PYTHON_VERSION_PATTERN.matcher(versionString);
        if (matcher.lookingAt()) {
            majorVersion = Integer.parseInt(matcher.group(1));
            minorVersion = Integer.parseInt(matcher.group(2));
            patchVersion = Integer.parseInt(matcher.group(3));
        } else {
            fail("Paring error");
        }

        // assert
        assertEquals(3, majorVersion);
        assertEquals(6, minorVersion);
        assertEquals(5, patchVersion);
    }
}
