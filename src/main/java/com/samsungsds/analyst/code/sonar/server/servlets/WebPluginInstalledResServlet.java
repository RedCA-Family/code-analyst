package com.samsungsds.analyst.code.sonar.server.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.samsungsds.analyst.code.util.IOAndFileUtils;

@Deprecated
@SuppressWarnings("serial")
public class WebPluginInstalledResServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Declare response encoding and types
		response.setContentType("application/json; charset=utf-8");

		// Declare response status code
		response.setStatus(HttpServletResponse.SC_OK);

		// Write back response
		try (OutputStream outStream = response.getOutputStream()) {
			IOAndFileUtils.write(outStream, "/statics/plugins_web.json");
		}
	}

}
