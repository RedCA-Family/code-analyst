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
package com.samsungsds.analyst.code.ckmetrics;

import com.google.gson.annotations.Expose;
import com.samsungsds.analyst.code.util.CSVFileResult;

import java.io.Serializable;

public class CkMetricsResult implements Serializable, CSVFileResult {
    private static final long serialVersionUID = -7592888907648916235L;

    @Expose
    private String qualifiedClassName;

    /** Weighted methods per class */
    @Expose
    private int wmc;

    /** Number of children */
    @Expose
    private int noc;

    /** Response for a Class */
    @Expose
    private int rfc;

    /** Coupling between object classes */
    @Expose
    private int cbo;

    /** Depth of inheritance tree */
    @Expose
    private int dit;

    /** Lack of cohesion in methods */
    @Expose
    private int lcom;

    /** File Path */
    @Expose
    private String filePath;

    public CkMetricsResult() {
        // default constructor (CSV)
    }

    public CkMetricsResult(String qualifiedClassName, int wmc, int noc, int rfc, int cbo, int dit, int lcom, String filePath) {
        this.qualifiedClassName = qualifiedClassName;
        this.wmc = wmc;
        this.noc = noc;
        this.rfc = rfc;
        this.cbo = cbo;
        this.dit = dit;
        this.lcom = lcom;
        this.filePath = filePath;
    }

    @Override
    public int getColumnSize() {
        return 8;
    }

    @Override
    public String getDataIn(int columnIndex) {
        switch (columnIndex) {
            case 0 : return qualifiedClassName;
            case 1 : return String.valueOf(wmc);
            case 2 : return String.valueOf(noc);
            case 3 : return String.valueOf(rfc);
            case 4 : return String.valueOf(cbo);
            case 5 : return String.valueOf(dit);
            case 6 : return String.valueOf(lcom);
            case 7 : return filePath;
            default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
        }
    }

    @Override
    public void setDataIn(int columnIndex, String data) {
        switch (columnIndex) {
            case 0 : qualifiedClassName = data; break;
            case 1 : wmc = Integer.parseInt(data); break;
            case 2 : noc = Integer.parseInt(data); break;
            case 3 : rfc = Integer.parseInt(data); break;
            case 4 : cbo = Integer.parseInt(data); break;
            case 5 : dit = Integer.parseInt(data); break;
            case 6 : lcom = Integer.parseInt(data); break;
            case 7 : filePath = data; break;
            default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
        }
    }

    public String getQualifiedClassName() {
        return qualifiedClassName;
    }

    public int getWmc() {
        return wmc;
    }

    public int getNoc() {
        return noc;
    }

    public int getRfc() {
        return rfc;
    }

    public int getCbo() {
        return cbo;
    }

    public int getDit() {
        return dit;
    }

    public int getLcom() {
        return lcom;
    }

    public String getFilePath() {
        return filePath;
    }
}
