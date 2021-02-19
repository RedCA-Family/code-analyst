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
package com.samsungsds.analyst.code.main.nodejs;

import com.google.common.io.Files;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.main.Version;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.os.ExecutableFinder;
import org.sonar.api.utils.ZipUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeRuntime {
    private static final Logger LOGGER = LogManager.getLogger(NodeRuntime.class);

    private static final String NODE_PROGRAM = "node";

    private static final boolean IS_WINDOWS = SystemUtils.IS_OS_WINDOWS;
    private static final boolean IS_MACOS = SystemUtils.IS_OS_MAC;
    private static final boolean IS_LINUX = SystemUtils.IS_OS_LINUX;

    private static final String NODE_PATH_FOR_WINDOWS = "node-v10.15.3-win-x86\\node.exe";
    private static final String NODE_PATH_FOR_MACOS = "node-v10.15.3-darwin-x64/bin/node";
    private static final String NODE_PATH_FOR_LINUX = "node-v10.15.3-linux-x64/bin/node";

    private static final Pattern NODE_VERSION_PATTERN = Pattern.compile("v?(\\d+)\\.(\\d+)\\.(\\d+)");

    private String path;
    private int majorVersion;
    private int minorVersion;
    private int patchVersion;

    public static String findNodeRuntimePath(String instanceKey) throws NodeRuntimeException {
        String nodeRuntimePath = MeasuredResult.getInstance(instanceKey).getNodeExecutablePath();

        if (nodeRuntimePath != null) {
            return nodeRuntimePath;
        }

        LOGGER.info("Find 'node' runtime...");
        // To perform analysis SonarJS requires Node.js >=6
        NodeRuntime nodeRuntime = new NodeRuntime(6);

        String path = nodeRuntime.getNodeExecutablePath();
        String version = nodeRuntime.getVersionString();

        LOGGER.info("Node({}) : {}", path, version);

        MeasuredResult.getInstance(instanceKey).setNodeExecutablePath(path);
        MeasuredResult.getInstance(instanceKey).setNodeVersion(version);

        return path;
    }

    public NodeRuntime(int requiredMajorVersion) throws NodeRuntimeException {
        try {
            if (!IS_WINDOWS && !IS_MACOS && !IS_LINUX) {
                throw new NodeRuntimeException("Current OS not supported...");
            }

            ExecutableFinder finder = new ExecutableFinder();
            String searchedPath = finder.find(NODE_PROGRAM);

            if (searchedPath == null) {
                path = installNodeRuntimeAndGetPath();
            } else {
                path = searchedPath;
            }

            checkNodeVersion();

            if (majorVersion < requiredMajorVersion) {
                LOGGER.warn("It's version({}) is lower than required version {}", majorVersion, requiredMajorVersion);

                path = installNodeRuntimeAndGetPath();
            }
        } catch (Throwable throwable) {
            LOGGER.error("Node Runtime SetUp Error", throwable);
            throw new NodeRuntimeException("Node Runtime SetUp Error", throwable);
        }
    }

    private void checkNodeVersion() throws NodeRuntimeException {
        ProcessBuilder builder;

        if (IS_MACOS || IS_LINUX) {
            builder = new ProcessBuilder("/bin/sh", "-c", path + " " + "-v");
        } else {
            builder = new ProcessBuilder(path, "-v");
        }

        try {
            Process proc = builder.start();

            int errCode = proc.waitFor();
            StringWriter writer = new StringWriter();
            if (errCode == 0) {
                IOUtils.copy(proc.getInputStream(), writer, Charset.defaultCharset());
                String versionString = writer.toString();

                Matcher matcher = NODE_VERSION_PATTERN.matcher(versionString);
                if (matcher.lookingAt()) {
                    majorVersion = Integer.parseInt(matcher.group(1));
                    minorVersion = Integer.parseInt(matcher.group(2));
                    patchVersion = Integer.parseInt(matcher.group(3));
                } else {
                    throw new NodeRuntimeException("Node version parsing error : " + versionString);
                }
            } else {
                IOUtils.copy(proc.getErrorStream(), writer, Charset.defaultCharset());

                throw new NodeRuntimeException(writer.toString());
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (InterruptedException ex) {
            throw new UncheckedExecutionException(ex);
        }
    }

    private String installNodeRuntimeAndGetPath() throws NodeRuntimeException {
        LOGGER.info("Saving Node runtime (version : {}) ...", Version.NODE_JS);

        try {
            if (IS_WINDOWS) {
                File zipFile = IOAndFileUtils.saveResourceFile("/statics/nodejs/node-v10.15.3-win-x86.zip", "node", ".zip");

                File dir = Files.createTempDir();
                ZipUtils.unzip(zipFile, dir);

                return dir + File.separator + NODE_PATH_FOR_WINDOWS;

            } else if (IS_MACOS) {
                File zipFile = IOAndFileUtils.saveResourceFile("/statics/nodejs/node-v10.15.3-darwin-x64.tar.gz", "node", ".tar.gz");

                File dir = Files.createTempDir();

                try (TarArchiveInputStream fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(zipFile)))) {
                    saveArchiveFile(fin, dir);
                }

                String path = dir + File.separator + NODE_PATH_FOR_MACOS;
                File nodeExecute = new File(path);
                nodeExecute.setExecutable(true);

                return path;
            } else if (IS_LINUX) {
                File zipFile = IOAndFileUtils.saveResourceFile("/statics/nodejs/node-v10.15.3-linux-x64.tar.xz", "node", ".tar.gz");

                File dir = Files.createTempDir();

                try (TarArchiveInputStream fin = new TarArchiveInputStream(new XZCompressorInputStream(new FileInputStream(zipFile)))) {
                    saveArchiveFile(fin, dir);
                }

                String path = dir + File.separator + NODE_PATH_FOR_LINUX;
                File nodeExecute = new File(path);
                nodeExecute.setExecutable(true);

                return path;
            } else {
                throw new NodeRuntimeException("Current OS not supported...");
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private void saveArchiveFile(TarArchiveInputStream fin, File dir) throws IOException {
        TarArchiveEntry entry;
        while ((entry = fin.getNextTarEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            File curfile = new File(dir, entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            IOUtils.copy(fin, new FileOutputStream(curfile));
        }
    }

    public String getNodeExecutablePath() {
        return path;
    }

    public String getVersionString() {
        return String.format("v%d.%d.%d", majorVersion, minorVersion, patchVersion);
    }
}
