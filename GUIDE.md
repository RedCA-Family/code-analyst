# Code Analyst 활용 가이드

## 1. 기본 실행 ##

### [Java]
	$> java -jar Code-Analyst-2.10.6.jar -p "프로젝트 위치" -s "src\main\java" -b "target\classes"

### [JavaScript]
	$> java -jar Code-Analyst-2.10.6.jar -l javascript -p "프로젝트 위치" -s "."

### [C#]
	$> java -jar Code-Analyst-2.10.6.jar -l c# -p "프로젝트 위치" -s "."

### [Python]
	$> java -jar Code-Analyst-2.10.6.jar -l python -p "프로젝트 위치" -s "."


### 결과 화면 (예시)

	14:28:28.929 INFO  c.s.a.c.m.App - Project Directory : C:\Workspace\Project
	14:28:28.935 INFO  c.s.a.c.m.App - Code Size Analysis start...
	...
	================================================================================
	              ______    _______  _______  __   __  ___      _______
	             |    _ |  |       ||       ||  | |  ||   |    |       |
	             |   | ||  |    ___||  _____||  | |  ||   |    |_     _|
	             |   |_||_ |   |___ | |_____ |  |_|  ||   |      |   |
	             |    __  ||    ___||_____  ||       ||   |___   |   |
	             |   |  | ||   |___  _____| ||       ||       |  |   |
	             |___|  |_||_______||_______||_______||_______|  |___|
	================================================================================
	Files : 60
	Dir. : 13
	Classes : 66
	Functions : 540
	lines : 6,703
	Comment Lines : 384
	Ncloc : 4,633
	Statements : 2,210

	Duplicated Blocks : 40
	Duplicated lines : 50
	Duplication % : 0.75%

	Complexity functions : 524
	Complexity Total : 932
	Complexity Over 10(%) : 0.76% (4)
	Complexity Over 15(%) : 0.38% (2)
	Complexity Over 20(%) : 0.00% (0)
	Complexity Equal Or Over 50(%) : 0.00% (0)
	- The complexity is calculated by PMD's Modified Cyclomatic Complexity method

	SonarJava violations : 447
	SonarJava 1 priority : 3
	SonarJava 2 priority : 43
	SonarJava 3 priority : 339
	SonarJava 4 priority : 62
	SonarJava 5 priority : 0

	PMD violations : 302
	PMD 1 priority : 22
	PMD 2 priority : 110
	PMD < 3 priority : 170

	FindBugs bugs : 20
	FindBugs 1 priority : 6
	FindBugs 2 priority : 14
	FindBugs < 3 priority : 0

	FindSecBugs bugs : 10

	WebResource violations : 0

	Cyclic Dependencies : 8

	UnusedCode : 351

	Technical Debt : 414.49MH

	* This project has classes with no package. In this case, some analysis of these classes is not possible.
	 - FindBugs, FindSecBugs, and Cyclic Dependencies


## 2. 세부 옵션

