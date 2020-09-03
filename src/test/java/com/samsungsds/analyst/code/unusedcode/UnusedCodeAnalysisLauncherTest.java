package com.samsungsds.analyst.code.unusedcode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import com.samsungsds.analyst.code.api.Language;
import com.samsungsds.analyst.code.main.CliParser;
import org.junit.Before;
import org.junit.Test;

import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.unusedcode.type.CAMethod;

public class UnusedCodeAnalysisLauncherTest {

	private static String testInstanceKey = "0";

	private UnusedCodeAnalysisLauncher unusedCodeAnalysisLauncher;

	private MeasuredResult measuredResult;

	private CliParser cli = new CliParser(new String[0], Language.JAVA);

	@Before	public void
	setUp() throws IOException {

		cli = spy(new CliParser(new String[0], Language.JAVA));
		unusedCodeAnalysisLauncher = spy(new UnusedCodeAnalysisLauncher());

		setOptionsForTest();
		setMeasuredResultForTest();
	}

	private void
	setOptionsForTest() {

	}

	private void
	setMeasuredResultForTest() throws IOException {

		when(cli.getSrc()).thenReturn("");
		when(cli.getBinary()).thenReturn("");

		testInstanceKey = Integer.toString(Integer.parseInt(testInstanceKey)+1);//테스트 메서드를 수행할때마다 다른 instance 키를 생성한다.
		measuredResult = MeasuredResult.getInstance(testInstanceKey);
		measuredResult.setProjectDirectory(new File(".").getCanonicalPath());
		measuredResult.setProjectInfo(cli);
		measuredResult.initialize(false, false);
	}

	@Test public void
	should_detect_2_unused_fields_when_launcher_analysis_UClass() {
		String src = "src/main/java/com/samsungsds/analyst/code/test/UClass.java";
		String binary = "target/classes/com/samsungsds/analyst/code/test/UClass.class";

		unusedCodeAnalysisLauncher.setProjectBaseDir(".");
		unusedCodeAnalysisLauncher.setTargetSrc(src);
		unusedCodeAnalysisLauncher.setTargetBinary(binary);

		unusedCodeAnalysisLauncher.run(testInstanceKey);

		assertThat(measuredResult.getUnusedFieldCount(), is(2));
	}

	@Test public void
	should_detect_2_unused_methods_when_launcher_analysis_UClass() {
		String src = "src/main/java/com/samsungsds/analyst/code/test/UClass.java";
		String binary = "target/classes/com/samsungsds/analyst/code/test/UClass.class";

		unusedCodeAnalysisLauncher.setProjectBaseDir(".");
		unusedCodeAnalysisLauncher.setTargetSrc(src);
		unusedCodeAnalysisLauncher.setTargetBinary(binary);

		unusedCodeAnalysisLauncher.run(testInstanceKey);

		assertThat(measuredResult.getUnusedMethodCount(), is(3));
	}

	@Test public void
	should_detect_1_unused_constant_when_launcher_analysis_UClass() {
		String src = "src/main/java/com/samsungsds/analyst/code/test/UClass.java";
		String binary = "target/classes/com/samsungsds/analyst/code/test/UClass.class";

		unusedCodeAnalysisLauncher.setProjectBaseDir(".");
		unusedCodeAnalysisLauncher.setTargetSrc(src);
		unusedCodeAnalysisLauncher.setTargetBinary(binary);

		unusedCodeAnalysisLauncher.run(testInstanceKey);

		assertThat(measuredResult.getUnusedConstantCount(), is(1));
	}

	@Test public void
	sholud_detect_1_unused_class_when_launcher_analysis_unusedcode_package(){
		String src = "src/main/java/com/samsungsds/analyst/code/unusedcode";
		String binary = "target/classes/com/samsungsds/analyst/code/unusedcode";

		unusedCodeAnalysisLauncher.setProjectBaseDir(".");
		unusedCodeAnalysisLauncher.setTargetSrc(src);
		unusedCodeAnalysisLauncher.setTargetBinary(binary);

		unusedCodeAnalysisLauncher.run(testInstanceKey);

		assertThat(measuredResult.getUnusedClassCount(), is(1));
	}

	//@Ignore("package not supported since V2.2")
	@Test public void
	should_detect_2_unused_constant_when_lancher_analysis_unusedcode_package_with_filter() {
		String src = "src/main/java/com/samsungsds/analyst/code/unusedcode";
		String binary = "target/classes/com/samsungsds/analyst/code/unusedcode";

		unusedCodeAnalysisLauncher.setProjectBaseDir(".");
		unusedCodeAnalysisLauncher.setTargetSrc(src);
		unusedCodeAnalysisLauncher.setTargetBinary(binary);

		//unusedCodeAnalysisLauncher.setExclude("*ClassVisitor*");

		unusedCodeAnalysisLauncher.run(testInstanceKey);

		assertThat(measuredResult.getUnusedConstantCount(), is(2));
	}

	@Test public void
	should_parsed_in_int_and_string_parameterTypes_when_parse_desc(){
		String desc = "(ILjava/lang/String;)V";

		CAMethod method = new CAMethod();
		method.setDesc(desc);

		String[] expected = {"int", "java.lang.String"};
		assertThat(method.getParameterTypes(), is(expected));
	}

	@Test public void
	test() {
		String src = "src/test/java/com/samsungsds/analyst/code/unusedcode";
		String binary = "target/test-classes/com/samsungsds/analyst/code/unusedcode";

		unusedCodeAnalysisLauncher.setProjectBaseDir(".");
		unusedCodeAnalysisLauncher.setTargetSrc(src);
		unusedCodeAnalysisLauncher.setTargetBinary(binary);

		unusedCodeAnalysisLauncher.run(testInstanceKey);

		assertThat(measuredResult.getUnusedClassCount(), is(1));
	}
}
