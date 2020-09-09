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
import com.samsungsds.analyst.code.main.MeasuredResult;

public class PmdAnalysisLauncherTest {

	private static String testInstanceKey = "0";

	private PmdAnalysisLauncher pmdAnalysisLauncher;

	private MeasuredResult measuredResult;

	@Before	public void
	setUp() throws IOException {
		pmdAnalysisLauncher = spy(new PmdAnalysisLauncher());

		setOptionsForTest();
		setMeasuredResultForTest();
	}

	private void
	setOptionsForTest() {
		CliParser cli = new CliParser(new String[0], Language.JAVA);

		pmdAnalysisLauncher.addOption("-dir", cli.getProjectBaseDir() + File.separator + cli.getSrc());
		pmdAnalysisLauncher.addOption("-encoding", cli.getEncoding());
		pmdAnalysisLauncher.addOption("-version", cli.getJavaVersion());
		pmdAnalysisLauncher.addOption("-language", "java");
	}

	private void
	setMeasuredResultForTest() throws IOException {
		testInstanceKey = Integer.toString(Integer.parseInt(testInstanceKey)+1);//테스트 메서드를 수행할때마다 다른 instance 키를 생성한다.
		measuredResult = MeasuredResult.getInstance(testInstanceKey);
		measuredResult.setProjectDirectory(new File(".").getCanonicalPath());
		measuredResult.initialize(false, false);
	}

	@Test public void
	should_add_a_option_to_arg_list_when_call_addOption() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = PmdAnalysisLauncher.class.getDeclaredField("arg");
		f.setAccessible(true);
        @SuppressWarnings("unchecked")
		List<String> arg = (List<String>) f.get(pmdAnalysisLauncher);

		int nextIdx = arg.size();

		pmdAnalysisLauncher.addOption("-format", "csv");
		assertThat(arg.get(nextIdx++), is("-format"));
		assertThat(arg.get(nextIdx++), is("csv"));
	}

	@Test public void
	should_add_option_name_only_when_option_value_is_null_or_blank() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Field f = PmdAnalysisLauncher.class.getDeclaredField("arg");
		f.setAccessible(true);
        @SuppressWarnings("unchecked")
		List<String> arg = (List<String>) f.get(pmdAnalysisLauncher);

		int nextIdx = arg.size();

		pmdAnalysisLauncher.addOption("-test", null);
		assertThat(arg.get(nextIdx++), is("-test"));

		try {
			arg.get(nextIdx);
		} catch (IndexOutOfBoundsException e) {
			assertThat(e, isA(IndexOutOfBoundsException.class));
		}


		pmdAnalysisLauncher.addOption("-test2", "");
		assertThat(arg.get(nextIdx++), is("-test2"));

		try {
			arg.get(nextIdx);
		} catch (IndexOutOfBoundsException e) {
			assertThat(e, isA(IndexOutOfBoundsException.class));
		}
	}

	@Test public void
	sholud_add_specific_ruleset_when_exist_decalare_ruleset_option() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = PmdAnalysisLauncher.class.getDeclaredField("arg");
		f.setAccessible(true);
        @SuppressWarnings("unchecked")
		List<String> arg = (List<String>) f.get(pmdAnalysisLauncher);

		String testRulesetFile = "/statics/PMD_ruleset_SDS_Standard_20160826_test.xml";
		String path = pmdAnalysisLauncher.saveRuleSetFile(testRulesetFile).toString();
		pmdAnalysisLauncher.addOption("-rulesets", path);

		pmdAnalysisLauncher.run(testInstanceKey);

		assertTrue(arg.contains(path));
	}
//
//	@Test public void
//	should_pmd_count_all_of_measuredResult_is_1_when_pmdResultList_has_a_element() {
//
//		List<PmdResult> resultList = new ArrayList<>();
//		PmdResult pmdResult = new PmdResult();
//		pmdResult.setDataIn(0, "data");
//		pmdResult.setDataIn(1, "1");
//		pmdResult.setDataIn(2, "rule");
//		pmdResult.setDataIn(3, "1");
//		pmdResult.setDataIn(4, "description");
//		resultList.add(pmdResult);
//
//		doReturn(resultList).when(pmdAnalysisLauncher).parseCSV(Mockito.any(), Mockito.any());
//
//		pmdAnalysisLauncher.run(testInstanceKey);
//
//		int actualPmdCountAll = measuredResult.getPmdCountAll();
//		assertThat(actualPmdCountAll, is(1));
//	}
}