### [Java]
| Option | Description | Default | since | Example |
| ------ | ----------- | ------- | :---: | ------- |
| -l, --language <arg> | 분석 대상 언어 지정 ('Java', 'JavaScript', 'C#' 또는 'Python', 지정되지 않으면 Java로 처리) | 'Java' | 2.7 | -l "java" |
| -p, --project <arg> | 프로젝트 기본 위치 지정 (지정되지 않으면 현재 디렉토리로 처리) | . | 1.0 | -p "C:\Workspace\Project" |
| -s, --src <arg> | 소스 디렉토리 (프로젝트 기본 위치에 대한 상대 경로) | src\main\java, src(v2.2 이전) | 1.0 | -s "src\main\java" |
| -b, --binary <arg> | binary 디렉토리 (프로젝트 기본 위치에 대한 상대 경로) | target\classes | 1.0 | -b "binary" |
| -d, --debug | 디버그 모드 |  | 1.0 | -d |
| -e, --encoding <arg> | 소스에 대한 파일 encoding 지정 | 	UTF-8 | 1.0 | -e UTF-8 |
| -j, --java <arg> | Java 버전 | 1.8 | 1.0 | -j "1.6" |
| -pmd <arg> | PMD ruleset 파일(XML)  미지정시 SDS 표준 Ruleset(v5.4, 147개 rule)으로 처리 |  | 1.0 | -pmd "C:\pmd-rules.xml" |
| -findbugs <arg> | FindBugs ruleset 파일(XML)  미지정시 SDS 표준 Ruleset(v3.0.1, 246개 rule)으로 처리 |  | 1.0 | -findbugs "C:\FindBugs-include.xml" |
| -sonar <arg> | SonarQube exclude 파일(XML)  형식 :  <SonarIssueFilter>  <Exclude key="common-java:DuplicatedBlocks"/>  </SonarIssueFilter> |  | 2.1 | -sonar "C:\SonarIssueFilter.xml" |
| -checkstyle <arg> | CheckStyle 설정 파일 지정   미지정시 자체 표준 Ruleset(58개 rule)으로 처리 |  | 2.8 | -checkstyle "C:\checkstyle.xml" |
| -o, --output <arg> | 결과 저장 파일명 지정  미지정시, 현재날짜를 포함하는 파일명 사용 | result-[yyyyMMddHHmmss].[out|json] | 1.0 | -o result.txt |
| -f, --format <arg> | 결과 파일 형식 지정 (json, text, none)  * none의 경우 summary 결과만 표시하고 결과 파일을 저장하지 않음 | json | 1.2 | -f text |
| -v, --version | 버전 정보 표시 (사용 library 및 버전, 적용 inspection rule 포함) |  | 1.0 | -v |
| -t, --timeout <arg> | 내부적으로 사용되는 web service에 대한 timeout | 100분 | 1.0 | -t 160 |
| -c, --complexity <arg> | 지정된 클래스에 대한 복잡도 표시 (전체 메소드 표시)  glob 패턴으로 지정하고 클래스명 기준으로 지정 (".java" 제외) |  | 1.1 | -c "MainApp" |
| -w, --webapp <arg> | webapp root 디렉토리 지정  ※ webapp 옵션이 지정되지 않으며, mode에 javascript, css, html 점검항목은 적용되지 않음 |  | 2.2 | -w "src/main/webapp" |
| -include <arg> | include 패턴 지정 (Ant-style로 comma로 구분되는 여러 패턴 지정 가능) |  | 1.2 | -include "com/sds/**/*.java" |
| -exclude <arg> | exclude 패턴 지정 (Ant-style로 comma로 구분되는 여러 패턴 지정 가능) |  | 1.2 | -exclude "com/sds/**/*VO.java" |
| -m, --mode <arg> | 점검 대상 선택 지정으로 code-size, duplication, sonarjava, complexity, pmd, findbugs, findsecbugs, javascript, css, html, dependency, unusedcode, ckmetrics 지원 (comma로 구분)  (v2.2 이후) 점검 대상 앞에 "-"가 붙는 경우는 해당 항목 점검이 제외되며, 기존 webresource가 javascript, css, html로 분리됨  ※ sonarjava, webresource, unusedcode는 2.0부터, ckmetrics는 2.5부터 지원 | ※ 전체 (css, html은 제외) | 1.3 | -m "pmd,findbugs,findsecbugs" |
| -a,--analysis | 세부 분석 모드로 "영향도 높은 중복 코드 블럭 식별" 등 추가   (내부적으로 좀더 많은 메모리를 사용하므로 OOM 발생 시, JVM의 '-Xmx' 옵션을 사용 권장, eg: -Xmx1024m) |  | 1.5 | -a |
| -r,--rerun <arg> | 이전 실행된 결과 output(text)와 동일한 옵션을 지정하여 실행  영향을 받는 옵션 : 'project', 'src', 'binary', 'encoding', 'java', 'pmd', 'findbugs', 'include', 'exclude', 'mode', 'analysis', 'seperated', 'catalog' 및 'webap' |  | 1.5 | -r result.out |
| -seperated | 중복도, 복잡도, PMD, FindBugs, FindSecBugs에 대한 결과 정보를 별도의 파일로 분리 |  | 1.5 | -seperated |
| -catalog | 점검된 파일 목록 |  | 2.1 | -catalog |
| -duplication <arg> | 중복 점검 방식 지정 ('statement' 또는 'token' 방식) | statement  | 2.6 | -duplication token |
| -tokens <arg> | token 중복 점검 방식의 token 기준값 지정 | 100 | 2.6 | -tokens 50 |

※ 'include' 또는 'exclude' 옵션 지정 시, ```@file``` 형태로 파일 지정이 가능합니다. (since v2.8)
이때 파일은 다음과 같은 형식으로 지정하면 됩니다. (여러 라인으로 구성되며, 각 라인은 콤마 없이 하나의 패턴을 지정)
```
com/sds/**/*.java
com/samsung/**/*.java
```

