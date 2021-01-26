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
package com.samsungsds.analyst.code.python.radon;

import com.google.common.io.Files;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.pmd.ComplexityAnalysis;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.python.PythonRuntime;
import com.samsungsds.analyst.code.python.PythonRuntimeException;
import com.samsungsds.analyst.code.python.radon.result.RadonComplexityDeserializer;
import com.samsungsds.analyst.code.python.radon.result.RadonComplexityItem;
import com.samsungsds.analyst.code.util.FindFileUtils;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sonar.api.utils.ZipUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RadonAnalysisLauncher implements ComplexityAnalysis {
    private static final Logger LOGGER = LogManager.getLogger(RadonAnalysisLauncher.class);

    private static final String PYTHON_RADON_PACKAGES = "/statics/python/radon.zip";
    private static final String WHEEL_PACKAGES = "/statics/python/wheel.zip";

    private String projectPath = null;
    private String exclude = null;
    private String src = null;

    @Override
    public void addOption(String option, String value) {
        if (option.equals("path")) {
            String path = new File(value).getAbsolutePath();
            projectPath = path;
        } else if (option.equals("exclude")) {
            exclude = value;
        } else if (option.equals("src")) {
            src = value;
        }
    }

    @Override
    public void run(String instanceKey) {
        String python = checkPythonVersionAndGetPath();

        File tmpDir = saveRadonPackageWheels();

        String virtualEnvDir = makeVirtualEnv(python, tmpDir);

        installWheelPackages(tmpDir.getPath());

        installRadonPackages(tmpDir.getPath());

        String resultJsonPath = runRadon(virtualEnvDir);

        LOGGER.debug("radon result : {}", resultJsonPath);
        processResult(resultJsonPath, instanceKey);
    }

    private String checkPythonVersionAndGetPath() {
        try {
            LOGGER.info("Find 'python' runtime...");
            PythonRuntime pythonRuntime = new PythonRuntime();

            String path = pythonRuntime.getPythonExecutablePath();
            String version = pythonRuntime.getVersionString();

            LOGGER.info("Python({}) : {}", path, version);

            if (pythonRuntime.getMajorVersion() == 3 &&
                (pythonRuntime.getMinorVersion() <= 3 || pythonRuntime.getMinorVersion() > 7)) {
                throw new PythonRuntimeException("Radon will run from Python from Python 3.4 to Python 3.7");
            }

            return path;
        } catch (PythonRuntimeException ex) {
            throw new RuntimeException(ex);
        }
    }

    private File saveRadonPackageWheels() {
        LOGGER.info("Saving Radon packages for install");

        File zipFile = IOAndFileUtils.saveResourceFile(PYTHON_RADON_PACKAGES, "radon", ".zip");

        File dir = Files.createTempDir();

        try {
            ZipUtils.unzip(zipFile, dir);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        return dir;
    }

    private String makeVirtualEnv(String python, File tmpDir) {
        LOGGER.info("making virtual environment for radon");

        ProcessBuilder builder;

        if (PythonRuntime.IS_MACOS || PythonRuntime.IS_LINUX) {
            builder = new ProcessBuilder("/bin/sh", "-c", python + " -m venv radon");
        } else {
            builder = new ProcessBuilder(python, "-m", "venv", "radon");
        }

        builder.directory(tmpDir);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            Process proc = builder.start();
            int errCode = proc.waitFor();
            if (errCode != 0) {
                throw new RuntimeException("Python Make Virtual Environment Error");
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (InterruptedException ex) {
            throw new UncheckedExecutionException(ex);
        }

        File radon = new File(tmpDir, "radon");

        return radon.getPath();
    }

    private void installWheelPackages(String virtualEnvDir) {
        LOGGER.info("Install wheel package");

        File wheel = IOAndFileUtils.saveResourceFile(WHEEL_PACKAGES, "wheel", ".zip");

        File wheelDir = Files.createTempDir();
        try {
            ZipUtils.unzip(wheel, wheelDir);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        String wheelFile = wheelDir + File.separator + "wheel-0.36.2-py2.py3-none-any.whl";

        String pip;
        ProcessBuilder builder;

        if (PythonRuntime.IS_MACOS || PythonRuntime.IS_LINUX) {
            pip = virtualEnvDir + File.separator + "radon" + File.separator + "bin" + File.separator + "pip";

            builder = new ProcessBuilder("/bin/sh", "-c", pip + " install --find-links . --no-index " + wheelFile);
        } else {
            pip = virtualEnvDir + File.separator + "radon" + File.separator + "Scripts" + File.separator + "pip.exe";

            builder = new ProcessBuilder(pip, "install", "--find-links", ".", "--no-index", wheelFile);
        }

        // "venv activate"
        // -> add Scripts path to PATH environment
        // -> set VIRTUAL_ENV environment
        builder.environment().put("VIRTUAL_ENV", virtualEnvDir);

        builder.directory(new File(virtualEnvDir));
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            Process proc = builder.start();
            int errCode = proc.waitFor();
            if (errCode != 0) {
                throw new RuntimeException("Python Install Wheel Packages Error");
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (InterruptedException ex) {
            throw new UncheckedExecutionException(ex);
        }
    }

    private void installRadonPackages(String virtualEnvDir) {
        LOGGER.info("Install radon");

        String pip;
        ProcessBuilder builder;

        if (PythonRuntime.IS_MACOS || PythonRuntime.IS_LINUX) {
            pip = virtualEnvDir + File.separator + "radon" + File.separator + "bin" + File.separator + "pip";

            builder = new ProcessBuilder("/bin/sh", "-c", pip + " install --find-links . --no-index radon-3.0.3-py2.py3-none-any.whl");
        } else {
            pip = virtualEnvDir + File.separator + "radon" + File.separator + "Scripts" + File.separator + "pip.exe";

            builder = new ProcessBuilder(pip, "install", "--find-links", ".", "--no-index", "radon-3.0.3-py2.py3-none-any.whl");
        }

        // "venv activate"
        // -> add Scripts path to PATH environment
        // -> set VIRTUAL_ENV environment
        builder.environment().put("VIRTUAL_ENV", virtualEnvDir);

        builder.directory(new File(virtualEnvDir));
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            Process proc = builder.start();
            int errCode = proc.waitFor();
            if (errCode != 0) {
                throw new RuntimeException("Python Install Radon Packages Error");
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (InterruptedException ex) {
            throw new UncheckedExecutionException(ex);
        }
    }

    private String runRadon(String virtualEnvDir) {
        LOGGER.info("Run radon ...");

        String radon;
        if (PythonRuntime.IS_MACOS || PythonRuntime.IS_LINUX) {
            radon = virtualEnvDir + File.separator + "bin" + File.separator + "radon";
        } else {
            radon = virtualEnvDir + File.separator + "Scripts" + File.separator + "radon.exe";
        }

        File json = new File(virtualEnvDir, "result.json");

        String[] srcPaths = src.split(FindFileUtils.COMMA_SPLITTER);
        StringBuilder pathWithQuotation = new StringBuilder();
        for (String path : srcPaths) {
            pathWithQuotation.append("\"").append(projectPath).append(File.separator).append(path).append("\"").append(" ");
        }

        ProcessBuilder builder;

        if (PythonRuntime.IS_MACOS || PythonRuntime.IS_LINUX) {
            builder = new ProcessBuilder("/bin/sh", "-c", radon + " cc -e \"" + exclude + "\" --no-assert --json " + pathWithQuotation.toString().trim());
        } else {
            List<String> pathList = new ArrayList<>();
            pathList.add(radon);
            pathList.add("cc");
            pathList.add("-e");
            pathList.add(exclude);
            pathList.add("--no-assert");
            pathList.add("--json");
            for (String path : srcPaths) {
                pathList.add(projectPath + File.separator + path);
            }
            builder = new ProcessBuilder(pathList);
        }

        // "venv activate"
        // -> add Scripts path to PATH environment
        // -> set VIRTUAL_ENV environment
        builder.environment().put("VIRTUAL_ENV", virtualEnvDir);

        builder.directory(new File(virtualEnvDir));
        builder.redirectOutput(ProcessBuilder.Redirect.to(json));
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            Process proc = builder.start();
            int errCode = proc.waitFor();
            if (errCode != 0) {
                throw new RuntimeException("Python Install Radon Packages Error");
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (InterruptedException ex) {
            throw new UncheckedExecutionException(ex);
        }

        return json.getPath();
    }

    private void processResult(String resultJsonPath, String instanceKey) {
        //Gson gson = new Gson();
        final GsonBuilder builder = new GsonBuilder();
        Type listType = new TypeToken<Map<String, List<RadonComplexityItem>>>() {
        }.getType();
        RadonComplexityDeserializer deserializer = new RadonComplexityDeserializer();

        builder.registerTypeAdapter(listType, deserializer);
        final Gson gson = builder.create();

        Map<String, List<RadonComplexityItem>> resultMap = null;
        try (Reader file = new FileReader(resultJsonPath)) {
            resultMap = gson.fromJson(file, listType);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        List<ComplexityResult> list = new ArrayList<>();
        resultMap.forEach((path, complexityItemList) -> {
            for (RadonComplexityItem item : complexityItemList) {
                if (item.getMethods() != null && !item.getMethods().isEmpty()) {
                    for (RadonComplexityItem subItem : item.getMethods()) {
                        if (subItem.getType().equalsIgnoreCase("method") || subItem.getType().equalsIgnoreCase("function")) {
                            list.add(new ComplexityResult(MeasuredResult.getConvertedFilePath(path, projectPath), subItem.getLineno(), subItem.getName(), subItem.getComplexity()));
                        }
                    }
                } else if (item.getType().equalsIgnoreCase("method") || item.getType().equalsIgnoreCase("function")) {
                    list.add(new ComplexityResult(MeasuredResult.getConvertedFilePath(path, projectPath), item.getLineno(), item.getName(), item.getComplexity()));
                }
            }
        });

        MeasuredResult.getInstance(instanceKey).putComplexityList(list);
    }
}
