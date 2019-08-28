package com.samsungsds.analyst.code.checkstyle;

import com.google.gson.annotations.Expose;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.util.CSVFileResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;

public class CheckStyleResult implements Serializable, CSVFileResult {
    private static final Logger LOGGER = LogManager.getLogger(CheckStyleResult.class);

    @Expose
    private String path;
    @Expose
    private int line;
    @Expose
    private String severity;
    @Expose
    private String message;
    @Expose
    private String checker;

    public CheckStyleResult() {
        // default constructor (CSV)
        // column : path, line, severity, message, checker
        path = "";
        line = 0;
        severity = "";
        message = "";
        checker = "";
    }

    @Override
    public int getColumnSize() {
        return 5;
    }

    @Override
    public String getDataIn(int columnIndex) {
        switch (columnIndex) {
            case 0 : return path;
            case 1 : return String.valueOf(line);
            case 2 : return severity;
            case 3 : return message;
            case 4 : return checker;
            default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
        }
    }

    @Override
    public void setDataIn(int columnIndex, String data) {
        switch (columnIndex) {
            case 0 : path = data; break;
            case 1 : line = Integer.parseInt(data); break;
            case 2 : severity = data; break;
            case 3 : message = data; break;
            case 4 : checker = data; break;
            default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
        }
    }

    public CheckStyleResult(String path, String line, String severity, String message, String checker, String instanceKey) {
        MeasuredResult measuredResult = MeasuredResult.getInstance(instanceKey);

        File filePath = new File(path);
        try {
            this.path = measuredResult.getConvertedFilePath(filePath.getCanonicalPath(), measuredResult.getProjectDirectory());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        this.line = Integer.parseInt(line);
        this.severity = severity;
        this.message = message;
        this.checker = checker;
    }

    public String getPath() {
        return path;
    }

    public int getLine() {
        return line;
    }

    public String getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public String getChecker() {
        return checker;
    }
}
