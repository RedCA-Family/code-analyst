package com.samsungsds.analyst.code.technicaldebt;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.samsungsds.analyst.code.util.CSVFileResult;

public class TechnicalDebtResult implements Serializable, CSVFileResult {

	private static final long serialVersionUID = -1903833121825373419L;

	@Expose
	private double technicalDebt;

	@Expose
	private double duplicationDebt;

	@Expose
	private double violationDebt;

	@Expose
	private double complexityDebt;

	@Expose
	private double acyclicDependencyDebt;

	public TechnicalDebtResult() {
		// default constructor (CSV)
		// column : technicalDebt, duplicationDebt, violationDebt, complexityDebt, acyclicDependencyDebt
		technicalDebt = 0;
		duplicationDebt = 0;
		violationDebt = 0;
		complexityDebt = 0;
		acyclicDependencyDebt = 0;
	}

	@Override
	public int getColumnSize() {
		return 5;
	}

	@Override
	public String getDataIn(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.valueOf(technicalDebt);
		case 1:
			return String.valueOf(duplicationDebt);
		case 2:
			return String.valueOf(violationDebt);
		case 3:
			return String.valueOf(complexityDebt);
		case 4:
			return String.valueOf(acyclicDependencyDebt);
		default:
			throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	@Override
	public void setDataIn(int columnIndex, String data) {
		switch (columnIndex) {
		case 0:
			technicalDebt = Double.parseDouble(data);
			break;
		case 1:
			duplicationDebt = Double.parseDouble(data);
			break;
		case 2:
			violationDebt = Double.parseDouble(data);
			break;
		case 3:
			complexityDebt = Double.parseDouble(data);
			break;
		case 4:
			acyclicDependencyDebt = Double.parseDouble(data);
			break;
		default:
			throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	public TechnicalDebtResult(double technicalDebt, double duplicationDebt, double violationDebt, double complexityDebt, double acyclicDependencyDebt) {
		this.technicalDebt = technicalDebt;
		this.duplicationDebt = duplicationDebt;
		this.violationDebt = violationDebt;
		this.complexityDebt = complexityDebt;
		this.acyclicDependencyDebt = acyclicDependencyDebt;
	}

	public double getTechnicalDebt() {
		return technicalDebt;
	}

	public void setTechnicalDebt(double technicalDebt) {
		this.technicalDebt = technicalDebt;
	}

	public double getDuplicationDebt() {
		return duplicationDebt;
	}

	public void setDuplicationDebt(double duplicationDebt) {
		this.duplicationDebt = duplicationDebt;
	}

	public double getViolationDebt() {
		return violationDebt;
	}

	public void setViolationDebt(double violationDebt) {
		this.violationDebt = violationDebt;
	}

	public double getComplexityDebt() {
		return complexityDebt;
	}

	public void setComplexityDebt(double complexityDebt) {
		this.complexityDebt = complexityDebt;
	}

	public double getAcyclicDependencyDebt() {
		return acyclicDependencyDebt;
	}

	public void setAcyclicDependencyDebt(double acyclicDependencyDebt) {
		this.acyclicDependencyDebt = acyclicDependencyDebt;
	}

}
