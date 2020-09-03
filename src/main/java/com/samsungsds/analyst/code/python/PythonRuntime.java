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
package com.samsungsds.analyst.code.python;

import com.google.common.util.concurrent.UncheckedExecutionException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.os.ExecutableFinder;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonRuntime {
    private static final Logger LOGGER = LogManager.getLogger(PythonRuntime.class);

    private static final String PYTHON_PROGRAM = "python";

    private static final Pattern PYTHON_VERSION_PATTERN = Pattern.compile("Python ?(\\d+)\\.(\\d+)\\.(\\d+)");

    public static final boolean IS_WINDOWS = SystemUtils.IS_OS_WINDOWS;
    public static final boolean IS_MACOS = SystemUtils.IS_OS_MAC;
    public static final boolean IS_LINUX = SystemUtils.IS_OS_LINUX;

    private String path;
    private int majorVersion;
    private int minorVersion;
    private int patchVersion;

    public PythonRuntime() throws PythonRuntimeException {
        try {
            if (!IS_WINDOWS && !IS_MACOS && !IS_LINUX) {
                throw new PythonRuntimeException("Current OS not supported...");
            }

            ExecutableFinder finder = new ExecutableFinder();
            path = finder.find(PYTHON_PROGRAM);

            if (path == null) {
                throw new PythonRuntimeException("There is no python runtime.");
            }

            checkPythonVersion();
        } catch (Throwable throwable) {
            if (throwable instanceof PythonRuntimeException) {
                throw throwable;
            } else {
                throw new PythonRuntimeException("Python Runtime Check Error", throwable);
            }
        }
    }

    private void checkPythonVersion() throws PythonRuntimeException {
        ProcessBuilder builder;

        if (IS_MACOS || IS_LINUX) {
            builder = new ProcessBuilder("/bin/sh", "-c", path + " " + "--version");
        } else {
            builder = new ProcessBuilder(path, "--version");
        }

        try {
            Process proc = builder.start();

            int errCode = proc.waitFor();
            if (errCode == 0) {
                StringWriter writer = new StringWriter();
                IOUtils.copy(proc.getInputStream(), writer, Charset.defaultCharset());
                String versionString = writer.toString();

                Matcher matcher = PYTHON_VERSION_PATTERN.matcher(versionString);
                if (matcher.lookingAt()) {
                    majorVersion = Integer.parseInt(matcher.group(1));
                    minorVersion = Integer.parseInt(matcher.group(2));
                    patchVersion = Integer.parseInt(matcher.group(3));
                } else {
                    throw new PythonRuntimeException("Python version parsing error : " + versionString);
                }
            } else {
                StringWriter writer = new StringWriter();
                IOUtils.copy(proc.getErrorStream(), writer, Charset.defaultCharset());

                throw new PythonRuntimeException(writer.toString());
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (InterruptedException ex) {
            throw new UncheckedExecutionException(ex);
        }
    }

    public String getPythonExecutablePath() {
        return path;
    }

    public String getVersionString() {
        return String.format("v%d.%d.%d", majorVersion, minorVersion, patchVersion);
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }
}
