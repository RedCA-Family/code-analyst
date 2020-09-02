package com.samsungsds.analyst.code.main;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class SourceFileHandlerTest {
    @Test
    public void testForWindowsStyle() {
        // arrange
        SourceFileHandler handler = new SourceFileHandler(".", new String[]{"src\\main\\java"});

        // act
        String result = handler.getPathStringWithInclude("**/*Controller.java");

        // assert
        assertThat(result, either(is(".\\src\\main\\java")).or(is("./src/main/java")));
        assertThat(new File(result).exists(), is(true));
    }

    @Test
    public void testForLinuxStyle() {
        // arrange
        SourceFileHandler handler = new SourceFileHandler(".", new String[]{"src/main/java"});

        // act
        String result = handler.getPathStringWithInclude("**/*Controller.java");

        // assert
        assertThat(result, either(is(".\\src\\main\\java")).or(is("./src/main/java")));
        assertThat(new File(result).exists(), is(true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testForExceptionCheck() {
        // arrange
        SourceFileHandler handler = new SourceFileHandler(".", new String[]{"src/main/no"});

        // act // assert
        handler.getPathStringWithInclude("**/*Controller.java");
    }
}
