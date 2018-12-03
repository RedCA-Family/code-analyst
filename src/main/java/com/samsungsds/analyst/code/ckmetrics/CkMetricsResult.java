package com.samsungsds.analyst.code.ckmetrics;

import com.google.gson.annotations.Expose;
import com.samsungsds.analyst.code.ckmetrics.library.CKNumber;
import com.samsungsds.analyst.code.util.CSVFileResult;

import java.io.Serializable;

public class CkMetricsResult implements Serializable, CSVFileResult {

    private static final long serialVersionUID = 6242072274980032300L;

    @Expose
    private String file;
    @Expose
    private String className;
    @Expose
    private String type;

    @Expose
    private int dit;
    @Expose
    private int noc;
    @Expose
    private int wmc;
    @Expose
    private int cbo;
    @Expose
    private int lcom;
    @Expose
    private int rfc;
    @Expose
    private int nom;
    @Expose
    private int nopm;
    @Expose
    private int nosm;

    @Expose
    private int nof;
    @Expose
    private int nopf;
    @Expose
    private int nosf;

    @Expose
    private int nosi;
    @Expose
    private int loc;

    public CkMetricsResult() {
        // default constructor (CSV)
    }

    public CkMetricsResult(CKNumber ckNumber) {
        file = ckNumber.getFile();
        className = ckNumber.getClassName();
        type = ckNumber.getType();

        dit = ckNumber.getDit();
        noc = ckNumber.getNoc();
        wmc = ckNumber.getWmc();
        cbo = ckNumber.getCbo();
        lcom = ckNumber.getLcom();
        rfc = ckNumber.getRfc();
        nom = ckNumber.getNom();
        nopm = ckNumber.getNopm();
        nosm = ckNumber.getNosm();

        nof = ckNumber.getNof();
        nopf = ckNumber.getNopf();
        nosf = ckNumber.getNosf();

        nosi = ckNumber.getNosi();
        loc = ckNumber.getLoc();
    }

    @Override
    public int getColumnSize() {
        return 17;
    }

    @Override
    public String getDataIn(int columnIndex) {
        switch (columnIndex) {
            case 0 : return file;
            case 1 : return className;
            case 2 : return type;
            case 3 : return String.valueOf(dit);
            case 4 : return String.valueOf(noc);
            case 5 : return String.valueOf(wmc);
            case 6 : return String.valueOf(cbo);
            case 7 : return String.valueOf(lcom);
            case 8 : return String.valueOf(rfc);
            case 9 : return String.valueOf(nom);
            case 10 : return String.valueOf(nopm);
            case 11 : return String.valueOf(nosm);
            case 12 : return String.valueOf(nof);
            case 13 : return String.valueOf(nopf);
            case 14 : return String.valueOf(nosf);
            case 15 : return String.valueOf(nosi);
            case 16 : return String.valueOf(loc);
            default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
        }
    }

    @Override
    public void setDataIn(int columnIndex, String data) {
        switch (columnIndex) {
            case 0 : file = data; break;
            case 1 : className = data; break;
            case 2 : type = data; break;
            case 3 : dit = Integer.parseInt(data); break;
            case 4 : noc = Integer.parseInt(data); break;
            case 5 : wmc = Integer.parseInt(data); break;
            case 6 : cbo = Integer.parseInt(data); break;
            case 7 : lcom = Integer.parseInt(data); break;
            case 8 : rfc = Integer.parseInt(data); break;
            case 9 : nom = Integer.parseInt(data); break;
            case 10 : nopm = Integer.parseInt(data); break;
            case 11 : nosm = Integer.parseInt(data); break;
            case 12 : nof = Integer.parseInt(data); break;
            case 13 : nopf = Integer.parseInt(data); break;
            case 14 : nosf = Integer.parseInt(data); break;
            case 15 : nosi = Integer.parseInt(data); break;
            case 16 : loc = Integer.parseInt(data); break;
            default : throw new IndexOutOfBoundsException("Index: " + columnIndex);
        }
    }

    public void replaceFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public String getClassName() {
        return className;
    }

    public String getType() {
        return type;
    }

    public int getDit() {
        return dit;
    }

    public int getNoc() {
        return noc;
    }

    public int getWmc() {
        return wmc;
    }

    public int getCbo() {
        return cbo;
    }

    public int getLcom() {
        return lcom;
    }

    public int getRfc() {
        return rfc;
    }

    public int getNom() {
        return nom;
    }

    public int getNopm() {
        return nopm;
    }

    public int getNosm() {
        return nosm;
    }

    public int getNof() {
        return nof;
    }

    public int getNopf() {
        return nopf;
    }

    public int getNosf() {
        return nosf;
    }

    public int getNosi() {
        return nosi;
    }

    public int getLoc() {
        return loc;
    }
}
