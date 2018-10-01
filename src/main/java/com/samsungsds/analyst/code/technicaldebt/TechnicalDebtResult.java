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
package com.samsungsds.analyst.code.technicaldebt;

import java.io.Serializable;
import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.Expose;

public class TechnicalDebtResult implements Serializable {

	private static final long serialVersionUID = -1903833121825373419L;

	private static final Logger LOGGER = LogManager.getLogger(TechnicalDebtResult.class);

	@Expose
	private double totalDebt;

	@Expose
	private double duplicationDebt;

	@Expose
	private double violationDebt;

	@Expose
	private double complexityDebt;

	@Expose
	private double acyclicDependencyDebt;

	public TechnicalDebtResult(double duplicationDebt, double violationDebt, double complexityDebt, double acyclicDependencyDebt) {
		this.duplicationDebt = roundDecimal(duplicationDebt);
		this.violationDebt = roundDecimal(violationDebt);
		this.complexityDebt = roundDecimal(complexityDebt);
		this.acyclicDependencyDebt = roundDecimal(acyclicDependencyDebt);
		calculateTotalDebt();
	}

	private void calculateTotalDebt() {
		totalDebt += duplicationDebt;
		totalDebt += violationDebt;
		totalDebt += complexityDebt;
		totalDebt += acyclicDependencyDebt;
		totalDebt = roundDecimal(totalDebt);
		LOGGER.info("TechnicalDebt(total): " + totalDebt);
	}

	private double roundDecimal(double decimal) {
		DecimalFormat decimalFormat = new DecimalFormat("0.##");
		return Double.parseDouble(decimalFormat.format(decimal));
	}

	public double getTotalDebt() {
		return totalDebt;
	}

	public void setTotalDebt(double totalDebt) {
		this.totalDebt = totalDebt;
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
