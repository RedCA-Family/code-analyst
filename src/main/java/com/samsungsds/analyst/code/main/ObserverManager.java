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
package com.samsungsds.analyst.code.main;

import com.samsungsds.analyst.code.api.AnalysisMode;
import com.samsungsds.analyst.code.api.AnalysisProgress;
import com.samsungsds.analyst.code.api.ProgressEvent;
import com.samsungsds.analyst.code.api.ProgressObserver;
import com.samsungsds.analyst.code.api.impl.AnalysisProgressMonitor;

import java.util.ArrayList;
import java.util.List;

public class ObserverManager {
    private final List<ProgressObserver> observerList = new ArrayList<>();

    private AnalysisProgressMonitor progressMonitor;

    public void addObserver(ProgressObserver observer) {
        observerList.add(observer);
    }

    public void removeObserver(ProgressObserver observer) {
        observerList.remove(observer);
    }

    public void notifyObservers(AnalysisProgress progress) {
        for (ProgressObserver observer : observerList) {
            observer.informProgress(progress);
        }
    }

    public void notifyObservers(ProgressEvent event) {
        if (progressMonitor != null) {
            notifyObservers(progressMonitor.getNextAnalysisProgress(event));
        }
    }

    public void setUpProgressMonitor(CliParser cli) {
        if (cli.getMode() == MeasurementMode.ComplexityMode) {
            AnalysisMode analysisMode = new AnalysisMode();

            analysisMode.setCodeSize(true);
            analysisMode.setComplexity(true);

            if (!observerList.isEmpty()) {
                progressMonitor = new AnalysisProgressMonitor(analysisMode);
            }
        } else {
            if (!observerList.isEmpty()) {
                progressMonitor = new AnalysisProgressMonitor(cli.getIndividualMode());
            }
        }
    }

    public boolean hasProgressMonitor() {
        return progressMonitor != null;
    }
}