### [JavaScript]
| Option | Description | Default | since | Example |
| ------ | ----------- | ------- | :---: | ------- |
| -l, --language <arg> | 분석 대상 언어 지정 ('Java', 'JavaScript', 'C#' 또는 'Python', 지정되지 않으면 Java로 처리) | 'Java' | 2.7 | -l "javascript" |
| -p, --project <arg> | 프로젝트 기본 위치 지정 (지정되지 않으면 현재 디렉토리로 처리) | . | 2.7 | -p "C:\Workspace\Project" |
| -s, --src <arg> | 소스 디렉토리 (프로젝트 기본 위치에 대한 상대 경로) | . | 2.7 | -s "app" |
| -d, --debug | 디버그 모드 |  | 2.7 | -d |
| -e, --encoding <arg> | 소스에 대한 파일 encoding 지정 | 	UTF-8 | 2.7 | -e UTF-8 |
| -sonar <arg> | SonarQube exclude 파일(XML)  형식 :  <SonarIssueFilter>  <Exclude key="common-js:DuplicatedBlocks"/>  </SonarIssueFilter> |  | 2.7 | -sonar "C:\SonarIssueFilter.xml" |
| -o, --output <arg> | 결과 저장 파일명 지정  미지정시, 현재날짜를 포함하는 파일명 사용 | result-[yyyyMMddHHmmss].[out|json] | 2.7 | -o result.txt |
| -f, --format <arg> | 결과 파일 형식 지정 (json, text, none)  * none의 경우 summary 결과만 표시하고 결과 파일을 저장하지 않음 | json | 2.7 | -f text |
| -v, --version | 버전 정보 표시 (사용 library 및 버전, 적용 inspection rule 포함) |  | 2.7 | -v |
| -t, --timeout <arg> | 내부적으로 사용되는 web service에 대한 timeout | 100분 | 2.7 | -t 160 |
| -include <arg> | include 패턴 지정 (Ant-style로 comma로 구분되는 여러 패턴 지정 가능) |  | 2.7 | -include "app/**/*.js" |
| -exclude <arg> | exclude 패턴 지정 (Ant-style로 comma로 구분되는 여러 패턴 지정 가능) |  | 2.7 | -exclude "tests/**,tests-*/**,*-tests/**" |
| -m, --mode <arg> | 점검 대상 선택 지정으로 code-size, duplication, complexity, sonarjs (comma로 구분)  점검 대상 앞에 "-"가 붙는 경우는 해당 항목 점검이 제외됨 | ※ 전체 | 2.7 | -m "sonarjs" |
| -a,--analysis | 세부 분석 모드로 "영향도 높은 중복 코드 블럭 식별" 등 추가   (내부적으로 좀더 많은 메모리를 사용하므로 OOM 발생 시, JVM의 '-Xmx' 옵션을 사용 권장, eg: -Xmx1024m) |  | 2.7 | -a |
| -r,--rerun <arg> | 이전 실행된 결과 output(text)와 동일한 옵션을 지정하여 실행  영향을 받는 옵션 : 'project', 'src', 'encoding', 'sonar', 'include', 'exclude', 'mode', 'analysis', 'seperated' alc 'catalog' |  | 2.7 | -r result.out |
| -seperated | 중복도, 복잡도, SonarJS에 대한 결과 정보를 별도의 파일로 분리 |  | 2.7 | -seperated |
| -catalog | 점검된 파일 목록 |  | 2.7 | -catalog |
| -duplication <arg> | 중복 점검 방식 지정 ('statement' 또는 'token' 방식) | statement  | 2.6 | -duplication token |
| -tokens <arg> | token 중복 점검 방식의 token 기준값 지정 | 100 | 2.6 | -tokens 50 |

※ 'include' 또는 'exclude' 옵션 지정 시, ```@file``` 형태로 파일 지정이 가능합니다. (since v2.8)
이때 파일은 다음과 같은 형식으로 지정하면 됩니다. (여러 라인으로 구성되며, 각 라인은 콤마 없이 하나의 패턴을 지정)
```
com/sds/**/*.js
com/samsung/**/*.js
```

