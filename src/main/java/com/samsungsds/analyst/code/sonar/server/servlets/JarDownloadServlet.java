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
package com.samsungsds.analyst.code.sonar.server.servlets;

import java.io.File;
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
public class JarDownloadServlet extends HttpServlet {
	private static final Logger LOGGER = LogManager.getLogger(JarDownloadServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();

		LOGGER.debug("Download URL : {}", url);

		String filename = url.substring(url.lastIndexOf("/") + 1);

		if (request.getParameter("name") != null && !request.getParameter("name").equals("")) {
		    filename = request.getParameter("name");
        }

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", filename);
        response.setHeader(headerKey, headerValue);

        File jarFile = IOAndFileUtils.extractFileToTemp("/statics/" + filename);
        LOGGER.debug("File size : {}", jarFile.length());

		// Declare response encoding and types
		response.setContentType("application/octet-stream");
		response.setContentLength((int)jarFile.length());

		// Declare response status code
		response.setStatus(HttpServletResponse.SC_OK);

		// Write back response
		try (OutputStream outStream = response.getOutputStream()) {
			IOAndFileUtils.write(outStream, jarFile);
		}
	}

}
