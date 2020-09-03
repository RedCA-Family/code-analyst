package com.samsungsds.analyst.code.sonar;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;

import com.samsungsds.analyst.code.api.Language;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sonar.api.CoreProperties;
import org.sonarsource.scanner.api.ScanProperties;
import org.sonarsource.scanner.api.ScannerProperties;
import org.sonarsource.scanner.api.internal.InternalProperties;

import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.sonar.server.JettySurrogateSonarServer;

public class SonarAnalysisLauncherTest {

	private static String testInstanceKey = "0";

	private SonarAnalysisLauncher sonar;

    private JettySurrogateSonarServer server;
	private int port;

	@Before
	public void setUp() throws IOException {
		CliParser cli = new CliParser(new String[0], Language.JAVA);

		server = new JettySurrogateSonarServer();
		port = server.startAndReturnPort(cli);

		sonar = new SonarAnalysisLauncher(cli.getProjectBaseDir(), cli.getSrc());

		setOptionsForTest();
		setMeasuredResultForTest();
	}

	@After
	public void destroy() {
		server.stop();
	}

	private void setOptionsForTest() {
		CliParser cli = new CliParser(new String[0], Language.JAVA);

		sonar.addProperty(ScannerProperties.HOST_URL, "http://localhost:" + port);
		sonar.addProperty(InternalProperties.SCANNER_APP, "SonarQubeScanner");
		sonar.addProperty(ScanProperties.PROJECT_SOURCE_ENCODING, cli.getEncoding());
		//sonar.addProperty(CoreProperties.ANALYSIS_MODE, CoreProperties.ANALYSIS_MODE_PUBLISH);

		sonar.addProperty(CoreProperties.PROJECT_KEY_PROPERTY, "local");

		sonar.addProperty("sonar.projectBaseDir", cli.getProjectBaseDir());
		sonar.addProperty("sonar.java.binaries", cli.getBinary());
		sonar.addProperty("sonar.sources", cli.getSrc());
		sonar.addProperty("sonar.java.source", cli.getJavaVersion());
		sonar.addProperty("sonar.ws.timeout", cli.getTimeout());
		sonar.addProperty("sonar.scanAllFiles", "true");
	}

	private void setMeasuredResultForTest() throws IOException {
		testInstanceKey = Integer.toString(Integer.parseInt(testInstanceKey) + 1);// 테스트 메서드를 수행할때마다 다른 instance 키를 생성한다.
        MeasuredResult measuredResult = MeasuredResult.getInstance(testInstanceKey);
		measuredResult.setProjectDirectory(new File(".").getCanonicalPath());
		measuredResult.initialize(false, false);
	}

	@Test
    @Ignore
	public void should_measuredResult_have_at_least_one_class_when_SonarAnalysisLauncher_run() {
		sonar.run(testInstanceKey);

		assertThat(MeasuredResult.getInstance(testInstanceKey).getClasses() > 0, is(true));
	}

}