### [C#]
| Option | Description | Default | since | Example |
| ------ | ----------- | ------- | :---: | ------- |
| -l, --language <arg> | 분석 대상 언어 지정 ('Java', 'JavaScript', 'C#' 또는 'Python', 지정되지 않으면 Java로 처리) | 'Java' | 2.7 | -l "c#" |
| -p, --project <arg> | 프로젝트 기본 위치 지정 (지정되지 않으면 현재 디렉토리로 처리) | . | 2.7 | -p "C:\Workspace\Project" |
| -s, --src <arg> | 소스 디렉토리 (프로젝트 기본 위치에 대한 상대 경로) | . | 2.7 | -s "app" |
| -d, --debug | 디버그 모드 |  | 2.7 | -d |
| -e, --encoding <arg> | 소스에 대한 파일 encoding 지정 | 	UTF-8 | 2.7 | -e UTF-8 |
| -sonar <arg> | SonarQube exclude 파일(XML)  형식 :  <SonarIssueFilter>  <Exclude key="common-js:DuplicatedBlocks"/>  </SonarIssueFilter> |  | 2.7 | -sonar "C:\SonarIssueFilter.xml" |
| -o, --output <arg> | 결과 저장 파일명 지정  미지정시, 현재날짜를 포함하는 파일명 사용 | result-[yyyyMMddHHmmss].[out|json] | 2.7 | -o result.txt |
| -f, --format <arg> | 결과 파일 형식 지정 (json, text, none)  * none의 경우 summary 결과만 표시하고 결과 파일을 저장하지 않음 | json | 2.7 | -f text |
| -v, --version | 버전 정보 표시 (사용 library 및 버전, 적용 inspection rule 포함) |  | 2.7 | -v |
| -t, --timeout <arg> | 내부적으로 사용되는 web service에 대한 timeout | 100분 | 2.7 | -t 160 |
| -include <arg> | include 패턴 지정 (Ant-style로 comma로 구분되는 여러 패턴 지정 가능) |  | 2.7 | -include "app/**/*.cs" |
| -exclude <arg> | exclude 패턴 지정 (Ant-style로 comma로 구분되는 여러 패턴 지정 가능) |  | 2.7 | -exclude "tests/**,tests-*/**,*-tests/**" |
| -m, --mode <arg> | 점검 대상 선택 지정으로 code-size, duplication, complexity, sonarcsharp (comma로 구분)  점검 대상 앞에 "-"가 붙는 경우는 해당 항목 점검이 제외됨 | ※ 전체 | 2.7 | -m "sonarcsharp" |
| -a,--analysis | 세부 분석 모드로 "영향도 높은 중복 코드 블럭 식별" 등 추가   (내부적으로 좀더 많은 메모리를 사용하므로 OOM 발생 시, JVM의 '-Xmx' 옵션을 사용 권장, eg: -Xmx1024m) |  | 2.7 | -a |
| -r,--rerun <arg> | 이전 실행된 결과 output(text)와 동일한 옵션을 지정하여 실행  영향을 받는 옵션 : 'project', 'src', 'encoding', 'sonar', 'include', 'exclude', 'mode', 'analysis', 'seperated' alc 'catalog' |  | 2.7 | -r result.out |
| -seperated | 중복도, 복잡도, SonarJS에 대한 결과 정보를 별도의 파일로 분리 |  | 2.7 | -seperated |
| -catalog | 점검된 파일 목록 |  | 2.7 | -catalog |
| -duplication <arg> | 중복 점검 방식 지정 ('statement' 또는 'token' 방식) | statement  | 2.6 | -duplication token |
| -tokens <arg> | token 중복 점검 방식의 token 기준값 지정 | 100 | 2.6 | -tokens 50 |

※ 'include' 또는 'exclude' 옵션 지정 시, ```@file``` 형태로 파일 지정이 가능합니다. (since v2.8)
이때 파일은 다음과 같은 형식으로 지정하면 됩니다. (여러 라인으로 구성되며, 각 라인은 콤마 없이 하나의 패턴을 지정)
```
com/sds/**/*.cs
com/samsung/**/*.cs
```

### [Python]
| Option | Description | Default | since | Example |
| ------ | ----------- | ------- | :---: | ------- |
| -l, --language <arg> | 분석 대상 언어 지정 ('Java', 'JavaScript', 'C#' 또는 'Python', 지정되지 않으면 Java로 처리) | 'Java' | 2.7 | -l "python" |
| -p, --project <arg> | 프로젝트 기본 위치 지정 (지정되지 않으면 현재 디렉토리로 처리) | . | 2.7 | -p "C:\Workspace\Project" |
| -s, --src <arg> | 소스 디렉토리 (프로젝트 기본 위치에 대한 상대 경로) | . | 2.7 | -s "app" |
| -d, --debug | 디버그 모드 |  | 2.7 | -d |
| -e, --encoding <arg> | 소스에 대한 파일 encoding 지정 | 	UTF-8 | 2.7 | -e UTF-8 |
| -sonar <arg> | SonarQube exclude 파일(XML)  형식 :  <SonarIssueFilter>  <Exclude key="common-js:DuplicatedBlocks"/>  </SonarIssueFilter> |  | 2.7 | -sonar "C:\SonarIssueFilter.xml" |
| -o, --output <arg> | 결과 저장 파일명 지정  미지정시, 현재날짜를 포함하는 파일명 사용 | result-[yyyyMMddHHmmss].[out|json] | 2.7 | -o result.txt |
| -f, --format <arg> | 결과 파일 형식 지정 (json, text, none)  * none의 경우 summary 결과만 표시하고 결과 파일을 저장하지 않음 | json | 2.7 | -f text |
| -v, --version | 버전 정보 표시 (사용 library 및 버전, 적용 inspection rule 포함) |  | 2.7 | -v |
| -t, --timeout <arg> | 내부적으로 사용되는 web service에 대한 timeout | 100분 | 2.7 | -t 160 |
| -include <arg> | include 패턴 지정 (Ant-style로 comma로 구분되는 여러 패턴 지정 가능) |  | 2.7 | -include "app/**/*.cs" |
| -exclude <arg> | exclude 패턴 지정 (Ant-style로 comma로 구분되는 여러 패턴 지정 가능) |  | 2.7 | -exclude "tests/**,tests-*/**,*-tests/**" |
| -m, --mode <arg> | 점검 대상 선택 지정으로 code-size, duplication, complexity, sonarpython (comma로 구분)  점검 대상 앞에 "-"가 붙는 경우는 해당 항목 점검이 제외됨 | ※ 전체 | 2.7 | -m "sonarcsharp" |
| -a,--analysis | 세부 분석 모드로 "영향도 높은 중복 코드 블럭 식별" 등 추가   (내부적으로 좀더 많은 메모리를 사용하므로 OOM 발생 시, JVM의 '-Xmx' 옵션을 사용 권장, eg: -Xmx1024m) |  | 2.7 | -a |
| -r,--rerun <arg> | 이전 실행된 결과 output(text)와 동일한 옵션을 지정하여 실행  영향을 받는 옵션 : 'project', 'src', 'encoding', 'sonar', 'include', 'exclude', 'mode', 'analysis', 'seperated' alc 'catalog' |  | 2.7 | -r result.out |
| -seperated | 중복도, 복잡도, SonarJS에 대한 결과 정보를 별도의 파일로 분리 |  | 2.7 | -seperated |
| -catalog | 점검된 파일 목록 |  | 2.7 | -catalog |
| -duplication <arg> | 중복 점검 방식 지정 ('statement' 또는 'token' 방식) | statement  | 2.6 | -duplication token |
| -tokens <arg> | token 중복 점검 방식의 token 기준값 지정 | 100 | 2.6 | -tokens 50 |

