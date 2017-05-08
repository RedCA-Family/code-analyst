package com.samsungsds.analyst.code.sonar.server.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.util.IOAndFileUtils;

@SuppressWarnings("serial")
public class QualityProfilesServlet extends HttpServlet {
	private static final Logger LOGGER = LogManager.getLogger(QualityProfilesServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		String queryString = request.getQueryString() == null ? "" : "?" + request.getQueryString();
		
		LOGGER.debug("Requested URL : {}{}", url, queryString);
		
		// Declare response encoding and types
		response.setContentType("application/octet-stream");

		// Declare response status code
		response.setStatus(HttpServletResponse.SC_OK);

		String resourceName = null;
		
		if (url.equals("/api/qualityprofiles/search.protobuf")) {
			resourceName = "/statics/search.protobuf";
		} else if (url.equals("/api/rules/search.protobuf")) {
			if (queryString.contains("qprofile=cs-sonar-way")) {
				resourceName = "/statics/cs-sonar-way.protobuf"; 
			} else if (queryString.contains("qprofile=java-sonar-way")) {
				resourceName = "/statics/java-sonar-way.protobuf"; 
			} else if (queryString.contains("qprofile=js-sonar-way")) {
				resourceName = "/statics/js-sonar-way.protobuf"; 
			}	
		} else if (url.equals("/api/rules/list.protobuf")) {
			resourceName = "/statics/list.protobuf";
		} else if (url.equals("/batch/project.protobuf")) {
			resourceName = "/statics/project.protobuf";
		} else if (url.equals("/batch/issues.protobuf")) {
			resourceName = "/statics/issues.protobuf";
		}
		
		if (resourceName == null) {
			throw new IllegalArgumentException("URL or queryString error...");
		}
		
		// Write back response
		try (OutputStream outStream = response.getOutputStream()) {
			IOAndFileUtils.write(outStream, resourceName);
		}
	}
}

