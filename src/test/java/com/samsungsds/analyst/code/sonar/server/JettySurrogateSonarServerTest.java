package com.samsungsds.analyst.code.sonar.server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.samsungsds.analyst.code.main.CliParser;

public class JettySurrogateSonarServerTest {
	
	private JettySurrogateSonarServer server;
	private int port;
	
	final String PREFIX = "http://localhost:";
	
	@Before public void 
	init() {
		server = new JettySurrogateSonarServer();
		port = server.startAndReturnPort(new CliParser(new String[0]));
	}
	
	private String[] servletPaths() {
		return new String[] {
				"/api/settings/values.protobuf",
				"/api/plugins/installed",
				"/api/metrics/search",
				"/deploy/plugins/findsecbugs-plugin-1.7.1.jar",
				"/api/qualityprofiles/search.protobuf",
				"/api/rules/search.protobuf?qprofile=AWEgcpHElIthtMf8fR6x",
				"/api/rules/list.protobuf",
				"/batch/project.protobuf"
		}; 
	}
	
//	@Test 
	public void 
	sholud_servlets_response_ok_when_server_is_running() throws IOException {

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
		
		assertThat(conn.getResponseCode(), is(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	}
	
	@Test(expected = ConnectException.class) public void 
	should_throw_connectException_when_server_was_stopped() throws IOException {
		
		server.stop();
		
		URL url = new URL(PREFIX+port+ servletPaths()[0]);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.getResponseCode();
	}
}