※ 'include' 또는 'exclude' 옵션 지정 시, ```@file``` 형태로 파일 지정이 가능합니다. (since v2.8)
이때 파일은 다음과 같은 형식으로 지정하면 됩니다. (여러 라인으로 구성되며, 각 라인은 콤마 없이 하나의 패턴을 지정)
```
com/sds/**/*.py
com/samsung/**/*.py
```

## 3. API 활용 가이드

### 3.1 기본 API

API 호출을 의한 Interface는 다음과 같이 정의되어 있습니다.

```java
package com.samsungsds.analyst.code.api;

public interface CodeAnalyst {
    void addProgressObserver(ProgressObserver observer);
    void deleteProgressObserver(ProgressObserver observer);

    String analyze(String where, ArgumentInfo argument, TargetFileInfo targetFile);
    ResultInfo analyzeWithSeperatedResult(String where, ArgumentInfo argument, TargetFileInfo targetFile);
}
```

* addProgressObserver() 및 deleteProgressObserver() 메소드는 진행상태 확인할 수 있는 Observer에 대한 등록 및 삭제를 제공 (3.3. 참조)
* analyze() 메소드는 코드 분석을 요청 (3.2. 참조)
* analyzeWithSeperatedResult() 메소는 5개의 결과 리스트(Duplication, Complexity, Pmd, Findbugs, FindSecBugs)를 별도의 json으로 분리하여 저장


### 3.2 코드 분석 요청

API의 analyze() 호출을 통해 코드 분석을 수행하며, 다음과 같은 3개의 파라미터를 지정해야 합니다.

* where : 결과 파일(json) 저장 위치 지정 (eg: eclipse plugin의 경우, workspace의 metadata 하위 디렉토리 지정 등)
* ArgumentInfo : 코드 분석을 위한 호출 정보로 다음과 같은 property를 지정

#### [Java]
| Property | Description | Remarks |
| -------- | ----------- | ------- |
| project | project 기본 디렉토리 지정 | 필수 |
| src | 점검 대상 소스 디렉토리 (위 project 속성에 대한 상대경로 지정) | 필수 |
| binary | 점검 대상 binary 디렉토리 (위 project 속성에 대한 상대경로 지정) | 필수 |
| debug | Debug 모드 (개발자 모드) | 기본값 : false |
| encoding | 소스 인코딩 | 기본값 : UTF-8 |
| javaVersion | 소스 Java 버전 | 기본값 : 1.8 |
| pmdRuleFile | PMD ruleset xml 파일 (생략되면 최종 RedCA Way ruleset 사용) |  |
| findBugsRuleFile | FindBugs ruleset xml 파일 (생략되면 최종 RedCA Way ruleset 사용)
| sonarRuleFile | SonarJava 등 SonarQube plugin exclude 처리 xml (생략되면 최종 RedCA Way ruleset 전체 적용) |  |
| checkStyleRuleFile | CheckStyle configuration xml (생략되면 최종 RedCA Way ruleset 적용) |  |
| timeout | 내부적으로 사용되는 jetty에 대한 timeout 지정 (개발자 모드) | 기본값 : 100분 |
| exclude | 제외 패턴(ant-style, 여러 개인 경우 comma로 구분) |  |
| webapp | webapp root 디렉토리 지정 (위 project 속성에 대한 상대경로 지정) |  |
| mode | AnalysisMode로 점검하고자 하는 항목 지정 (codeSize, duplication, complexity, pmd, findBugs, findSecBugs, javascript, css, html, dependency, unused, ckmetrics, checkstyle) | css, html은 기본적으로 비활성화 |
| detailAnalysis | 세부 분석 처리 (Top 10 분석 등) |  |
| saveCatalog | 점검된 파일 목록 기록 |  |

