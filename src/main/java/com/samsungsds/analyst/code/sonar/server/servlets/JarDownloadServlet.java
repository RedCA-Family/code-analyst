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
public class JarDownloadServlet  extends HttpServlet {
	private static final Logger LOGGER = LogManager.getLogger(JarDownloadServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		
		LOGGER.debug("Download URL : {}", url);
		
		String filename = url.substring(url.lastIndexOf("/") + 1);
        
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

