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

import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

@SuppressWarnings("serial")
public class PluginDownloadResServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger(PluginDownloadResServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getRequestURI();

        LOGGER.debug("Download URL : {}", url);

        String plugin = request.getParameter("plugin");
        String filename;
        String hash;

        switch (plugin) {
            case "csharp":
                filename = "sonar-csharp-plugin-8.6.1.17183.jar";
                hash = "885374536d1df7ea1a355ab41be74ff8";
                break;
            case "java":
                filename = "sonar-java-plugin-6.3.2.22818.jar";
                hash = "5718493547506efe17d67410c378e4eb";
                break;
            case "cssfamily":
                filename = "sonar-css-plugin-1.2.0.1325.jar";
                hash = "26232e683c1debb38b15b6a4f0801e21";
                break;
            case "go":
                filename = "sonar-go-plugin-1.6.0.719.jar";
                hash = "31899c2ada93bcca6e02085f1f47d6de";
                break;
            case "web":
                filename = "sonar-html-plugin-3.2.0.2082.jar";
                hash = "298a75a167830bfe6c1a75bf3a08fe19";
                break;
            case "javascript":
                filename = "sonar-javascript-plugin-6.2.1.12157.jar";
                hash = "52f5340dd05620cd162e2b9a45a57124";
                break;
            case "kotlin":
                filename = "sonar-kotlin-plugin-1.5.0.315.jar";
                hash = "2d9994a460180757f3fbad54f03e818e";
                break;
            case "python":
                filename = "sonar-python-plugin-2.8.0.6204.jar";
                hash = "7f02282f2c6196f47e6b35359b030a03";
                break;
            case "scala":
                filename = "sonar-scala-plugin-1.5.0.315.jar";
                hash = "45e3bfcd65e7578ed4cd89604de3b06f";
                break;
            case "typescript":
                filename = "sonar-typescript-plugin-2.1.0.4359.jar";
                hash = "1b0c24e5d9a55450dfadcd0a2c8bc5c8";
                break;
            default:
                throw new IllegalArgumentException("Requested plugin error : " + plugin);
        }

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", filename);
        response.setHeader(headerKey, headerValue);
        //response.setHeader("Sonar-Compression", "pack200");
        //response.setHeader("Sonar-UncompressedMD5", hash);
        response.setHeader("Sonar-MD5", hash);

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
