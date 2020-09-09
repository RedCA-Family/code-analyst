package com.samsungsds.analyst.code.main;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import com.samsungsds.analyst.code.api.Language;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.samsungsds.analyst.code.main.result.OutputFileFormat;

public class CliParserTest {
	private final ByteArrayOutputStream outputContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errorContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;

	@Before
	public void setUp() {
		System.setOut(new PrintStream(outputContent));
		System.setErr(new PrintStream(errorContent));
	}

	@After
	public void tearDown() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

	@Test
	public void testForHelp() {
		// arrange
		String[] args = new String[] { "-h" };

		CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(false)));
		assertThat(outputContent.toString(),
				startsWith("usage: java -jar " + "Code-Analyst-" + Version.CODE_ANALYST + ".jar"));
	}

	@Test
	public void testForVersion() {
		// arrange
		String[] args = new String[] { "-v" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(false)));
		assertThat(outputContent.toString(), startsWith("Code Analyst : " + Version.CODE_ANALYST));
	}

	@Test
	public void testForDefaultOptions() {
		// arrange
		String[] args = new String[] {};

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getProjectBaseDir(), is("."));
		assertThat(cli.getSrc(), is("src" + File.separator + "main" + File.separator + "java"));
		assertThat(cli.getBinary(), is("target" + File.separator + "classes"));

		assertThat(cli.isDetailAnalysis(), is(false));
		assertThat(cli.isSeperatedOutput(), is(false));
		assertThat(cli.isSaveCatalog(), is(false));
	}

	@Test
	public void testForProjectBaseDirError() {
		// arrange
		String[] args = new String[] { "-p", "abc,def" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(false)));
		assertThat(outputContent.toString(), startsWith("The 'project' directory contains a comma."));
	}

	@Test
	public void testForProjectBaseDir() {
		// arrange
		String[] args = new String[] { "-p", "abc" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getProjectBaseDir(), is("abc"));
	}

	@Test
	public void testForSrcAndBinaryOption() {
		// arrange
		String[] args = new String[] { "-s", "abc", "-b", "def" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getSrc(), is("abc"));
		assertThat(cli.getBinary(), is("def"));
	}

	@Test
	public void testForMinorOptions() {
		// arrange
		String[] args = new String[] { "-library", "library", "-w", "webapp", "-d", "-e", "euc-kr", "-j", "1.6", "-o",
				"output", "-t", "100" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getLibrary(), is("library"));
		assertThat(cli.getWebapp(), is("webapp"));
		assertThat(cli.isDebug(), is(true));
		assertThat(cli.getJavaVersion(), is("1.6"));
		assertThat(cli.getOutput(), is("output"));
		assertThat(cli.getTimeout(), is("100"));
	}

	@Test
	public void testForRuleSetOptions() {
		// arrange
		String[] args = new String[] { "-pmd", "pmd", "-findbugs", "findbugs", "-sonar", "sonar" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getRuleSetFileForPMD(), is("pmd"));
		assertThat(cli.getRuleSetFileForFindBugs(), is("findbugs"));
		assertThat(cli.getRuleSetFileForSonar(), is("sonar"));
	}

	@Test
	public void testForFormatError() {
		// arrange
		String[] args = new String[] { "-f", "error" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(false)));
		assertThat(outputContent.toString(), startsWith("Option Error :"));
	}

	@Test
	public void testForFormat() {
		// arrange
		String[] args = new String[] { "-f", "text" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getFormat(), is(OutputFileFormat.TEXT));
	}

	@Test
	public void testForComplexityMode() {
		// arrange
		String[] args = new String[] { "-c", "class" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getMode(), is(MeasurementMode.ComplexityMode));
		assertThat(cli.getClassForCCMeasurement(), is("class"));
	}

	@Test
	public void testForIncludeExclude() {
		// arrange
		String[] args = new String[] { "-include", "include", "-exclude", "exclude" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getIncludes(), is("include"));
		assertThat(cli.getExcludes(), is("exclude"));
	}

	@Test
	public void testForModeWithComplexityMode() {
		// arrange
		String[] args = new String[] { "-m", "code-size", "-c", "class" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(false)));
		assertThat(outputContent.toString(), startsWith("Option Error :"));
	}

	@Test
	public void testForModeError() {
		// arrange
		String[] args = new String[] { "-m", "error" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(false)));
		assertThat(outputContent.toString(), startsWith("Option Error :"));
	}

	@Test
	public void testForModeWithMinusPrefix() {
		// arrange
		String[] args = new String[] { "-m", "code-size,-duplication" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getIndividualMode().isCodeSize(), is(true));
		assertThat(cli.getIndividualMode().isDuplication(), is(false));
		assertThat(cli.getIndividualMode().isComplexity(), is(true));	// default
	}

	@Test
	public void testForModeDefault() {
		// arrange
		String[] args = new String[] {};

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getIndividualMode().isCodeSize(), is(true));
		assertThat(cli.getIndividualMode().isDuplication(), is(true));
		assertThat(cli.getIndividualMode().isComplexity(), is(true));
		assertThat(cli.getIndividualMode().isSonarJava(), is(true));
		assertThat(cli.getIndividualMode().isPmd(), is(true));
		assertThat(cli.getIndividualMode().isFindBugs(), is(true));
		assertThat(cli.getIndividualMode().isFindSecBugs(), is(true));
		assertThat(cli.getIndividualMode().isJavascript(), is(false));	// false
		assertThat(cli.getIndividualMode().isCss(), is(false));
		assertThat(cli.getIndividualMode().isHtml(), is(false));
		assertThat(cli.getIndividualMode().isDependency(), is(true));
		assertThat(cli.getIndividualMode().isUnusedCode(), is(true));
		assertThat(cli.getIndividualMode().isCkMetrics(), is(true));
	}

	@Test
	public void testForModeDefaultWithWebappOption() {
		// arrange
		String[] args = new String[] {"-w", "webapp"};

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getIndividualMode().isCodeSize(), is(true));
		assertThat(cli.getIndividualMode().isDuplication(), is(true));
		assertThat(cli.getIndividualMode().isComplexity(), is(true));
		assertThat(cli.getIndividualMode().isSonarJava(), is(true));
		assertThat(cli.getIndividualMode().isPmd(), is(true));
		assertThat(cli.getIndividualMode().isFindBugs(), is(true));
		assertThat(cli.getIndividualMode().isFindSecBugs(), is(true));
		assertThat(cli.getIndividualMode().isJavascript(), is(true));	// true
		assertThat(cli.getIndividualMode().isCss(), is(false));
		assertThat(cli.getIndividualMode().isHtml(), is(false));
		assertThat(cli.getIndividualMode().isDependency(), is(true));
		assertThat(cli.getIndividualMode().isUnusedCode(), is(true));
		assertThat(cli.getIndividualMode().isCkMetrics(), is(true));
	}

	@Test
	public void testForModeWithOneMinusPrefix() {
		// arrange
		String[] args = new String[] {"-m", "-duplication"};

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getIndividualMode().isCodeSize(), is(true));
		assertThat(cli.getIndividualMode().isDuplication(), is(false));	// minus
		assertThat(cli.getIndividualMode().isComplexity(), is(true));
		assertThat(cli.getIndividualMode().isSonarJava(), is(true));
		assertThat(cli.getIndividualMode().isPmd(), is(true));
		assertThat(cli.getIndividualMode().isFindBugs(), is(true));
		assertThat(cli.getIndividualMode().isFindSecBugs(), is(true));
		assertThat(cli.getIndividualMode().isJavascript(), is(false));
		assertThat(cli.getIndividualMode().isCss(), is(false));
		assertThat(cli.getIndividualMode().isHtml(), is(false));
		assertThat(cli.getIndividualMode().isDependency(), is(true));
		assertThat(cli.getIndividualMode().isUnusedCode(), is(true));
		assertThat(cli.getIndividualMode().isCkMetrics(), is(true));
	}

	@Test
	public void testForModeWithAllOptions() {
		// arrange
		String[] args = new String[] {"-m", "codesize,duplication,complexity,sonarjava,pmd,findbugs,findsecbugs,javascript,css,html,dependency,-unusedcode,-ckmetrics"};

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getIndividualMode().isCodeSize(), is(true));
		assertThat(cli.getIndividualMode().isDuplication(), is(true));
		assertThat(cli.getIndividualMode().isComplexity(), is(true));
		assertThat(cli.getIndividualMode().isSonarJava(), is(true));
		assertThat(cli.getIndividualMode().isPmd(), is(true));
		assertThat(cli.getIndividualMode().isFindBugs(), is(true));
		assertThat(cli.getIndividualMode().isFindSecBugs(), is(true));
		assertThat(cli.getIndividualMode().isJavascript(), is(false));
		assertThat(cli.getIndividualMode().isCss(), is(false));
		assertThat(cli.getIndividualMode().isHtml(), is(false));
		assertThat(cli.getIndividualMode().isDependency(), is(true));
		assertThat(cli.getIndividualMode().isUnusedCode(), is(false));	// false
		assertThat(cli.getIndividualMode().isCkMetrics(), is(false));	// false
	}

	@Test
	public void testForEtcOptions() {
		// arrange
		String[] args = new String[] { "-a", "-seperated", "-catalog" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.isDetailAnalysis(), is(true));
		assertThat(cli.isSeperatedOutput(), is(true));
		assertThat(cli.isSaveCatalog(), is(true));
	}

	@Test
	public void testForRerunAllMode() {
		// arrange
		String result = saveTempFile("all");

		String[] args = new String[] { "-r", result };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getProjectBaseDir(), is("C:\\Project"));
		assertThat(cli.getSrc(), is("src\\main\\java"));
		assertThat(cli.getBinary(), is("target\\classes"));
		assertThat(cli.getJavaVersion(), is("1.8"));
		assertThat(cli.getJavaVersion(), is("1.8"));

		assertThat(cli.isDetailAnalysis(), is(false));
		assertThat(cli.isSeperatedOutput(), is(false));
		assertThat(cli.isSaveCatalog(), is(false));
		assertThat(cli.isTokenBased(), is(false));
		assertThat(cli.getMinimumTokens(), is(100));
	}

	@Test
	public void testForRerunAllModeWithOtherOptions() {
		// arrange
		String result = saveTempFile("all",
				"detailAnalysis = true",
				"seperatedOutput = true",
				"saveCatalog = true",
				"duplication = token",
				"tokens = 200"
				);

		String[] args = new String[] { "-r", result };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getProjectBaseDir(), is("C:\\Project"));
		assertThat(cli.getSrc(), is("src\\main\\java"));
		assertThat(cli.getBinary(), is("target\\classes"));
		assertThat(cli.getJavaVersion(), is("1.8"));
		assertThat(cli.getJavaVersion(), is("1.8"));

		assertThat(cli.isDetailAnalysis(), is(true));
		assertThat(cli.isSeperatedOutput(), is(true));
		assertThat(cli.isSaveCatalog(), is(true));
		assertThat(cli.isTokenBased(), is(true));
		assertThat(cli.getMinimumTokens(), is(200));
	}

	@Test
	public void testForRerunIndividualMode() {
		// arrange
		String result = saveTempFile("code-size,-duplication" );

		String[] args = new String[] { "-r", result };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getIndividualMode().isCodeSize(), is(true));
		assertThat(cli.getIndividualMode().isDuplication(), is(false));
	}

	@Test
	public void testForDuplicationStatementMethod() {
		// arrange
		String[] args = new String[] { "-duplication", "statement" };

        CliParser cli = new CliParser(args, Language.JAVA);


        // act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.isTokenBased(), is(false));
	}

	@Test
	public void testForDuplicationTokenMethod() {
		// arrange
		String[] args = new String[] { "-duplication", "token" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.isTokenBased(), is(true));
		assertThat(cli.getMinimumTokens(), is(100));
	}

	@Test
	public void testForDuplicationMethodError() {
		// arrange
		String[] args = new String[] { "-duplication", "error" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(false)));
		assertThat(outputContent.toString(), startsWith("Option Error :"));
	}

	@Test
	public void testForDuplicationTokenModified() {
		// arrange
		String[] args = new String[] { "-duplication", "token", "-tokens", "150" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.isTokenBased(), is(true));
		assertThat(cli.getMinimumTokens(), is(150));
	}

	@Test
	public void testForCheckDuplication() {
		// arrange
		String[] args = new String[] { "-s", "abc/,def", "-w", "def" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(false)));
		assertThat(outputContent.toString(), startsWith("Source Directories(include webapp dir.) overlapped. :"));
	}

	@Test
	public void testForParseError() {
		// arrange
		String[] args = new String[] { "-p", "-s" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(false)));
		assertThat(cli.getErrorMessage(), is("Failed to parse command line"));
	}

	@Test
	public void testForDirectoriesModified() {
		// arrange
		String[] args = new String[] { "-s", "/abc/def" };

        CliParser cli = new CliParser(args, Language.JAVA);

		// act
		boolean ret = cli.parse();

		// assert
		assertThat(ret, is(equalTo(true)));
		assertThat(cli.getSrc(), is("abc/def"));
	}

	private String saveTempFile(String mode, String... addStrings) {
		File tmp;
		try {
			tmp = File.createTempFile("result-", "ini");
			tmp.deleteOnExit();

			StringBuilder data = new StringBuilder();

			data.append("[Project]").append("\n");
			data.append("Target = C:\\Project").append("\n");
			data.append("Source = src\\main\\java").append("\n");
			data.append("Binary = target\\classes").append("\n");
			data.append("Encoding = UTF-8").append("\n");
			data.append("JavaVersion = 1.8").append("\n");
			data.append("Datetime = 2019-01-01 00:00:00").append("\n");
			data.append("ElapsedAnalysisTime = 2").append("\n");
			data.append("; Elapsed Analysis Time Unit : Minutes").append("\n");
			data.append("mode = ").append(mode).append("\n");
			data.append("version = 2.7").append("\n");
			data.append("engineVersion = 2.7.0").append("\n");

			for (String add : addStrings) {
				data.append(add).append("\n");
			}

			FileUtils.writeStringToFile(tmp, data.toString(), Charset.defaultCharset());
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}

		return tmp.getPath();
	}

    @Test
    public void testForCheckSourceDuplicationWithGoodCase() {
        // arrange
        String[] args = {
            "-s", "/src/main/java,/src/main/java-test"
        };

        CliParser cliParser = new CliParser(args, Language.JAVA);

        // act
        boolean result = cliParser.parse();

        // assert
        assertTrue(result);
        assertThat(cliParser.getErrorMessage(), not(containsString("Source Directories(include webapp dir.) overlapped. :")));
    }

    @Test
    public void testForCheckSourceDuplicationWithBadCase() {
        // arrange
        String[] args = {
            "-s", "/src/main/java,/src/main/java/some"
        };

        CliParser cliParser = new CliParser(args, Language.JAVA);

        // act
        boolean result = cliParser.parse();

        // assert
        assertFalse(result);
        assertThat(cliParser.getErrorMessage(), containsString("Source Directories(include webapp dir.) overlapped. :"));
    }
}
