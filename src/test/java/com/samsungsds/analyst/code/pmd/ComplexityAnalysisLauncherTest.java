package com.samsungsds.analyst.code.pmd;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import com.samsungsds.analyst.code.api.Language;
import org.junit.Before;
import org.junit.Test;

import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.main.IndividualMode;
import com.samsungsds.analyst.code.main.MeasuredResult;

public class ComplexityAnalysisLauncherTest {

	private static String testInstanceKey = "0";

	private ComplexityAnalysisLauncher complexityAnalysisLauncher;

	private MeasuredResult measuredResult;

	@Before	public void
	setUp() throws IOException {
		complexityAnalysisLauncher = spy(new ComplexityAnalysisLauncher());

		setOptionsForTest();
		setMeasuredResultForTest();
	}

	private void
	setOptionsForTest() {
		CliParser cli = new CliParser(new String[0], Language.JAVA);

		complexityAnalysisLauncher.addOption("-dir", cli.getProjectBaseDir() + File.separator + cli.getSrc());
		complexityAnalysisLauncher.addOption("-encoding", cli.getEncoding());
		complexityAnalysisLauncher.addOption("-version", cli.getJavaVersion());
		complexityAnalysisLauncher.addOption("-language", "java");
	}

	private void
	setMeasuredResultForTest() throws IOException {
		testInstanceKey = Integer.toString(Integer.parseInt(testInstanceKey)+1);//테스트 메서드를 수행할때마다 다른 instance 키를 생성한다.
		IndividualMode mode = new IndividualMode();//set default mode
		mode.setDefault();

		measuredResult = MeasuredResult.getInstance(testInstanceKey);
		measuredResult.setIndividualMode(mode);
		measuredResult.setProjectDirectory(new File(".").getCanonicalPath());
		measuredResult.initialize(false, false);
	}

	@Test public void
	should_add_a_option_to_arg_list_when_call_addOption() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = ComplexityAnalysisLauncher.class.getDeclaredField("arg");
		f.setAccessible(true);
        @SuppressWarnings("unchecked")
		List<String> arg = (List<String>) f.get(complexityAnalysisLauncher);

		int nextIdx = arg.size();

		complexityAnalysisLauncher.addOption("-format", "csv");
		assertThat(arg.get(nextIdx++), is("-format"));
		assertThat(arg.get(nextIdx++), is("csv"));
	}

	@Test public void
	should_add_option_name_only_when_option_value_is_null_or_blank() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Field f = ComplexityAnalysisLauncher.class.getDeclaredField("arg");
		f.setAccessible(true);
        @SuppressWarnings("unchecked")
		List<String> arg = (List<String>) f.get(complexityAnalysisLauncher);

		int nextIdx = arg.size();

		complexityAnalysisLauncher.addOption("-test", null);
		assertThat(arg.get(nextIdx++), is("-test"));

		try {
            arg.get(nextIdx);
        } catch (IndexOutOfBoundsException e) {
			assertThat(e, isA(IndexOutOfBoundsException.class));
		}


		complexityAnalysisLauncher.addOption("-test2", "");
		assertThat(arg.get(nextIdx++), is("-test2"));

		try {
			arg.get(nextIdx);
		} catch (IndexOutOfBoundsException e) {
			assertThat(e, isA(IndexOutOfBoundsException.class));
		}
	}

	@Test public void
	should_complexity_sum_greater_than_0_when_ComplexityAnalysisLauncher_run() {

		complexityAnalysisLauncher.run(testInstanceKey);

		int actualComplexitySum = measuredResult.getComplexitySum();
		assertTrue(actualComplexitySum > 0);
	}
}
