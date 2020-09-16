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
			// URL 호출 : /api/qualityprofiles/search.protobuf?projectKey=local%3Acom.samsungsds.analyst.code.main.App

			resourceName = "/statics/search.protobuf";
		} else if (url.equals("/api/rules/search.protobuf")) {
			// URL 호출
			// JavaScript : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt,updatedAt&activation=true&qprofile=AXRH1ytGAhLXeJN1dxQE&ps=500&p=1
			// CSS        : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt,updatedAt&activation=true&qprofile=AXQjDQ-cai_BF8caLvsh&ps=500&p=1
			// Java       : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt,updatedAt&activation=true&qprofile=AXQvgbsEcvFlxZDP-lAC&ps=500&p=1
			// Web        : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt,updatedAt&activation=true&qprofile=AXQjDReQai_BF8caLwNQ&ps=500&p=1
            // CSharp     : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt,updatedAt&activation=true&qprofile=AXRH3wgCAhLXeJN1dxS4&ps=500&p=1
            // Python     : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt,updatedAt&activation=true&qprofile=AXSUcsLtvMPN_CJo408b&ps=500&p=1

            // Go         : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt,updatedAt&activation=true&qprofile=AXQyuYmthzVTrCiH1EHi&ps=500&p=1
            // Kotlin     : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt,updatedAt&activation=true&qprofile=AXQjDRDcai_BF8caLvuX&ps=500&p=1
            // TypeScript : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt,updatedAt&activation=true&qprofile=AXQjDRwUai_BF8caLwal&ps=500&p=1
            // Scala      : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt,updatedAt&activation=true&qprofile=AXQjDRBGai_BF8caLvtX&ps=500&p=1
            // JSP        : /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt,updatedAt&activation=true&qprofile=AXQjDRDCai_BF8caLvt2&ps=500&p=1

            if (!queryString.contains("&p=1&") && !queryString.endsWith("&p=1")) {
				LOGGER.error("'p' parameter error : {}", queryString);
				throw new IllegalArgumentException("Request URL parameter error ('p' isn't 1)");
			}

			if (queryString.contains("qprofile=AXRH1ytGAhLXeJN1dxQE")) {		// JavaScript
				resourceName = "/statics/AXRH1ytGAhLXeJN1dxQE.protobuf";
			} else if (queryString.contains("qprofile=AXQjDQ-cai_BF8caLvsh")) {	// CSS / Less / SCSS
				resourceName = "/statics/AXQjDQ-cai_BF8caLvsh.protobuf";
			} else if (queryString.contains("qprofile=AXQvgbsEcvFlxZDP-lAC")) {	// Java
				resourceName = "/statics/AXQvgbsEcvFlxZDP-lAC.protobuf";
			} else if (queryString.contains("qprofile=AXQjDReQai_BF8caLwNQ")) {	// Web
				resourceName = "/statics/AXQjDReQai_BF8caLwNQ.protobuf";
			} else if (queryString.contains("qprofile=AXRH3wgCAhLXeJN1dxS4")) { // CSharp
			    resourceName = "/statics/AXRH3wgCAhLXeJN1dxS4.protobuf";
            } else if (queryString.contains("qprofile=AXSUcsLtvMPN_CJo408b")) { // Python
                resourceName = "/statics/AXSUcsLtvMPN_CJo408b.protobuf";
            } else if (queryString.contains("qprofile=AXQyuYmthzVTrCiH1EHi")) { // Go
                resourceName = "/statics/AXQyuYmthzVTrCiH1EHi.protobuf";
            } else if (queryString.contains("qprofile=AXQjDRDcai_BF8caLvuX")) { // Kotlin
                resourceName = "/statics/AXQjDRDcai_BF8caLvuX.protobuf";
            } else if (queryString.contains("qprofile=AXQjDRwUai_BF8caLwal")) { // TypeScript
                resourceName = "/statics/AXQjDRwUai_BF8caLwal.protobuf";
            } else if (queryString.contains("qprofile=AXQjDRBGai_BF8caLvtX")) { // Scala
                resourceName = "/statics/AXQjDRBGai_BF8caLvtX.protobuf";
            } else if (queryString.contains("qprofile=AXQjDRDCai_BF8caLvt2")) { // JSP
                resourceName = "/statics/AXQjDRDCai_BF8caLvt2.protobuf";
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
