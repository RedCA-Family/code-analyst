package com.samsungsds.analyst.code.main;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class SourceFileHandlerTest {
    @Test
    public void testForWindowsStyle() {
        // arrange
        SourceFileHandler handler = new SourceFileHandler(".", new String[]{"src\\main\\java"});

        // act
        String result = handler.getPathStringWithInclude("**/*Controller.java");

        // assert
        assertThat(result, either(is(".\\src\\main\\java")).or(is("./src/main/java")));
        assertTrue(new File(result).exists());
    }

    @Test
    public void testForLinuxStyle() {
        // arrange
        SourceFileHandler handler = new SourceFileHandler(".", new String[]{"src/main/java"});

        // act
        String result = handler.getPathStringWithInclude("**/*Controller.java");

        // assert
        assertThat(result, either(is(".\\src\\main\\java")).or(is("./src/main/java")));
        assertTrue(new File(result).exists());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testForExceptionCheck() {
        // arrange
        SourceFileHandler handler = new SourceFileHandler(".", new String[]{"src/main/no"});

        // act // assert
        handler.getPathStringWithInclude("**/*Controller.java");
    }
}
