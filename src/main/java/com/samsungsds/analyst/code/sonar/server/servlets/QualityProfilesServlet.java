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
			// URL 호출 : /batch/project.protobuf?key=local%3Acom.samsungsds.analyst.code.main.App

			resourceName = "/statics/search.protobuf";
		} else if (url.equals("/api/rules/search.protobuf")) {
			// URL 호출
			// JavaScript : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWncZv1RIhyt1CDWUP11&p=1&ps=500
			// CSS        : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJooCyCQBOyaqowaiw&p=1&ps=500
			// Java       : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJpVp5CQBOyaqowaql&p=1&ps=500
			// Less       : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJoogvCQBOyaqowapc&p=1&ps=500
			// SCSS       : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJooTrCQBOyaqowal7&p=1&ps=500
			// Web        : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJooQ2CQBOyaqowakX&p=1&ps=500

			if (!queryString.contains("&p=1&")) {
				LOGGER.error("'p' parameter error : {}", queryString);
				throw new IllegalArgumentException("Request URL parameter error ('p' isn't 1");
			}

			if (queryString.contains("qprofile=AWncZv1RIhyt1CDWUP11")) {		// JavaScript
				resourceName = "/statics/AWncZv1RIhyt1CDWUP11.protobuf";
			} else if (queryString.contains("qprofile=AWPJooCyCQBOyaqowaiw")) {	// CSS
				resourceName = "/statics/AWPJooCyCQBOyaqowaiw.protobuf";
			} else if (queryString.contains("qprofile=AWPJpVp5CQBOyaqowaql")) {	// Java
				resourceName = "/statics/AWPJpVp5CQBOyaqowaql.protobuf";
			} else if (queryString.contains("qprofile=AWPJoogvCQBOyaqowapc")) {	// Less
				resourceName = "/statics/AWPJoogvCQBOyaqowapc.protobuf";
			} else if (queryString.contains("qprofile=AWPJooTrCQBOyaqowal7")) {	// SCSS
				resourceName = "/statics/AWPJooTrCQBOyaqowal7.protobuf";
			} else if (queryString.contains("qprofile=AWPJooQ2CQBOyaqowakX")) {	// Web
				resourceName = "/statics/AWPJooQ2CQBOyaqowakX.protobuf";
			}
		} else if (url.equals("/api/rules/list.protobuf")) {	// RuleSet이 변경되면 같이 변경하여야 함.. (parameter 없음)
			// URL 호출 : /api/rules/list.protobuf
			resourceName = "/statics/list.protobuf";
		} else if (url.equals("/batch/project.protobuf")) {
			// URL 호출 : /batch/project.protobuf?key=local%3Acom.samsungsds.analyst.code.main.App
			resourceName = "/statics/project.protobuf";
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
