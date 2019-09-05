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

import com.samsungsds.analyst.code.api.ProgressEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class SonarProgressEventChecker implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(SonarProgressEventChecker.class);

    private final static int DEFAULT_INTERVAL_MILLISECOND = 7_000;
    private final static double DEFAULT_INCREASE_RATE = 1.1;

    private Thread worker;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean stopped = new AtomicBoolean(true);

    private ObserverManager observerManager;

    private int intervalMillisecond = DEFAULT_INTERVAL_MILLISECOND;
    private double increaseRate = DEFAULT_INCREASE_RATE;

    private AtomicBoolean codeSizeCompleted = new AtomicBoolean(false);
    private AtomicBoolean duplicationCompleted = new AtomicBoolean(false);
    private AtomicBoolean sonarJavaCompleted = new AtomicBoolean(false);
    private AtomicBoolean javascriptCompleted = new AtomicBoolean(false);
    private AtomicBoolean cssCompleted = new AtomicBoolean(false);
    private AtomicBoolean htmlCompleted = new AtomicBoolean(false);
    private AtomicBoolean sonarCSharpCompleted = new AtomicBoolean(false);
    private AtomicBoolean sonarPythonCompleted = new AtomicBoolean(false);

    public SonarProgressEventChecker(IndividualMode mode, ObserverManager observerManager, int targetFiles) {
        this(mode, observerManager);
        this.intervalMillisecond = (int)(DEFAULT_INTERVAL_MILLISECOND * (targetFiles / 100.0));

        if (targetFiles > 1_000) {
            increaseRate = 1.2;
        }
    }

    public SonarProgressEventChecker(IndividualMode mode, ObserverManager observerManager) {
        this.observerManager = observerManager;

        if (!mode.isCodeSize()) {
            codeSizeCompleted.set(true);
        }

        if (!mode.isDuplication()) {
            duplicationCompleted.set(true);
        }

        if (!mode.isSonarJava()) {
            sonarJavaCompleted.set(true);
        }

        if (!mode.isJavascript()) {
            javascriptCompleted.set(true);
        }

        if (!mode.isCss()) {
            cssCompleted.set(true);
        }

        if (!mode.isHtml()) {
            htmlCompleted.set(true);
        }

        if (!mode.isSonarCSharp()) {
            sonarCSharpCompleted.set(true);
        }

        if (!mode.isSonarPython()) {
            sonarPythonCompleted.set(true);
        }

        LOGGER.debug("Sonar Progress Event Start ... (interval : {}ms)", intervalMillisecond);
    }

    public void start() {
        LOGGER.debug("Sonar Progress Event Thread starting...");
        worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        LOGGER.debug("Sonar Process Event Thread stopping...");
        interrupt();

        synchronized(codeSizeCompleted) {
            if (!codeSizeCompleted.get()) {
                codeSizeCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.CODE_SIZE_COMPLETE);
            }
        }

        synchronized(duplicationCompleted) {
            if (!duplicationCompleted.get()) {
                duplicationCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.DUPLICATION_COMPLETE);
            }
        }

        synchronized(sonarJavaCompleted) {
            if (!sonarJavaCompleted.get()) {
                sonarJavaCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.SONARJAVA_COMPLETE);
            }
        }

        synchronized(javascriptCompleted) {
            if (!javascriptCompleted.get()) {
                javascriptCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.JAVASCRIPT_COMPLETE);
            }
        }

        synchronized(cssCompleted) {
            if (!cssCompleted.get()) {
                cssCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.CSS_COMPLETE);
            }
        }

        synchronized(htmlCompleted) {
            if (!htmlCompleted.get()) {
                htmlCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.HTML_COMPLETE);
            }
        }

        synchronized(sonarCSharpCompleted) {
            if (!sonarCSharpCompleted.get()) {
                sonarCSharpCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.SONARCSHARP_COMPLETE);
            }
        }

        synchronized(sonarPythonCompleted) {
            if (!sonarPythonCompleted.get()) {
                sonarPythonCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.SONARPYTHON_COMPLETE);
            }
        }
    }

    private void interrupt() {
        running.set(false);
        worker.interrupt();
    }

    boolean isRunning() {
        return running.get();
    }

    boolean isStopped() {
        return stopped.get();
    }

    @Override
    public void run() {
        running.set(true);
        stopped.set(false);

        while (running.get()) {
            try {
                Thread.sleep(intervalMillisecond);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.debug("Thread was interrupted...");
            }
            LOGGER.debug("Thread Process Event : {}, {}, {}, {}, {}, {}, {}, {}",
                codeSizeCompleted, duplicationCompleted, sonarJavaCompleted, javascriptCompleted, cssCompleted, htmlCompleted, sonarCSharpCompleted, sonarPythonCompleted);
            intervalMillisecond *= increaseRate;
            processEvent();
        }
        stopped.set(true);
    }

    private void processEvent() {
        synchronized(codeSizeCompleted) {
            if (!codeSizeCompleted.get()) {
                codeSizeCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.CODE_SIZE_COMPLETE);

                return;
            }
        }

        synchronized(duplicationCompleted) {
            if (!duplicationCompleted.get()) {
                duplicationCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.DUPLICATION_COMPLETE);

                return;
            }
        }

        synchronized(sonarJavaCompleted) {
            if (!sonarJavaCompleted.get()) {
                sonarJavaCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.SONARJAVA_COMPLETE);

                return;
            }
        }

        synchronized(javascriptCompleted) {
            if (!javascriptCompleted.get()) {
                javascriptCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.JAVASCRIPT_COMPLETE);

                return;
            }
        }

        synchronized(cssCompleted) {
            if (!cssCompleted.get()) {
                cssCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.CSS_COMPLETE);

                return;
            }
        }

        synchronized(htmlCompleted) {
            if (!htmlCompleted.get()) {
                htmlCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.HTML_COMPLETE);

                return;
            }
        }

        synchronized(sonarCSharpCompleted) {
            if (!sonarCSharpCompleted.get()) {
                sonarCSharpCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.SONARCSHARP_COMPLETE);

                return;
            }
        }

        synchronized(sonarPythonCompleted) {
            if (!sonarPythonCompleted.get()) {
                sonarPythonCompleted.set(true);
                observerManager.notifyObservers(ProgressEvent.SONARPYTHON_COMPLETE);

                return;
            }
        }
    }
}