#### [JavaScript]
| Property | Description | Remarks |
| -------- | ----------- | ------- |
| project | project 기본 디렉토리 지정 | 필수 |
| src | 점검 대상 소스 디렉토리 (위 project 속성에 대한 상대경로 지정) | 필수 |
| debug | Debug 모드 (개발자 모드) | 기본값 : false |
| encoding | 소스 인코딩 | 기본값 : UTF-8 |
| sonarRuleFile | SonarJava 등 SonarQube plugin exclude 처리 xml (생략되면 최종 RedCA Way ruleset 전체 적용) |  |
| timeout | 내부적으로 사용되는 jetty에 대한 timeout 지정 (개발자 모드) | 기본값 : 100분 |
| exclude | 제외 패턴(ant-style, 여러 개인 경우 comma로 구분) |  |
| mode | AnalysisMode로 점검하고자 하는 항목 지정 (codeSize, duplication, complexity, sonarjs) | 기본 : 전체 |
| detailAnalysis | 세부 분석 처리 (Top 10 분석 등) |  |
| saveCatalog | 점검된 파일 목록 기록 |  |

#### [C#]
| Property | Description | Remarks |
| -------- | ----------- | ------- |
| project | project 기본 디렉토리 지정 | 필수 |
| src | 점검 대상 소스 디렉토리 (위 project 속성에 대한 상대경로 지정) | 필수 |
| debug | Debug 모드 (개발자 모드) | 기본값 : false |
| encoding | 소스 인코딩 | 기본값 : UTF-8 |
| sonarRuleFile | SonarJava 등 SonarQube plugin exclude 처리 xml (생략되면 최종 RedCA Way ruleset 전체 적용) |  |
| timeout | 내부적으로 사용되는 jetty에 대한 timeout 지정 (개발자 모드) | 기본값 : 100분 |
| exclude | 제외 패턴(ant-style, 여러 개인 경우 comma로 구분) |  |
| mode | AnalysisMode로 점검하고자 하는 항목 지정 (codeSize, duplication, complexity, sonarcsharp) | 기본 : 전체 |
| detailAnalysis | 세부 분석 처리 (Top 10 분석 등) |  |
| saveCatalog | 점검된 파일 목록 기록 |  |

#### [Python]
| Property | Description | Remarks |
| -------- | ----------- | ------- |
| project | project 기본 디렉토리 지정 | 필수 |
| src | 점검 대상 소스 디렉토리 (위 project 속성에 대한 상대경로 지정) | 필수 |
| debug | Debug 모드 (개발자 모드) | 기본값 : false |
| encoding | 소스 인코딩 | 기본값 : UTF-8 |
| sonarRuleFile | SonarJava 등 SonarQube plugin exclude 처리 xml (생략되면 최종 RedCA Way ruleset 전체 적용) |  |
| timeout | 내부적으로 사용되는 jetty에 대한 timeout 지정 (개발자 모드) | 기본값 : 100분 |
| exclude | 제외 패턴(ant-style, 여러 개인 경우 comma로 구분) |  |
| mode | AnalysisMode로 점검하고자 하는 항목 지정 (codeSize, duplication, complexity, sonarpython) | 기본 : 전체 |
| detailAnalysis | 세부 분석 처리 (Top 10 분석 등) |  |
| saveCatalog | 점검된 파일 목록 기록 |  |

#### 공통 (Java, JavaScript, C#, Python)

* TargetFileInfo : 분석 대상 폴더 및 소스 파일 지정 (addPackage() 또는 addFile()/addFileExactly()을 통해 분석 대상 소스 지정)

| Method | Description |
| setIncludeSubPackage(boolean) | 패키지 지정 시, 하위 패키지 포함 여부 지정 |
| addPackage(String packageName) | 패키지를 지정 (하위 패키지 포함 여부에 함께 사용됨) |
| addFile(String packageName, String file) | 패키지에 대한 파일명 지정 (src가 정확히 지정되지 않더라고 처리되나 일부 속도가 느려짐)  ※ **/packageName/file 형식으로 지정됨 |
| addFileExactly(String packageName, String file) | 패키지에 대한 파일명 지정 (src가 정확히 지정되어야 함)  ※ packageName/file 형식으로 지정됨 |


* 여러 결과를 받는 ResultInfo는 다음과 같은 property를 갖음

