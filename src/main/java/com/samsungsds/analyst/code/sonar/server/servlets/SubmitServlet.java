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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.sonar.ReportFileReader;

@SuppressWarnings("serial")
public class SubmitServlet extends HttpServlet {
	private static final Logger LOGGER = LogManager.getLogger(SubmitServlet.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		String queryString = request.getQueryString() == null ? "" : "?" + request.getQueryString();

		LOGGER.debug("Requested URL : {}{}", url, queryString);

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		if (isMultipart) {
			LOGGER.info("Multipart...");
		}

		String projectKey = request.getParameter("projectKey");
		LOGGER.info("Project Key : {}", projectKey);

		String[] keys = projectKey.split(":");
		String instanceKey = keys[1];

		DiskFileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		try {
			// Parse the request to get file items.
			List<FileItem> fileItems = upload.parseRequest(request);

			for (FileItem fi : fileItems) {
				File file = File.createTempFile("code_analyst_", ".zip");
				file.deleteOnExit();

				LOGGER.info("File uploaded... file : {}", file);

				file.delete();

				// Write the file
				fi.write(file);

				try (ReportFileReader reader = new ReportFileReader(file, instanceKey)) {
					reader.read();
				}
			}
		} catch (Throwable ex) {
			throw new IllegalArgumentException(ex.toString(), ex);
		}

		// Declare response status code
		response.setStatus(HttpServletResponse.SC_OK);
	}
}

