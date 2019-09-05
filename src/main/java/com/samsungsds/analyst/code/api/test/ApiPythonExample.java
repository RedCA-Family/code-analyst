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
package com.samsungsds.analyst.code.api.test;

import com.samsungsds.analyst.code.api.*;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

public class ApiPythonExample {
    private static final String TEMP_DIRECTORY = "C:\\Temp";

    private static final NumberFormat numberFormatter = NumberFormat.getInstance();

    public static void main(String[] args) {
        CodeAnalyst analyst = CodeAnalystFactory.create(Language.PYTHON);

        analyst.addProgressObserver(progress -> {
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            System.out.print("Event : " + progress.getProgressEvent() + ", Current : " + progress.getCompletedPercent() + "%");
            System.out.println(", " + numberFormatter.format(progress.getElapsedTimeInMillisecond()) + " elapsed ms");
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
        });

        ArgumentInfo argument = new ArgumentInfo();
        try {
            argument.setProject(new File("D:\\Repositories\\GitHub\\python-tutorial").getCanonicalPath());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        //argument.setEncoding("UTF-8"); // default

        argument.setSrc(".");

        //argument.setDebug(true);

        AnalysisMode mode = new AnalysisMode();
        mode.setCodeSize(true);
        mode.setDuplication(true);
        mode.setComplexity(true);
        mode.setSonarPython(true);

        //System.setProperty("print.path.filter", "true");

        argument.setMode(mode);

        TargetFileInfo targetFile = new TargetFileInfo();

        File temp = new File(TEMP_DIRECTORY);
        if (!temp.exists()) {
            temp.mkdirs();
        }

        String resultFile = analyst.analyze(TEMP_DIRECTORY, argument, targetFile);

        System.out.println("Result File : " + resultFile);
    }
}
