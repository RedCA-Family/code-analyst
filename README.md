# Code Analyst Project

[![](https://img.shields.io/github/tag/RedCA-Family/code-analyst.svg?style=flat&label=release)](https://github.com/RedCA-Family/code-analyst/releases)
[![Build Status](https://travis-ci.org/RedCA-Family/code-analyst.svg?branch=development)](https://travis-ci.org/RedCA-Family/code-analyst)
[![License](https://img.shields.io/github/license/RedCA-Family/code-analyst.svg?style=flat)](https://raw.githubusercontent.com/RedCA-Family/code-analyst/master/LICENSE.txt)

Code Analyst는 코드 품질에 대한 다양한 지표를 통합적으로 확인할 수 있는 프로그램입니다.

기본적으로 측정되는 코드 규모(프로그램 개수, loc 등)뿐만 아니라 중복도, 복잡도, Inspection 결과(PMD, FindBugs) 등을 확인할 수 있습니다.

※ 현재 Java, JavaScript(Node.js), C# 및 Python을 지원


## Build & Installation

Code Analyst를 실행하기 위해서는 Java 1.8이 필요하며, 하나의 통합 jar로 구성되어 있습니다. (참고로 분석 대상 프로젝트의 JDK 버전과는 별도임)

빌드는 다음과 같이 maven을 통해 수행합니다.(사전에 maven 혹은 이클립스 등 컴파일 도구가 필요합니다.)

	git clone https://github.com/RedCA-Family/code-analyst.git
	cd code-analyst
	mvnw clean package

생성된 jar 파일을 임의의 디렉토리에 위치시키고 아래 사용법과 같이 실행하여 사용합니다.

### Docker 기반 빌드 및 실행
Docker를 통해 다음과 같이 빌드를 할 수 있습니다.

```shell
$ docker image build -t code-analyst .
```

실행은 `/project`에 대한 volume을 지정하여 분석하고자 하는 프로젝트 위치를 지정합니다. 최종 결과 파일도 지정된 `/project` volume에 생성됩니다.
(docker를 사용하는 경우 `-p` 프로젝트 위치 옵션 지정 대신 이 `/project` volume을 사용합니다.)

```shell
$ docker run --rm -v /workspace/project:/project code-analyst [options] 
```


## API 사용 ##
API 형태로 사용하기 위해서는 Maven dependencies에 다음과 같은 dependency를 추가하면 됩니다.

	<dependency>
		<groupId>com.samsungsds.analyst</groupId>
		<artifactId>code-analyst</artifactId>
		<version>2.10.6</version>
	</dependency>

**API 활용에 대한 사항은 [Guide](GUIDE.md)를 참조**해 주세요.


## Usage
CLI(Command Line Interface) 형태로 사용되며, Java, JavaScript(Node.js), C# 및 Python을 지원합니다.
언어에 대한 지정은 ```--language``` 또는 ```-l``` 옵션을 통해 지정됩니다.

### Java 언어 점검

    $> java -jar Code-Analyst-2.10.6.jar -l java -p "프로젝트 위치" -s "src\main\java" -b "target\classes"

※ 참고로 ```-l,--language``` 지정이 없으면, Java 언어를 기본 점검 대상으로 합니다.

기본적으로 --project 옵션을 통해 분석하고자 하는 프로젝트 위치를 지정합니다.

이와 함께 --src, --binary 옵션으로 소스 디렉토리와 binary 디렉토리(컴파일된 class 파일 생성 위치)를 지정합니다. (생략되면 maven 프로젝트 기준으로 설정되며, "--project" 옵션에 대한 상대 경로로 지정해야 합니다.)


#### Help (Java)

	$> java -jar Code-Analyst-2.10.6.jar --help
    usage: java -jar Code-Analyst-2.10.6.jar
     ※ To see individual language-specific option usages, specify the '-l' or '--language' option
     -l,--language <arg>     specify the language to analyze. ('Java', 'JavaScript', 'C#' or 'Python', default : "Java")
     -h,--help               show help.
     -p,--project <arg>      specify project base directory. (default: ".")
     -s,--src <arg>          specify source directories with comma separated. (default: "${project}\src\main\java")
     -b,--binary <arg>       specify binary directories with comma separated. (default: "${project}\target\classes")
     -library <arg>          specify library directory, jar files contained.
     -d,--debug              debug mode.
     -e,--encoding <arg>     encoding of the source code. (default: UTF-8)
     -j,--java <arg>         specify java version. (default: 1.8)
     -pmd <arg>              specify PMD ruleset xml file.
     -findbugs <arg>         specify FindBugs ruleset(include filter) xml file.
     -sonar <arg>            specify SonarQube issue ruleset(exclude filter) xml file.
                             ex:
                             <SonarIssueFilter>
                             <Exclude key="common-java:DuplicatedBlocks"/>
                             </SonarIssueFilter>
     -checkstyle <arg>       specify CheckStyle configuration xml file.
     -o,--output <arg>       specify result output file. (default : "result-[yyyyMMddHHmmss].[out|json]")
     -f,--format <arg>       specify result output file format(json, text, none). (default : text)
     -v,--version            display version info.
     -t,--timeout <arg>      specify internal ws timeout. (default : 100 min.)
     -c,--complexity <arg>   specify class name(glob pattern) to be measured. (Cyclomatic Complexity Measurement mode)
     -w,--webapp <arg>       specify webapp root directory to be inspected. If it's not specified, 'javascript', 'css',
                             'html' analysis items will be disabled.
                             ※ webapp directory should not overlap the src directories.
     -include <arg>          specify include pattern(Ant-style) with comma separated. (e.g.: com/sds/**/*.java)
     -exclude <arg>          specify exclude pattern(Ant-style) with comma separated. (e.g.: com/sds/**/*VO.java)
                             ※ If 'include' or 'exclude' option starts with '@' and has file name, the option value is read
                             from the file
     -m,--mode <arg>         specify analysis items with comma separated. If '-' specified in each mode, the mode is
                             excluded. (code-size, duplication, complexity, sonarjava, pmd, findbugs, findsecbugs,
                             javascript, css, html, dependency, unusedcode, ckmetrics, checkstyle)
                             ※ 'javascript', 'css' and 'html' will be disabled when 'webapp' option isn't set, and 'css' and
                             'html' are disabled by default
     -a,--analysis           detailed analysis mode. (required more memory. If OOM exception occurred, use JVM '-Xmx' option
                             like '-Xmx1024m')
     -r,--rerun <arg>        specify previous output file to rerun with same options. ('project', 'src', 'binary',
                             'encoding', 'java', 'pmd', 'findbugs', 'include', 'exclude', 'mode', 'analysis', 'seperated',
                             'catalog', 'duplication', 'token' and 'webapp')
     -seperated              specify seperated output mode.
     -catalog                specify file catalog saving mode.
     -duplication <arg>      specify duplication detection mode. ('statement' or 'token', default : statement)
     -tokens <arg>           specify the minimum number of tokens when token-based duplication detection mode. (default :
                             100)

※ Java 11 버전 점검 : FindBugs, CKMetrics에 대하여 실험적 지원으로 일부 점검 결과가 틀릴 수 있음

### JavaScript 언어 점검

    $> java -jar Code-Analyst-2.10.6.jar -l javascript -p "프로젝트 위치" -s "."

#### Help (JavaScript)

    $> java -jar Code-Analyst-2.10.6.jar -l javascript --help
    usage: java -jar Code-Analyst-2.10.6.jar
     -l,--language <arg>   specify the language to analyze. ('Java', 'JavaScript', 'C#' or 'Python', default : "Java")
     -h,--help             show help.
     -p,--project <arg>    specify project base directory. (default: ".")
     -s,--src <arg>        specify source directories with comma separated. (default: "${project}\.")
     -d,--debug            debug mode.
     -e,--encoding <arg>   encoding of the source code. (default: UTF-8)
     -sonar <arg>          specify SonarQube issue ruleset(exclude filter) xml file.
                           ex:
                           <SonarIssueFilter>
                           <Exclude key="common-js:DuplicatedBlocks"/>
                           </SonarIssueFilter>
     -o,--output <arg>     specify result output file. (default : "result-[yyyyMMddHHmmss].[out|json]")
     -f,--format <arg>     specify result output file format(json, text, none). (default : text)
     -v,--version          display version info.
     -t,--timeout <arg>    specify internal ws timeout. (default : 100 min.)
     -include <arg>        specify include pattern(Ant-style) with comma separated. (e.g.: app/**/*.js)
     -exclude <arg>        specify exclude pattern(Ant-style) with comma separated. (e.g.: tests/**,tests-*/**,*-tests/**)
                             ※ If 'include' or 'exclude' option starts with '@' and has file name, the option value is read
                             from the file
     -m,--mode <arg>       specify analysis items with comma separated. If '-' specified in each mode, the mode is excluded.
                           (code-size, duplication, complexity, sonarjs)
     -a,--analysis         detailed analysis mode. (required more memory. If OOM exception occurred, use JVM '-Xmx' option
                           like '-Xmx1024m')
     -r,--rerun <arg>      specify previous output file to rerun with same options. ('project', 'src', 'encoding', 'sonar',
                           'include', 'exclude', 'mode', 'analysis', 'seperated' and 'catalog')
     -seperated            specify seperated output mode.
     -catalog              specify file catalog saving mode.
     -duplication <arg>      specify duplication detection mode. ('statement' or 'token', default : statement)
     -tokens <arg>           specify the minimum number of tokens when token-based duplication detection mode. (default :
                             100)

### C# 언어 점검

    $> java -jar Code-Analyst-2.10.6.jar -l C# -p "프로젝트 위치" -s "."

※ 참고로 프로젝트 위치는 Visual Studio 솔루션("*.sln") 파일이 있는 위치입니다.

#### Help (C#)

    $> java -jar Code-Analyst-2.10.6.jar -l C# --help
    usage: java -jar Code-Analyst-2.10.6.jar
     -l,--language <arg>   specify the language to analyze. ('Java', 'JavaScript', 'C#' or 'Python', default : "Java")
     -h,--help             show help.
     -p,--project <arg>    specify project base directory. (default: ".")
     -s,--src <arg>        specify source directories with comma separated. (default: "${project}\.")
     -d,--debug            debug mode.
     -e,--encoding <arg>   encoding of the source code. (default: UTF-8)
     -sonar <arg>          specify SonarQube issue ruleset(exclude filter) xml file.
                           ex:
                           <SonarIssueFilter>
                           <Exclude key="common-js:DuplicatedBlocks"/>
                           </SonarIssueFilter>
     -o,--output <arg>     specify result output file. (default : "result-[yyyyMMddHHmmss].[out|json]")
     -f,--format <arg>     specify result output file format(json, text, none). (default : text)
     -v,--version          display version info.
     -t,--timeout <arg>    specify internal ws timeout. (default : 100 min.)
     -include <arg>        specify include pattern(Ant-style) with comma separated. (e.g.: app/**/*.cs)
     -exclude <arg>        specify exclude pattern(Ant-style) with comma separated. (e.g.:
                           **/*.AssemblyInfo.cs,tests/**,tests-*/**,*-tests/**)
                           ※ If 'include' or 'exclude' option starts with '@' and has file name, the option value is read
                           from the file
                           - default exclusions pattern is added :
                           **/*.AssemblyInfo.cs,**/*.generated.cs,**/*Language.Designer.cs
     -m,--mode <arg>       specify analysis items with comma separated. If '-' specified in each mode, the mode is excluded.
                           (code-size, duplication, complexity, sonarcsharp)
     -a,--analysis         detailed analysis mode. (required more memory. If OOM exception occurred, use JVM '-Xmx' option
                           like '-Xmx1024m')
     -r,--rerun <arg>      specify previous output file to rerun with same options. ('project', 'src', 'encoding', 'sonar',
                           'include', 'exclude', 'mode', 'analysis', 'seperated', and 'catalog')
     -seperated            specify seperated output mode.
     -catalog              specify file catalog saving mode.
     -duplication <arg>      specify duplication detection mode. ('statement' or 'token', default : statement)
     -tokens <arg>           specify the minimum number of tokens when token-based duplication detection mode. (default :
                             100)

### Python 언어 점검

    $> java -jar Code-Analyst-2.10.6.jar -l Python -p "프로젝트 위치" -s "."

#### Help (Python)

    $> java -jar Code-Analyst-2.10.6.jar -l Python --help
    usage: java -jar Code-Analyst-2.10.6.jar
     -l,--language <arg>   specify the language to analyze. ('Java', 'JavaScript', 'C#' or 'Python', default : "Java")
     -h,--help             show help.
     -p,--project <arg>    specify project base directory. (default: ".")
     -s,--src <arg>        specify source directories with comma separated. (default: "${project}\.")
     -d,--debug            debug mode.
     -e,--encoding <arg>   encoding of the source code. (default: UTF-8)
     -sonar <arg>          specify SonarQube issue ruleset(exclude filter) xml file.
                           ex:
                           <SonarIssueFilter>
                           <Exclude key="common-js:DuplicatedBlocks"/>
                           </SonarIssueFilter>
     -o,--output <arg>     specify result output file. (default : "result-[yyyyMMddHHmmss].[out|json]")
     -f,--format <arg>     specify result output file format(json, text, none). (default : text)
     -v,--version          display version info.
     -t,--timeout <arg>    specify internal ws timeout. (default : 100 min.)
     -include <arg>        specify include pattern(Ant-style) with comma separated. (e.g.: app/**/*.py)
     -exclude <arg>        specify exclude pattern(Ant-style) with comma separated. (e.g.: tests/**,tests-*/**,*-tests/**)
                           ※ If 'include' or 'exclude' option starts with '@' and has file name, the option value is read
                           from the file
     -m,--mode <arg>       specify analysis items with comma separated. If '-' specified in each mode, the mode is excluded.
                           (code-size, duplication, complexity, sonarpython)
     -a,--analysis         detailed analysis mode. (required more memory. If OOM exception occurred, use JVM '-Xmx' option
                           like '-Xmx1024m')
     -r,--rerun <arg>      specify previous output file to rerun with same options. ('project', 'src', 'encoding', 'sonar',
                           'include', 'exclude', 'mode', 'analysis', 'seperated', and 'catalog')
     -seperated            specify seperated output mode.
     -catalog              specify file catalog saving mode.
     -duplication <arg>      specify duplication detection mode. ('statement' or 'token', default : statement)
     -tokens <arg>           specify the minimum number of tokens when token-based duplication detection mode. (default :
                             100)

### Version 정보

	$> java -jar Code-Analyst-2.10.6.jar --version
    Code Analyst : 2.10.6
      - Sonar Scanner API : 2.15.0.2182 (LGPL v3.0)
      - Sonar Scanner for MSBuild : 4.10.0.19059 (LGPL v3.0)
      - Sonar Server : 7.9.4.35981 (LGPL v3.0)
         [Plugins]
           - SonarJava : 6.3.2.22818 (LGPL v3.0)
           - SonarJS : 6.2.1.12157 (LGPL v3.0)
           - SonarC# : 8.6.1.17183 (LGPL v3.0)
           - SonarPython : 2.8.6204 (LGPL v3.0)
           - CSS/SCSS/Less : 1.2.1325 (LGPL v3.0)
           - HTML : 3.2.2082 (Apache v2.0)
      - PMD : 6.22.0 (BSD-style)
      - FindBugs(SpotBugs) : 4.0.6 (LGPL v3.0)
      - FindSecBugs : 1.10.1 (LGPL v3.0)
      - JDepend : 2.9.1-based modification (BSD-style)
      - CKJM : 1.9-based modification (Apache v2.0)
      - Node.js : 10.15.3 LTS (MIT)
        ※ Supported Platform : Windows/MacOS/Linux(x64)
      - ESLint : 5.16.0 (MIT)
      - CheckStyle : 8.35 (LGPL v2.1)
      - MS CodeAnalysis Metrics : 3.3.0 (Apache v2.0)
        ※ Supported Platform : Windows
      - Radon(python) : 3.0.3 (MIT)
        ※ required Python runtime (any platform)

    Default RuleSet
      - PMD : 91 ruleset (v5.4, RedCA Way Ruleset, '18.03)
      - SpotBugs(FindBugs) : 213 ruleset (v4.0.6, RedCA Way Ruleset, '20.09)
      - FindSecBugs : 81 rules (v1.10.1, RedCA Way Ruleset, '20.09)
      - SonarJava : 243 ruleset (v6.3.2, RedCA Way Ruleset, '20.09)
      - SonarCSharp : 155 ruleset (v8.6.1, RedCA Way Ruleset, '20.09)
      - SonarPython : 76 ruleset (v2.8, RedCA Way Ruleset, '20.09)
      - Web Resources :
          - JS : 89 ruleset (v6.2.1, RedCA Way Ruleset, '20.09)
          - CSS : CSS / Less / SCSS 27 ruleset (v1.2)
          - HTML : 28 ruleset (v3.2)
      - CheckStyle : 58 ruleset (RedCA Way Ruleset, '20.09)

    Copyright(c) 2018-20 By Samsung SDS (DevOps Group)


보다 **자세한 사항은 [Guide](GUIDE.md)를 참조**해 주세요.


## Contributing

버그 리포팅, 기능 개선 요청, pull request 요청 등은 [issue tracker](https://github.com/RedCA-Family/code-analyst/issues)를 활용해 주세요.

* 기타 연락처 : [codari@samsung.com](codari@samsung.com)


## History

- (2017.05) Initial Version released (v1.0)
- (2018.03) New Major Version released (v2.0)
- (2018.10) OSS Version released (v2.4)
- (2018.12) Design Metrics(CK Metrics) added (v2.5)
- (2019.01) Token based duplication detection mode added (v2.6)
- (2019.04) JavaScript language mode added & Node.js runtime provided for JavaScript/SonarJS analysis  (v2.7)
- (2019.07) CheckStyle check mode added (v2.8)
- (2019.09) C# and Python language mode added (v2.9)
- (2020.09) SonarQube 7.9(LTS) w/ related plugins and other OSS versions upgraded (v2.10)

## License

Code Analyst is licensed under the version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).
See [LICENSE](./LICENSE.txt) for the Code-Analyst full license text.
Licenses about 3rd-party library are in [./src/main/resources/LICENSES](./src/main/resources/LICENSES).

Unless required by applicable law or agreed to in writing, Software distributed as an "AS IS" BASIS WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND.
In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law (such as deliberate and grossly negligent acts) or agreed to in writing, shall any Contributor be liable to You for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work (including but not limited to damages for loss of goodwill, work stoppage, computer failure or malfunction, or any and all other commercial damages or losses), even if such Contributor has been advised of the possibility of such damages.
Accepting Warranty or Additional Liability. While redistributing the Work or Derivative Works thereof, You may choose to offer, and charge a fee for, acceptance of support, warranty, indemnity, or other liability obligations and/or rights consistent with this License. However, in accepting such obligations, You may act only on Your own behalf and on Your sole responsibility, not on behalf of any other Contributor, and only if You agree to indemnify, defend, and hold each Contributor harmless for any liability incurred by, or claims asserted against, such Contributor by reason of your accepting any such warranty or additional liability.
