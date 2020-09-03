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

import com.google.gson.Gson;
import com.samsungsds.analyst.code.api.Language;
import com.samsungsds.analyst.code.main.IndividualMode;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@SuppressWarnings("serial")
public class PluginInstalledResServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger(PluginInstalledResServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Declare response encoding and types
        response.setContentType("application/json; charset=utf-8");

        String userAgent = request.getHeader("User-Agent");
        LOGGER.info("User-Agent : {}", userAgent);

        String mode = null;

        String infoString = userAgent.split("\\s*/\\s*")[1];
        String[] info = infoString.split("\\s*§\\s*");

        for (String item : info) {
            String[] keyValue = item.split("\\s*=\\s*");
            String key = keyValue[0];

            if (key.equalsIgnoreCase("mode")) {
                mode = keyValue[1];
            }
        }

        if (mode == null) {
            throw new IllegalStateException("Mode Information not passed");
        }

        Gson gson = new Gson();
        IndividualMode individualMode = gson.fromJson(mode, IndividualMode.class);

        // Declare response status code
        response.setStatus(HttpServletResponse.SC_OK);

        // Write back response
        try (OutputStream outStream = response.getOutputStream()) {
            boolean isWritten = false;

            IOAndFileUtils.writeString(outStream, "{\n" + "\t\"plugins\": [\n");

            if (individualMode.getLanguageType() == Language.JAVA && (individualMode.isCodeSize() || individualMode.isDuplication() || individualMode.isSonarJava())) {
                IOAndFileUtils.write(outStream, "/statics/plugin_java.json");
                isWritten = true;
            }
            if ((individualMode.getLanguageType() == Language.JAVA && individualMode.isJavascript()) ||
                (individualMode.getLanguageType() == Language.JAVASCRIPT && (individualMode.isCodeSize() || individualMode.isDuplication() || individualMode.isSonarJS()))) {
                if (isWritten) {
                    IOAndFileUtils.writeString(outStream, ",\n");
                }
                IOAndFileUtils.write(outStream, "/statics/plugin_javascript.json");
                isWritten = true;
            }
            if (individualMode.isHtml()) {
                if (isWritten) {
                    IOAndFileUtils.writeString(outStream, ",\n");
                }
                IOAndFileUtils.write(outStream, "/statics/plugin_web.json");
                isWritten = true;
            }
            if (individualMode.isCss()) {
                if (isWritten) {
                    IOAndFileUtils.writeString(outStream, ",\n");
                }
                IOAndFileUtils.write(outStream, "/statics/plugin_css.json");
                isWritten = true;
            }
            if (individualMode.getLanguageType() == Language.CSHARP && (individualMode.isCodeSize() || individualMode.isDuplication() || individualMode.isSonarCSharp())) {
                if (isWritten) {
                    IOAndFileUtils.writeString(outStream, ",\n");
                }
                IOAndFileUtils.write(outStream, "/statics/plugin_csharp.json");
                isWritten = true;
            }
            if (individualMode.getLanguageType() == Language.PYTHON && (individualMode.isCodeSize() || individualMode.isDuplication() || individualMode.isSonarPython())) {
                if (isWritten) {
                    IOAndFileUtils.writeString(outStream, ",\n");
                }
                IOAndFileUtils.write(outStream, "/statics/plugin_python.json");
                isWritten = true;
            }

            IOAndFileUtils.writeString(outStream, "\t]\n" + "}");
        }
    }
}
