package com.samsungsds.analyst.code.main;

import com.samsungsds.analyst.code.api.ProgressEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class SonarProgressEventChecker implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(SonarProgressEventChecker.class);

    private final static int DEFAULT_INTERVAL_MILLISECOND = 10_000;
    private final static double DEFAULT_INCREASE_RATE = 1.3;

    private Thread worker;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean stopped = new AtomicBoolean(true);

    private App app;

    private int intervalMillisecond = DEFAULT_INTERVAL_MILLISECOND;

    private AtomicBoolean codeSizeCompleted = new AtomicBoolean(false);
    private AtomicBoolean duplicationCompleted = new AtomicBoolean(false);
    private AtomicBoolean sonarJavaCompleted = new AtomicBoolean(false);
    private AtomicBoolean webResourceCompleted = new AtomicBoolean(false);

    public SonarProgressEventChecker(IndividualMode mode, App app, int intervalMillisecond) {
        this(mode, app);
        this.intervalMillisecond = intervalMillisecond;
    }

    public SonarProgressEventChecker(IndividualMode mode, App app) {
        this.app = app;

        if (!mode.isCodeSize()) {
            codeSizeCompleted.set(true);
        }

        if (!mode.isDuplication()) {
            duplicationCompleted.set(true);
        }

        if (!mode.isSonarJava()) {
            sonarJavaCompleted.set(true);
        }

        if (!mode.isWebResource()) {
            webResourceCompleted.set(true);
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
                app.notifyObservers(ProgressEvent.CODE_SIZE_COMPLETE);
            }
        }

        synchronized(duplicationCompleted) {
            if (!duplicationCompleted.get()) {
                duplicationCompleted.set(true);
                app.notifyObservers(ProgressEvent.DUPLICATION_COMPLETE);
            }
        }

        synchronized(sonarJavaCompleted) {
            if (!sonarJavaCompleted.get()) {
                sonarJavaCompleted.set(true);
                app.notifyObservers(ProgressEvent.SONARJAVA_COMPLETE);
            }
        }

        synchronized(webResourceCompleted) {
            if (!webResourceCompleted.get()) {
                webResourceCompleted.set(true);
                app.notifyObservers(ProgressEvent.WEBRESOURCE_COMPLETE);
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
            LOGGER.debug("Thread Process Event : {}, {}, {}, {}", codeSizeCompleted, duplicationCompleted, sonarJavaCompleted, webResourceCompleted);
            intervalMillisecond *= DEFAULT_INCREASE_RATE;
            processEvent();
        }
        stopped.set(true);
    }

    private void processEvent() {
        synchronized(codeSizeCompleted) {
            if (!codeSizeCompleted.get()) {
                codeSizeCompleted.set(true);
                app.notifyObservers(ProgressEvent.CODE_SIZE_COMPLETE);

                return;
            }
        }

        synchronized(duplicationCompleted) {
            if (!duplicationCompleted.get()) {
                duplicationCompleted.set(true);
                app.notifyObservers(ProgressEvent.DUPLICATION_COMPLETE);

                return;
            }
        }

        synchronized(sonarJavaCompleted) {
            if (!sonarJavaCompleted.get()) {
                sonarJavaCompleted.set(true);
                app.notifyObservers(ProgressEvent.SONARJAVA_COMPLETE);

                return;
            }
        }

        synchronized(webResourceCompleted) {
            if (!webResourceCompleted.get()) {
                webResourceCompleted.set(true);
                app.notifyObservers(ProgressEvent.WEBRESOURCE_COMPLETE);

                return;
            }
        }
    }
}
