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
package com.samsungsds.analyst.code.ckmetrics;

import com.samsungsds.analyst.code.ckmetrics.gr.spinellis.ckjm.CkjmOutputHandler;
import com.samsungsds.analyst.code.ckmetrics.gr.spinellis.ckjm.ClassMetrics;

import java.util.ArrayList;
import java.util.List;

public class CkMetricsResultHandler implements CkjmOutputHandler {

    private List<CkMetricsResult> ckMetricsResults = new ArrayList<>();

    @Override
    public void handleClass(String name, ClassMetrics c) {
        String filePath = getFilePathFromFqcn(name);
        ckMetricsResults.add(new CkMetricsResult(name, c.getWmc(), c.getNoc(), c.getRfc(), c.getCbo(), c.getDit(), c.getLcom(), filePath));
    }

    private String getFilePathFromFqcn(String name) {
        if (name.contains("$")) {
            name = name.substring(0, name.indexOf('$'));
        }

        return name.replace(".", "/") + ".java";
    }

    public List<CkMetricsResult> getCkMetricsResults() {
        return ckMetricsResults;
    }
}
