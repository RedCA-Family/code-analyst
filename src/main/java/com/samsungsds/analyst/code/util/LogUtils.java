package com.samsungsds.analyst.code.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

public class LogUtils {
    public static void unsetDebugLevel() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration conf = ctx.getConfiguration();
        conf.getLoggerConfig("com.samsungsds.analyst.code").setLevel(Level.INFO);
        conf.getLoggerConfig("org.sonar").setLevel(Level.INFO);
        ctx.updateLoggers(conf);
    }
}
