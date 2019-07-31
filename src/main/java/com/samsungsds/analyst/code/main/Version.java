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

public class Version {
	//--------------------------------------------------
	// Changelog
	// 1.0.0 : 초기 버전
	// 1.1.0 : 개별 Class에 대한 Complexity 측정 기능 추가
	// 1.1.1 : 복잡도 50 이상 표시, 중복 lines 계산 오류 수정
	// 1.2.0 : File path filter 적용, JSON 출력 추가
	// 1.3.0 : FindSecBugs 점검 추가, 각 점검 개별 호출 옵션 지원, 각종 오류 수정
	// 1.3.1 : Complexity Mode 상에 출력 방식 변경 (source link 지원)
	// 1.4.0 : API 추가
	// 1.4.1 : 다중 호출 시 문제 보완
	// 1.4.2 : API CodeAnalyst Factory 추가
	// 1.4.3 : CSV 형식 문자 처리 (목록 부분), 여러 instance 호출 가능
	// 1.4.4 : CSV Excel 형식 지원 ("," 뒤 공백 제거), Mac 임시 디렉토리 생성 권한 오류 조치,
	//         default package class를 갖는 경우 Warning 표시
	// 1.5.0 : 세부 분석 옵션 추가, MeasuredResult instance remove 호출 (GC), 복잡도 목록 대상 변경 (10 -> 20),
	//         기존 out 파일 재실행 기능 추가, 결과 파일 분리 옵션 추가 (API 포함)
	// 1.5.1 : separated mode가 아닌 경우에도 분리된 json 저장 오류 수정
	// 1.5.2 : 복잡도 Top 10 분석 개선
	// 1.5.3 : Martin metrics 측정 추가, Inspection(PMD, FindBugs)에 대한 Top 10 측정 추가, Duplication Top 10 수정
	// 1.5.4 : API 변경 (세부 분석 옵션 추가)
	// 2.0.0 : SonarQube Upgrade (6.2.1 -> 6.7.1 LTS), 신규 기능(unused code detection, technical debt, Web Resource 점검), '18년 3월 표준룰 개정 반영,
	//         API 부분 패턴 fixed 처리 추가(default package 클래스 처리), Issue Type 정부 추가 (Bug, Vulnerability, Code Smell) 등
	// 2.0.1 : Unused 오류 수정 및 Progress Event 개선
	// 2.1.0 : Top 10 Issue 구분(Bug, ...) 추가, SonarJava Issue filter(API 포함), 속도 개선(PMD 점검 대상 제한 등),
	//         점검 Rule 수 표시, 점검된 대상 파일 list 표시
	// 2.1.1 : FindSecBugs plugin 이중 로딩 오류 수정
	// 2.2.0 : mode 옵션에 제외("-") 지정 기능, Web Resource 점검 분리, src/binary multi 지정, SonarJS SDS 표준 반영, SonarQube (6.7.1 -> 6.7.4) 변경,
	//         전체 분석 시간 기록, SonarQube plugin 다운로드 세분화 (속도 개선), 순환참조 중복 제거 등
	// 2.3.0 : FindSecBugs SDS 표준 반영, JDepend mult directory 지정 오류 수정, API include 추가 (SonarQube Plugin에서 필요)
	// 2.3.1 : SonarQube Plugin에서 호출 시 ".sonar" 삭제 문제 해결 (".sonar" 디렉토리 변경), Unused Path 형식 오류 수정
	// 2.3.2 : localhost -> 127.0.0.1 변경 (일부 보안 시스템에 의해 차단되는 경우 발생), src 경로 중복 확인 오류 수정
	// 2.4.0 : OSS(Open Source Software) Version release
	// 2.5.0 : CK Metrics 추가
	// 2.5.1 : CK Metrics 처리 방식 변경 (eclipse JDT Parser -> BCEL 사용 CKJM)
	// 2.6.0 : Duplication 점검 방식 추가 (token 기반), PMD 버전 번경 (5.X 최종 적용), Unused Code CSV output 오류 수정
	// 2.6.1 : source 경로 오류('/' 또는 '\'를 prefix로 갖는 경우), API Progress Event 미완료 오류 수정
	// 2.7.0 : SonarJS plugin upgrade(4.1.0 -> 5.1.1, ruleset 조정 포함), Node 실행환경 제공 (Sonar 분석 부분 분리),
	//         Language 옵션 및 JavaScript language(Node.js) 추가
    // 2.8.0 : CheckStyle 적용, 소스 디렉토리 중복 체크 오류 수정, CKJM bcel lambda 함수 관련 오류 수정, include/exclude @file 처리 기능 추가
    // --------------------------------------------------
	public static final String CODE_ANALYST = "2.8.0";
	public static final String DOCUMENT_VERSION = "2.8";
	public static final String APPLICATION_JAR = "Code-Analyst-" + CODE_ANALYST + ".jar";

