# Code Analyst Project

[![Build Status](https://travis-ci.org/RedCA-Family/code-analyst.svg?branch=development)](https://travis-ci.org/RedCA-Family/code-analyst)

Code Analyst는 코드 품질에 대한 다양한 지표를 통합적으로 확인할 수 있는 프로그램입니다. 

기본적으로 측정되는 코드 규모(프로그램 개수, loc 등)뿐만 아니라 중복도, 복잡도, Inspection 결과(PMD, FindBugs) 등을 확인할 수 있습니다.

※ 현재는 Java만 지원하며, 향후 다양한 언어로 확대될 예정임


## Build & Installation

Code Analyst를 실행하기 위해서는 Java 1.8이 필요하며, 하나의 통합 jar로 구성되어 있습니다. (참고로 분석 대상 프로젝트의 JDK 버전과는 별도임)

빌드는 다음과 같이 maven을 통해 수행합니다.(사전에 maven 혹은 이클립스 등 컴파일 도구가 필요합니다.)

	git clone https://github.com/RedCA-Family/code-analyst.git
	cd code-analyst
	mvn clean package

생성된 jar 파일을 임의의 디렉토리에 위치시키고 아래 사용법 과 같이 실행하여 사용합니다.


## API 사용 ##
API 형태로 사용하기 위해서는 Maven dependencies에 다음과 같은 2개의 dependency를 추가하면 됩니다. 
	
	<dependency>
		<groupId>com.samsungsds.analyst</groupId>
		<artifactId>code-analyst</artifactId>
		<version>2.5.1</version>
	</dependency>

API 활용에 대한 사항은 [Guide](GUIDE.md)를 참조해 주세요.
  

## Usage
CLI(Command Line Interface) 형태로 사용하기 위해서는 다음과 같이 실행합니다.

    $> java -jar Code-Analyst-2.5.1.jar -p "프로젝트 위치" -s "src\main\java" -b "target\classes"
 
기본적으로 --project 옵션을 통해 분석하고자 하는 프로젝트 위치를 지정합니다. 

이와 함께 --src, --binary 옵션으로 소스 디렉토리와 binary 디렉토리(컴파일된 class 파일 생성 위치)를 지정합니다. (생략되면 maven 프로젝트 기준으로 설정되며, "--project" 옵션에 대한 상대 경로로 지정해야 합니다.)


### Help

	$> java -jar Code-Analyst-2.5.1.jar --help
    usage: java -jar Code-Analyst-2.4.0.jar
     -h,--help               show help.
     -p,--project <arg>      specify project base directory. (default: ".")
     -s,--src <arg>          specify source directories with comma separated. (default: "${project}\src\main\java")
     -b,--binary <arg>       specify binary directories with comma separated. (default: "${project}\target\classes")
     -l,--library <arg>      specify library directory, jar files contained.
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
     -o,--output <arg>       specify result output file. (default : "result-[yyyyMMddHHmmss].[out|json]")
     -f,--format <arg>       specify result output file format(json, text, none). (default : text)
     -v,--version            display version info.
     -t,--timeout <arg>      specify internal ws timeout. (default : 100 min.)
     -c,--complexity <arg>   specify class name(glob pattern) to be measured. (Cyclomatic Complexity Measurement mode)
     -w,--webapp <arg>       specify webapp root directory to be inspected. If it's not specified, 'javascript', 'css',
                             'html' analysis items will be disabled.
                             ※ webapp directory should not overlap the src directories.
     -include <arg>          specify include pattern(Ant-style) with comma separated. (eg: com/sds/**/*.java)
     -exclude <arg>          specify exclude pattern(Ant-style) with comma separated. (eg: com/sds/**/*VO.java)
     -m,--mode <arg>         specify analysis items with comma separated. If '-' specified in each mode, the mode is
                             excluded. (code-size, duplication, complexity, sonarjava, pmd, findbugs, findsecbugs,
                             javascript, css, html, dependency, unusedcode, ckmetrics)
                             ※ 'javascript', 'css' and 'html' will be disabled when 'webapp' option isn't set, and 'css' and
                             'html' are disabled by default
     -a,--analysis           detailed analysis mode. (required more memory. If OOM exception occured, use JVM '-Xmx' option
                             like '-Xmx1024m')
     -r,--rerun <arg>        specify previous output file to rerun with same options. ('project', 'src', 'binary',
                             'encoding', 'java', 'pmd', 'findbugs', 'include', 'exclude', 'mode', 'analysis', 'seperated',
                             'catalog' and 'webapp')
     -seperated              specify seperated output mode.
     -catalog                specify file catalog saving mode.


### Version 정보

	$> java -jar Code-Analyst-10.jar --version
    Code Analyst : 2.5.1
      - Sonar Scanner : 2.10.0.1189 (LGPL v3.0)
      - Sonar Server : 6.7.4 (LGPL v3.0)
         [Plugins]
           - SonarJava : 5.1.1.13214 (LGPL v3.0)
           - SonarJS : 4.1.0.6085 (LGPL v3.0)
           - CSS/SCSS/Less : 3.1 (LGPL v3.0)
           - Web : 2.5.0.476 (Apache v2.0)
      - PMD : 5.4.6 (BSD-style)
      - FindBugs : 3.0.1 (LGPL v3.0)
      - FindSecBugs : 1.7.1 (LGPL v3.0)
      - JDepend : 2.9.1-based modification (BSD-style)
      - CKJM : 1.9 (Apache v2.0)
    
    Default RuleSet
      - PMD : 91 ruleset (v5.4, RedCA Way Ruleset, '18.03)
      - FindBugs : 214 ruleset (v3.0.1, RedCA Way Ruleset, '18.03)
      - FindSecBugs : 81 rules (v1.7.1, RedCA Way Ruleset, '18.06)
      - SonarJava : 227 ruleset (v4.15, RedCA Way Ruleset, '18.03)
      - Web Resources :
          - JS : 109 ruleset (v4.1, RedCA Way Ruleset, '18.05)
          - CSS : CSS 71 / Less 71 / SCSS 82 ruleset (v3.1)
          - HTML : 16 ruleset (v2.5)
    
    Copyright(c) 2018 By Samsung SDS (Code Quality Group)


보다 자세한 사항은 [Guide](GUIDE.md)를 참조해 주세요. 


## Contributing

버그 리포팅, 기능 개선 요청, pull request 요청 등은 [issue tracker](https://github.com/RedCA-Family/code-analyst/issues)를 활용해 주세요.

* 기타 연락처 : [codari@samsung.com](codari@samsung.com)


## History

- (2017.05) Initial Version released (v1.0)
- (2018.03) New Major Version released (v2.0)
- (2018.10) OSS Version released (v2.4)
- (2018.12) Design Metrics(CK Metrics) added (v2.5)


## License

Code Analyst is licensed under the version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).   
See [LICENSE](./LICENSE.txt) for the Code-Analyst full license text.  
Licenses about 3rd-party library are in [./src/main/resources/LICENSES](./src/main/resources/LICENSES).  

Unless required by applicable law or agreed to in writing, Software distributed as an "AS IS" BASIS WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND.    
In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law (such as deliberate and grossly negligent acts) or agreed to in writing, shall any Contributor be liable to You for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work (including but not limited to damages for loss of goodwill, work stoppage, computer failure or malfunction, or any and all other commercial damages or losses), even if such Contributor has been advised of the possibility of such damages.  
Accepting Warranty or Additional Liability. While redistributing the Work or Derivative Works thereof, You may choose to offer, and charge a fee for, acceptance of support, warranty, indemnity, or other liability obligations and/or rights consistent with this License. However, in accepting such obligations, You may act only on Your own behalf and on Your sole responsibility, not on behalf of any other Contributor, and only if You agree to indemnify, defend, and hold each Contributor harmless for any liability incurred by, or claims asserted against, such Contributor by reason of your accepting any such warranty or additional liability.
