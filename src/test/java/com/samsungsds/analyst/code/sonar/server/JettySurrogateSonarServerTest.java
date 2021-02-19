package com.samsungsds.analyst.code.sonar.server;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import com.samsungsds.analyst.code.api.Language;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.samsungsds.analyst.code.main.CliParser;

public class JettySurrogateSonarServerTest {

	private JettySurrogateSonarServer server;
	private int port;

	final String PREFIX = "http://localhost:";

	@Before public void
	init() {
		server = new JettySurrogateSonarServer();
		port = server.startAndReturnPort(new CliParser(new String[0], Language.JAVA));
	}

	private String[] servletPaths() {
		return new String[] {
				"/api/settings/values.protobuf",
				"/api/plugins/installed",
				"/api/metrics/search",
				"/deploy/plugins/findsecbugs-plugin-1.10.1.jar",
				"/api/qualityprofiles/search.protobuf",
				"/api/rules/search.protobuf?qprofile=AWPJpVp5CQBOyaqowaql",
				"/api/rules/list.protobuf",
				"/batch/project.protobuf"
		};
	}

	@Ignore
    @Test public void
    should_servlets_response_ok_when_server_is_running() throws IOException {

		for (String path : servletPaths()) {
			URL url = new URL(PREFIX+port+path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			assertThat(conn.getResponseCode(), is(HttpServletResponse.SC_OK));

			conn.disconnect();
		}

		server.stop();
	}

	@Test public void
	should_SubmitServlet_reponse_ok_when_server_is_running() throws IOException {

		String path = "/api/ce/submit";
		URL url = new URL(PREFIX+port+ path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");

		String body = "projectKey=local:test";
		conn.setDoOutput(true);
		OutputStream os = conn.getOutputStream();
		os.write(body.getBytes(StandardCharsets.UTF_8));
		os.flush();
		os.close();

		assertThat(conn.getResponseCode(), is(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	}

	@Ignore
	@Test(expected = ConnectException.class) public void
	should_throw_connectException_when_server_was_stopped() throws IOException {

		server.stop();

		URL url = new URL(PREFIX+port+ servletPaths()[0]);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.getResponseCode();
	}
}