	public static final String SONAR_SCANNER = "2.10.0.1189";
	public static final String SONAR_SERVER = "6.7.4";
	public static final String PMD = "5.8.1";
	public static final String FINDBUGS = "3.0.1";
	public static final String FINDSECBUGS = "1.7.1";
	public static final String JDEPEND = "2.9.1";
	public static final String CKJM = "1.9";

	public static final String NODE_JS = "10.15.3 LTS";
	public static final String ESLINT = "5.16.0";

	public static final String SONAR_JAVA = "5.1.1.13214";
	public static final String SONAR_JS = "5.1.1.7506";
	public static final String SONAR_CSS = "3.1";
	public static final String SONAR_WEB = "2.5.0.476";

	public static final String CHECKSTYLE = "8.21";

	public static final String PMD_RULESET = "91 ruleset (v5.4, RedCA Way Ruleset, '18.03)";
	public static final String FINDBUGS_RULESET = "214 ruleset (v3.0.1, RedCA Way Ruleset, '18.03)";
	public static final String FINDSECBUGS_RULESET = "81 rules (v1.7.1, RedCA Way Ruleset, '18.06)";
	public static final String SONAR_JAVA_RULESET = "227 ruleset (v4.15, RedCA Way Ruleset, '18.03)";
	public static final String SONAR_JS_RULESET = "95 ruleset (v5.0, RedCA Way Ruleset, '18.11)";
	public static final String SONAR_CSS_RULESET = "CSS 71 / Less 71 / SCSS 82 ruleset (v3.1)";
	public static final String SONAR_WEB_RULESET = "16 ruleset (v2.5)";
	public static final String CHECKSTYLE_RULESET = "58 ruleset (RedCA Way Ruleset, '19.06)";

	public static final int SONAR_JAVA_DEFAULT_RULES = 227;
	public static final int PMD_DEFAULT_RULES = 91;
	public static final int FINDBUGS_DEFAULT_RULES = 214;
	public static final int FINDSECBUGS_DEFAULT_RULES = 81;
	public static final int SONAR_JS_DEFAULT_RULES = 95;

	public static void printVersionInfo() {
		System.out.println("Code Analyst : " + CODE_ANALYST);
		System.out.println("  - Sonar Scanner : " + SONAR_SCANNER + " (LGPL v3.0)");
		System.out.println("  - Sonar Server : " + SONAR_SERVER + " (LGPL v3.0)");
		System.out.println("     [Plugins]");
		System.out.println("       - SonarJava : " + SONAR_JAVA + " (LGPL v3.0)");
		System.out.println("       - SonarJS : " + SONAR_JS + " (LGPL v3.0)");
		System.out.println("       - CSS/SCSS/Less : " + SONAR_CSS + " (LGPL v3.0)");
		System.out.println("       - Web : " + SONAR_WEB + " (Apache v2.0)");
		System.out.println("  - PMD : " + PMD + " (BSD-style)");
		System.out.println("  - FindBugs : " + FINDBUGS + " (LGPL v3.0)");
		System.out.println("  - FindSecBugs : " + FINDSECBUGS + " (LGPL v3.0)");
		System.out.println("  - JDepend : " + JDEPEND +"-based modification" + " (BSD-style)");
		System.out.println("  - CKJM : " + CKJM + "-based modification" + " (Apache v2.0)");
		System.out.println("  - Node.js : " + NODE_JS + " (MIT)");
		System.out.println("    ※ Supported Platform : Windows/MacOS/Linux(x64)");
		System.out.println("  - ESLint : " + ESLINT + " (MIT)");
		System.out.println("  - CheckStyle : " + CHECKSTYLE + " (LGPL v2.1)");
		System.out.println();
		System.out.println("Default RuleSet");
		System.out.println("  - PMD : " + PMD_RULESET);
		System.out.println("  - FindBugs : " + FINDBUGS_RULESET);
		System.out.println("  - FindSecBugs : " + FINDSECBUGS_RULESET);
		System.out.println("  - SonarJava : " + SONAR_JAVA_RULESET);
		System.out.println("  - Web Resources :");
		System.out.println("      - JS : " + SONAR_JS_RULESET);
		System.out.println("      - CSS : " + SONAR_CSS_RULESET);
		System.out.println("      - HTML : " + SONAR_WEB_RULESET);
		System.out.println("  - CheckStyle : " + CHECKSTYLE_RULESET);
		System.out.println();
		System.out.println("Copyright(c) 2018 By Samsung SDS (Code Quality Group)");
	}
}
