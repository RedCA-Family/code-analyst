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
		setCkMetrics(true);
		setCheckStyle(true);
		setSonarCSharp(true);
		setSonarPython(true);
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
		setCkMetrics(false);
		setCheckStyle(false);
		setSonarCSharp(false);
		setSonarPython(false);
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
		if (isSonarCSharp()) {
		    builder.append("SonarCSharp, ");
        }
        if (isSonarPython()) {
            builder.append("SonarPython, ");
        }
		if (isDependency()) {
			builder.append("Dependency, ");
		}
		if (isUnusedCode()) {
			builder.append("Unused Code, ");
		}
		if (isCkMetrics()) {
			builder.append("CK Metrics, ");
		}
        if (isCheckStyle()) {
            builder.append("CheckStyle, ");
        }

        if (builder.length() == 0) {
            return "*None*";
        }

		return builder.substring(0, builder.length() - 2);
	}
}
