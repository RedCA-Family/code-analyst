package com.samsungsds.analyst.code.api.impl;

import com.samsungsds.analyst.code.api.AnalysisProgress;
import com.samsungsds.analyst.code.api.CodeAnalyst;
import com.samsungsds.analyst.code.api.ProgressObserver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class AbstractCodeAnalystImpl implements CodeAnalyst {
    protected List<ProgressObserver> observerList = new ArrayList<>();

    @Override
    public void addProgressObserver(ProgressObserver observer) {
        observerList.add(observer);
    }

    @Override
    public void deleteProgressObserver(ProgressObserver observer) {
        observerList.remove(observer);
    }

    protected String getUniqueId() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    protected String getArgumentsString(String[] arguments) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < arguments.length; i++) {
            if (i == 0) {
                builder.append(checkSpaceOrAsterisk(arguments[i]));
            } else {
                builder.append(" ").append(checkSpaceOrAsterisk(arguments[i]));
            }
        }

        return builder.toString();
    }

    protected String checkSpaceOrAsterisk(String string) {
        if (string.contains(" ") || string.contains("*")) {
            return "\"" + string + "\"";
        }

        return string;
    }

    protected String getOutputFile(String where, String ext) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = "result-" + format.format(new Date()) + "." + ext;

        File directory = new File(where);

        File resultFile = new File(directory, fileName);

        try {
            return resultFile.getCanonicalPath();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    protected void addAnalysisItem(StringBuilder parameter, String modeString) {
        if (parameter.length() != 0) {
            parameter.append(",");
        }
        parameter.append(modeString);
    }

    protected boolean isNotValidated(String str) {
        if (str == null || str.trim().equals("")) {
            return true;
        }

        return false;
    }

    protected boolean isValidated(String str) {
        if (str != null && !str.trim().equals("")) {
            return true;
        }

        return false;
    }
}
