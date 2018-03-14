package com.samsungsds.analyst.code.main;

import com.samsungsds.analyst.code.api.ProgressEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class SonarProgressEventChecker implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(SonarProgressEventChecker.class);

    private final static int DEFAULT_INTERVAL_MILLESECOND = 10_000;
    private final static double DEFAULT_INCREASE_RATE = 1.3;

    private Thread worker;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean stopped = new AtomicBoolean(true);

    private App app;

    private int intervalMillesecond = DEFAULT_INTERVAL_MILLESECOND;

    private boolean codeSizeCompleted = false;
    private boolean duplicationCompleted = false;
    private boolean sonarJavaCompleted = false;
    private boolean webResourceCompleted = false;

    public SonarProgressEventChecker(IndividualMode mode, App app, int intervalMillesecond) {
        this(mode, app);
        this.intervalMillesecond = intervalMillesecond;
    }

    public SonarProgressEventChecker(IndividualMode mode, App app) {
        this.app = app;

        if (!mode.isCodeSize()) {
            codeSizeCompleted = true;
        }

        if (!mode.isDuplication()) {
            duplicationCompleted = true;
        }

        if (!mode.isSonarJava()) {
            sonarJavaCompleted = true;
        }

        if (!mode.isWebResource()) {
            webResourceCompleted = true;
        }

        LOGGER.debug("Sonar Progress Event Start ... (interval : {}ms)", intervalMillesecond);
    }

    public void start() {
        LOGGER.debug("Sonar Progress Event Thread starting...");
        worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        LOGGER.debug("Sonar Process Event Thread stopping...");
        interrupt();

        if (!codeSizeCompleted) {
            codeSizeCompleted = true;
            app.notifyObservers(ProgressEvent.CODE_SIZE_COMPLETE);
        }

        if (!duplicationCompleted) {
            duplicationCompleted = true;
            app.notifyObservers(ProgressEvent.DUPLICATION_COMPLETE);
        }

        if (!sonarJavaCompleted) {
            sonarJavaCompleted = true;
            app.notifyObservers(ProgressEvent.SONARJAVA_COMPLETE);
        }

        if (!webResourceCompleted) {
            webResourceCompleted = true;
            app.notifyObservers(ProgressEvent.WEBRESOURCE_COMPLETE);
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
                Thread.sleep(intervalMillesecond);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.debug("Thread was interrupted...");
            }
            LOGGER.debug("Thread Process Event : {}, {}, {}, {}", codeSizeCompleted, duplicationCompleted, sonarJavaCompleted, webResourceCompleted);
            intervalMillesecond *= DEFAULT_INCREASE_RATE;
            processEvent();
        }
        stopped.set(true);
    }

    private void processEvent() {
        if (!codeSizeCompleted) {
            codeSizeCompleted = true;
            app.notifyObservers(ProgressEvent.CODE_SIZE_COMPLETE);

            return;
        }

        if (!duplicationCompleted) {
            duplicationCompleted = true;
            app.notifyObservers(ProgressEvent.DUPLICATION_COMPLETE);

            return;
        }

        if (!sonarJavaCompleted) {
            sonarJavaCompleted = true;
            app.notifyObservers(ProgressEvent.SONARJAVA_COMPLETE);

            return;
        }

        if (!webResourceCompleted) {
            webResourceCompleted = true;
            app.notifyObservers(ProgressEvent.WEBRESOURCE_COMPLETE);

            return;
        }
    }
}
