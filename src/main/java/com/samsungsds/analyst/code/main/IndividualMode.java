package com.samsungsds.analyst.code.main;

import com.samsungsds.analyst.code.api.AnalysisMode;

public class IndividualMode extends AnalysisMode {

	public void setDefault() {
		setCodeSize(true);
		setDuplication(true);
		setComplexity(true);
		setSonarJava(true);
		setPmd(true);
		setFindBugs(true);
		setFindSecBugs(true);
		setJavascript(true);
		setCss(false);			// disabled by default
		setHtml(false);			// disabled by default
		setDependency(true);
		setUnusedCode(true);
	}

	public void setUnsetAll() {
		setCodeSize(false);
		setDuplication(false);
		setComplexity(false);
		setSonarJava(false);
		setPmd(false);
		setFindBugs(false);
		setFindSecBugs(false);
		setJavascript(false);
		setCss(false);
		setHtml(false);
		setDependency(false);
		setUnusedCode(false);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (isCodeSize()) {
			builder.append("Code-Size, ");
		}
		if (isDuplication()) {
			builder.append("Duplication, ");
		}
		if (isComplexity()) {
			builder.append("Complexity, ");
		}
		if (isSonarJava()) {
			builder.append("SonarJava, ");
		}
		if (isPmd()) {
			builder.append("PMD, ");
		}
		if (isFindBugs()) {
			builder.append("FindBugs, ");
		}
		if (isFindSecBugs()) {
			builder.append("FindSecBugs, ");
		}
		if (isJavascript()) {
			builder.append("JavaScript, ");
		}
		if (isCss()) {
			builder.append("CSS, ");
		}
		if (isHtml()) {
			builder.append("HTML, ");
		}
		if (isDependency()) {
			builder.append("Dependency, ");
		}
		if (isUnusedCode()) {
			builder.append("Unused Code, ");
		}

		return builder.substring(0, builder.length() - 2);
	}
}
