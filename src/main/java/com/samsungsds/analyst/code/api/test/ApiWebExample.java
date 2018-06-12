package com.samsungsds.analyst.code.api.test;

import com.samsungsds.analyst.code.api.*;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

public class ApiWebExample {
    private static final String TEMP_DIRECTORY = "C:\\Temp";

    private static final NumberFormat numberFormatter = NumberFormat.getInstance();

    public static void main(String[] args) {
        CodeAnalyst analyst = CodeAnalystFactory.create();

        analyst.addProgressObserver(progress -> {
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            System.out.print("Event : " + progress.getProgressEvent() + ", Current : " + progress.getCompletedPercent() + "%");
            System.out.println(", " + numberFormatter.format(progress.getElapsedTimeInMillisecond()) + " elapsed ms");
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
        });

        WebArgumentInfo argument = new WebArgumentInfo();
        try {
            argument.setProject(new File(".").getCanonicalPath());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        argument.setEncoding("UTF-8"); // default

        argument.setWebapp("src/main/webapp");

        // argument.setDebug(true);

        // argument.setExclude("**/test.js");

        argument.setSaveCatalog(true);

        WebTargetFileInfo targetFile = new WebTargetFileInfo();

        targetFile.addFile("js/test.js");

        File temp = new File(TEMP_DIRECTORY);
        if (!temp.exists()) {
            temp.mkdirs();
        }

        String resultFile = analyst.analyzeWebResource(TEMP_DIRECTORY, argument, targetFile, false);

        System.out.println("Result File : " + resultFile);

    }
}
