package com.samsungsds.analyst.code.sonar.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.sonar.server.servlets.DefaultPluginInstalledResServlet;
import com.samsungsds.analyst.code.sonar.server.servlets.JarDownloadServlet;
import com.samsungsds.analyst.code.sonar.server.servlets.JavaPluginInstalledResServlet;
import com.samsungsds.analyst.code.sonar.server.servlets.MetricsResServlet;
import com.samsungsds.analyst.code.sonar.server.servlets.QualityProfilesServlet;
import com.samsungsds.analyst.code.sonar.server.servlets.SettingValuesResServlet;
import com.samsungsds.analyst.code.sonar.server.servlets.SubmitServlet;
import com.samsungsds.analyst.code.sonar.server.servlets.WebPluginInstalledResServlet;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

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
		if (cli.getIndividualMode().isCodeSize() || cli.getIndividualMode().isDuplication() || cli.getIndividualMode().isSonarJava()) {
			if (cli.getIndividualMode().isWebResources()) {
				handler.addServletWithMapping(DefaultPluginInstalledResServlet.class, "/api/plugins/installed");
			} else {
				handler.addServletWithMapping(JavaPluginInstalledResServlet.class, "/api/plugins/installed");
			}
		} else if (cli.getIndividualMode().isWebResources()) {
			handler.addServletWithMapping(WebPluginInstalledResServlet.class, "/api/plugins/installed");
		}
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
			URL url = new URL("http://localhost:" + serverPort + "/shutdown?token=" + secretPassword.toString());
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