| Property | Description |
| -------- | ----------- |
| outputFile | 전체 결과를 갖는 파일 |
| duplicationFile | Duplication List 결과 파일 |
| complexityFile | Complexity List 결과 파일 |
| pmdFile | PMD List 결과 파일 |
| findBugsFile | FindBugs List 결과 파일 |
| findSecBugsFile | FindSecBugs List 결과 파일 |
| sonarJavaFile | SonarJava List 결과 파일 |
| sonarJsFile | SonarJs List 결과 파일 |
| sonarCSharpFile | SonarCSharp List 결과 파일 |
| sonarPythonFile | SonarPython List 결과 파일 |
| webResourceFile | Web Resource List 결과 파일 |
| ckMetricsFile | CK Metrics List 결과 파일 |
| checkStyleFile | CheckStyle List 결과 파일 |

최정 return 정보는 where 하위에 생성된 결과 파일(json)에 대한 전체 경로로 "result-[yyyyMMddHHmmss].json" 형식으로 처리됩니다.

Java language 모드에서 분석 요청에 대한 예는 다음과 같습니다.

```java
	CodeAnalyst analyst = CodeAnalystFactory.create();

	ArgumentInfo argument = new ArgumentInfo();

	argument.setProject("C:\\Workspace\\Project");

	argument.setSrc("src");
	argument.setBinary("target\\classes");

	argument.setEncoding("UTF-8");  // default
	argument.setJavaVersion("1.8"); // default

	AnalysisMode mode = new AnalysisMode();
	mode.setCodeSize(true);
	mode.setDuplication(true);
	mode.setSonarJava(true);
	mode.setComplexity(true);
	mode.setPmd(true);
	mode.setFindBugs(true);
	mode.setFindSecBugs(false);
	mode.setWebResource(false);
	mode.setDependency(true);
	mode.setUnused(true);
	mode.setCkMetrics(true);

	argument.setMode(mode);

	argument.setExclude("JDepend.java,com/samsungsds/analyst/code/main/filter/*.java");

	TargetFileInfo targetFile = new TargetFileInfo();

	// addPackage() 또는 addFile()로 점검 대상 지정 (or 조건으로 처리됨)
	// - addPackage()는 선택된 패키지의 소스 전체
	// - addFile()은 선택된 소스

	//targetFile.addPackage("com.samsungsds.analyst.code.main");    // include sub-packages

	targetFile.addFile("com.samsungsds.analyst.code.main", "MeasuredResult.java");
	targetFile.addFile("com.samsungsds.analyst.code.main", "ResultProcessor.java");

	File temp = new File("C:\\Temp");
	if (!temp.exists()) {
	    temp.mkdirs();
	}

	String resultFile = analyst.analyze("C:\\Temp", argument, targetFile);
```

JavaScript language 모드에서 분석 요청에 대한 예는 다음과 같습니다. (```CodeAnalystFactory.create()``` 호출 시 Language 파라미터 추가 지정)

```java
    CodeAnalyst analyst = CodeAnalystFactory.create(Language.JAVASCRIPT);

    ArgumentInfo argument = new ArgumentInfo();
    try {
        argument.setProject(new File(".").getCanonicalPath());
    } catch (IOException ex) {
        ex.printStackTrace();
        return;
    }

    argument.setEncoding("UTF-8"); // default

    argument.setSrc("app");

    AnalysisMode mode = new AnalysisMode();
    mode.setCodeSize(true);
    mode.setDuplication(true);
    mode.setComplexity(true);
    mode.setSonarJS(true);

    argument.setMode(mode);

    TargetFileInfo targetFile = new TargetFileInfo();

    File temp = new File(TEMP_DIRECTORY);
    if (!temp.exists()) {
        temp.mkdirs();
    }

    String resultFile = analyst.analyze("C:\\Temp", argument, targetFile);

    System.out.println("Result File : " + resultFile);
```


※ TargetFileInfo의 addPackage()는 특정 패키지의 하위 소스가 점검 대상으로 추가하며, addFile()로 특정 패키지의 특정 파일을 추가할 수 있습니다. (각 조건은 "or" 조건으로 처리)


### 3.3 Web 리소스 분석(JavaScript, CSS, HTML)

V2.2부터 web 리소스 분석을 별도의 analyzeWebResource() 메소드로 분리하였으며 다음과 같이 4개의 파라미터와 함께 호출합니다.
※ Web 리소스 분석은 'Java' language 모드에서 동작함

* where : 결과 파일(json) 저장 위치 지정 (eg: eclipse plugin의 경우, workspace의 metadata 하위 디렉토리 지정 등)
* WebArgumentInfo : 코드 분석을 위한 호출 정보로 다음과 같은 property를 지정

