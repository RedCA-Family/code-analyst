package com.samsungsds.analyst.code.main;

import com.samsungsds.analyst.code.api.AnalysisMode;

public class IndividualMode extends AnalysisMode {

	public void setAll() {
		setCodeSize(true);
		setDuplication(true);
		setComplexity(true);
		setSonarJava(true);
		setPmd(true);
		setFindBugs(true);
		setFindSecBugs(true);
		setWebResource(true);
		setDependency(true);
		setUnusedCode(true);
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
		if (isWebResource()) {
			builder.append("WebResource, ");
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
