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
package com.samsungsds.analyst.code.api.impl;

import com.samsungsds.analyst.code.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PythonCodeAnalystImpl extends GeneralLanguageCodeAnalystImpl {
	private static final Logger LOGGER = LogManager.getLogger(PythonCodeAnalystImpl.class);

    @Override
    protected Language getLanguageType() {
        return Language.PYTHON;
    }

    @Override
    protected String getSeparatedResultFileSuffix() {
        return "sonarpython";
    }

    @Override
    protected String getLanguageName() {
        return "Python";
    }

    @Override
    protected boolean isBinaryArgumentNecessary() {
        return false;
    }

    @Override
    protected boolean isInspectionCheck(AnalysisMode mode) {
        return mode.isSonarPython();
    }

    @Override
    protected String getInspectionModeName() {
        return "sonarpython";
    }
}