| Property | Description | Remarks |
| -------- | ----------- | ------- |
| project | project 기본 디렉토리 지정 | 필수 |
| webapp | webapp root 디렉토리 지정 (위 project 속성에 대한 상대경로 지정) | 필수 |
| debug | Debug 모드 (개발자 모드) | 기본값 : false |
| encoding | 소스 인코딩 | 기본값 : UTF-8 |
| javaVersion | 소스 Java 버전 | 기본값 : 1.8 |
| sonarRuleFile | SonarJava 등 SonarQube plugin exclude 처리 xml (생략되면 최종 SDS 표준 ruleset 전체 적용) |  |
| timeout | 내부적으로 사용되는 jetty에 대한 timeout 지정 (개발자 모드) | 기본값 : 100분 |
| exclude | 제외 패턴(ant-style, 여러 개인 경우 comma로 구분) |  |
| detailAnalysis | 세부 분석 처리 (Top 10 분석 등) |  |
| saveCatalog | 점검된 파일 목록 기록 |  |

* WebTargetFileInfo : 분석 대상 폴더 및 소스 파일 지정 (addDirectory() 또는 addFile()을 통해 분석 대상 소스 지정)

| Method | Description |
| setIncludeSubDirectory(boolean) | 디렉토리 지정 시, 하위 디렉토리 포함 여부 지정 |
| addDirectory(String directory) | 디렉토리를 추가 지정 |
| addFile(String filePath) | 파일을 추가 추가 |

* includeCssAndHtml : CSS 및 HTML 추가 점검 지정

분석 요청의 예는 다음과 같습니다.

```java
	WebArgumentInfo argument = new WebArgumentInfo();


	argument.setProject("C:\workspace\test");
	argument.setEncoding("UTF-8"); // default

	argument.setWebapp("src/main/webapp");

	// argument.setDebug(true);

	// argument.setExclude("**/test.js");

	argument.setSaveCatalog(true);

	WebTargetFileInfo targetFile = new WebTargetFileInfo();

	File temp = new File(TEMP_DIRECTORY);
	if (!temp.exists()) {
	    temp.mkdirs();
	}

	String resultFile = analyst.analyzeWebResource(TEMP_DIRECTORY, argument, targetFile, false);

```

### 3.4 진행상태 Observer

API는 addProgressObserver() 및 deleteProgressObserver()를 통해 진행 상태에 대한 상태를 확인하기 위한 Observer 추가 및 삭제가 가능합니다.

```java
	analyst.addProgressObserver(progress -> {
	     System.out.println("++++++++++++++++++++++++++++++++++++++++");
	     System.out.print("Event : " + progress.getProgressEvent() + ", Current : " + progress.getCompletedPercent() + "%");
	     System.out.println(", " + numberFormatter.format(progress.getElapsedTimeInMillisecond()) + " elapsed ms");
	     System.out.println("++++++++++++++++++++++++++++++++++++++++");
	 });
```

ProgressObserver의 informProgress() 메소드에 의해 notify되며, AnalysisProgress 파라미터를 통해 다음과 같은 3가지 정보를 확인할 수 있습니다.

| Method | Description | Remarks |
| ------ | ----------- | ------- |
| getProgressEvent() | ProgressEvent enum 유형으로 9가지 단계를 갖는다 (아래 참조) | AnalysisMode로 지정된 점검 항목만 호출됨 |
| getCompletedPercent() | 진행 상태를 %로 가져옴 | 0 ~ 100 |
| getElapsedTimeInMillisecond() | 이전 단계부터 현재단계까지의 진행 시간 | 1/1000 초 단위 |

#### ProgressEvent
* PREPARE_COMPLETE : 준비 완료 후 호출
* SONAR_START_COMPLETE : Sonar 시작 완료 후 호출
* CODE_SIZE_COMPLETE : 기본 코드 분석 후 호출
* DUPLICATION_COMPLETE : 중복 분석 후 호출
* COMPLEXITY_COMPLETE : 복잡도 분석 후 호출
* SONARJAVA_COMPLETE : SonarJava 분석 후 호출
* JAVASCRIPT_COMPLETE : SonarJS 분석 후 호출
* SONARCSHARP_COMPLETE : SonarCSharp 분석 후 호출
* SONARPYTHON_COMPLETE : SonarPython 분석 후 호출
* CSS_COMPLETE : CSS(Sonar) 분석 후 호출
* HTML_COMPLETE : Sonar Web 분석 후 호출
* SONAR_ALL_COMPLETE: Sonar 관련 전체 분석 후 호출
* PMD_COMPLETE : PMD 점검 후 호출
* FINDBUGS_COMPLETE : FindBugs 점검 후 호출
* FINDSECBUGS_COMPLETE : FindSecBugs 점검 후 호출
* DEPENDENCY_COMPLETE : 순환참조 점검 후 호출
* UNUSED_COMPLTE : 미사용코드 점검 후 호출
* CK_METRICS_COMPLETE : CK Metrics 점검 후 호출
* CHECKSTYLE_COMPLETE : CheckStyle 점검 후 호출
* FINAL_COMPLETE : 최종 점검 완료 후 호출
