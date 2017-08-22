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
	//--------------------------------------------------
	public static final String CODE_ANALYST = "1.4.1";
	public static final String SONAR_SCANNER = "2.8";
	public static final String SONAR_SERVER = "6.2.1";
	public static final String PMD = "5.4.6";
	public static final String FINDBUGS = "3.0.1";
	public static final String FINDSECBUGS = "1.7.1";
	public static final String JDEPEND = "2.9.1";
	
	public static final String PMD_RULESET = "5.4 - 147 ruleset";
	public static final String FINDBUGS_RULESET = "3.0.1 - 246 ruleset";
	public static final String FINDSECBUGS_RULESET = "1.7.1 - 121 ruleset";
	
	public static void printVersionInfo() {
		System.out.println("Code Analyst : " + CODE_ANALYST);
		System.out.println("  - Sonar Scanner : " + SONAR_SCANNER + " (LGPL v3.0)");
		System.out.println("  - Sonar Server : " + SONAR_SERVER + " (LGPL v3.0)");
		System.out.println("  - PMD : " + PMD + " (BSD-style)");
		System.out.println("  - FindBugs : " + FINDBUGS + " (LGPL v3.0)");
		System.out.println("  - FindSecBugs : " + FINDSECBUGS + " (LGPL v3.0)");
		System.out.println("  - JDepend : " + JDEPEND +"-based modification" + " (BSD-style) ");
		System.out.println();
		System.out.println("Default RuleSet");
		System.out.println("  - PMD : " + PMD_RULESET);
		System.out.println("  - FindBugs : " + FINDBUGS_RULESET);
		System.out.println("  - FindSecBugs : " + FINDSECBUGS_RULESET);
		System.out.println();
		System.out.println("Copyright(c) 2017 By Samsung SDS (SW Technology Lab.)");
	}
}