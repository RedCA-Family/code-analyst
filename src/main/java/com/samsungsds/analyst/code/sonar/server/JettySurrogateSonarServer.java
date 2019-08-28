/*
Copyright 2018 Samsung SDS

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.samsungsds.analyst.code.sonar.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.UUID;

import com.samsungsds.analyst.code.sonar.server.servlets.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

/*
Request 처리 예시
- Load global settings : /api/settings/values.protobuf
- Load plugins index : /api/plugins/installed
- Load project repositories : /batch/project.protobuf?key=local%3Acom.samsungsds.analyst.code.main.App
- Load quality profiles : /api/qualityprofiles/search.protobuf?projectKey=local%3Acom.samsungsds.analyst.code.main.App/api/qualityprofiles/search.protobuf?projectKey=local%3Acom.samsungsds.analyst.code.main.App
- Load active rules (변경된 quality profile만 위 search.protobuf에서 ID 반영, QualityProfilesServlet.java도 반영 필요)
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWncZv1RIhyt1CDWUP11&p=1&ps=500
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJooCyCQBOyaqowaiw&p=1&ps=500
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJpVp5CQBOyaqowaql&p=1&ps=500
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJoogvCQBOyaqowapc&p=1&ps=500
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJooTrCQBOyaqowal7&p=1&ps=500
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJooQ2CQBOyaqowakX&p=1&ps=500
- Load metrics repository : /api/metrics/search?f=name,description,direction,qualitative,custom&ps=500&p=1
- Load server rules : /api/rules/list.protobuf
 */
public class JettySurrogateSonarServer implements SurrogateSonarServer {

	private static final Logger LOGGER = LogManager.getLogger(JettySurrogateSonarServer.class);

	private Server server = null;

	private UUID secretPassword = UUID.randomUUID();

	private int serverPort = 0;

	@Override
	public int startAndReturnPort(CliParser cli) {
		serverPort = IOAndFileUtils.findFreePort();

		if (serverPort == -1) {
			throw new IllegalStateException("No free port...");
		}

		server = new Server(serverPort);

		ServletHandler handler = new ServletHandler();

		handler.addServletWithMapping(SettingValuesResServlet.class, "/api/settings/values.protobuf");
		/*
		if (cli.getIndividualMode().isCodeSize() || cli.getIndividualMode().isDuplication() || cli.getIndividualMode().isSonarJava()) {
			if (cli.getIndividualMode().isWebResources()) {
				handler.addServletWithMapping(DefaultPluginInstalledResServlet.class, "/api/plugins/installed");
			} else {
				handler.addServletWithMapping(JavaPluginInstalledResServlet.class, "/api/plugins/installed");
			}
		} else if (cli.getIndividualMode().isWebResources()) {
			handler.addServletWithMapping(WebPluginInstalledResServlet.class, "/api/plugins/installed");
		}
		*/
		handler.addServletWithMapping(PluginInstalledResServlet.class, "/api/plugins/installed");

		handler.addServletWithMapping(MetricsResServlet.class, "/api/metrics/search");
		handler.addServletWithMapping(JarDownloadServlet.class, "/deploy/plugins/*");
		handler.addServletWithMapping(QualityProfilesServlet.class, "/api/qualityprofiles/search.protobuf");
		handler.addServletWithMapping(QualityProfilesServlet.class, "/api/rules/search.protobuf");
		handler.addServletWithMapping(QualityProfilesServlet.class, "/api/rules/list.protobuf");
		handler.addServletWithMapping(QualityProfilesServlet.class, "/batch/project.protobuf");
		handler.addServletWithMapping(SubmitServlet.class, "/api/ce/submit");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { new ShutdownHandler(secretPassword.toString(), false, true), handler });
		server.setHandler(handlers);

		try {
			server.start();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return serverPort;
	}

	@Override
	public void stop() {
		try {
			URL url = new URL("http://127.0.0.1:" + serverPort + "/shutdown?token=" + secretPassword.toString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.getResponseCode();
			LOGGER.info("Shutting down {} : {}", url, connection.getResponseMessage());
		} catch (SocketException e) {
			LOGGER.debug("Not running");
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
