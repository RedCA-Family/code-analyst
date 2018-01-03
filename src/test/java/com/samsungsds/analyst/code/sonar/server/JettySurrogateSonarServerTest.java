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

public class JettySurrogateSonarServerTest {
	
	private JettySurrogateSonarServer server;
	private int port;
	
	@Before public void 
	init() {
		server = new JettySurrogateSonarServer();
	}
	
	private String[] allServletPathsToBeChecked() {
		return new String[] {
				"/batch/global",
				"/deploy/plugins/index.txt",
				"/deploy/plugins/findsecbugs-plugin-1.7.1.jar",
				"/api/qualityprofiles/search.protobuf",
				"/api/rules/search.protobuf?qprofile=java-sonar-way",
				"/api/rules/list.protobuf",
				"/batch/project.protobuf",
				"/batch/issues.protobuf"
		}; 
	}
	
	@Test public void 
	sholud_all_servlet_response_ok_when_server_is_running() throws IOException {
		String prefix = "http://localhost:";
		port = server.startAndReturnPort();
		
		for (String path : allServletPathsToBeChecked()) {
			URL url = new URL(prefix+port+path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			assertThat(conn.getResponseCode(), is(HttpServletResponse.SC_OK));
			
			conn.disconnect();
		}
		
		server.stop();
	}
	
	@Test(expected = ConnectException.class) public void 
	should_throw_connectException_when_server_was_stopped() throws IOException {
		String prefix = "http://localhost:";
		port = server.startAndReturnPort();
		
		server.stop();
		
		URL url = new URL(prefix+port+ allServletPathsToBeChecked()[0]);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.getResponseCode();
	}
}
