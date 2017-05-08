package com.samsungsds.analyst.code.sonar.server;

import org.apache.log4j.LogManager;
import org.eclipse.jetty.util.log.AbstractLogger;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class Log4jLog extends AbstractLogger {
	private final static boolean DEBUG_MODE = false;
	
	private final org.apache.log4j.Logger _logger;
	
	public Log4jLog() {
		this("org.eclipse.jetty");
	}
	
	public Log4jLog(String name) {
		_logger = LogManager.getLogger(name);
	}

	@Override
	public String getName() {
		return _logger.getName();
	}

	@Override
	public void warn(String msg, Object... args) {
		if (_logger.isWarnEnabled()) {
			FormattingTuple ft = MessageFormatter.arrayFormat(msg, args);
			
			_logger.warn(ft.getMessage());
		}
	}

	@Override
	public void warn(Throwable thrown) {
		_logger.warn("", thrown);
	}

	@Override
	public void warn(String msg, Throwable thrown) {
		_logger.warn(msg, thrown);
	}

	@Override
	public void info(String msg, Object... args) {
		if (_logger.isInfoEnabled()) {
			FormattingTuple ft = MessageFormatter.arrayFormat(msg, args);
		
			_logger.info(ft.getMessage());
		}
	}

	@Override
	public void info(Throwable thrown) {
		_logger.info("", thrown);
	}

	@Override
	public void info(String msg, Throwable thrown) {
		_logger.info(msg, thrown);
	}

	@Override
	public boolean isDebugEnabled() {
		return _logger.isDebugEnabled();
	}

	@Override
	public void setDebugEnabled(boolean enabled) {
		warn("setDebugEnabled not implemented", null, null);
	}

	@Override
	public void debug(String msg, Object... args) {
		if (!DEBUG_MODE) {
			return;
		}
		
		if (_logger.isDebugEnabled()) {
			FormattingTuple ft = MessageFormatter.arrayFormat(msg, args);
		
			_logger.debug(ft.getMessage());
		}
	}

	@Override
	public void debug(Throwable thrown) {
		if (!DEBUG_MODE) {
			return;
		}
		
		_logger.debug("", thrown);
	}

	@Override
	public void debug(String msg, Throwable thrown) {
		if (!DEBUG_MODE) {
			return;
		}
		
		_logger.debug(msg, thrown);
	}

	@Override
	public void ignore(Throwable ignored) {
		if (Log.__ignored) {
            debug(Log.IGNORED, ignored);
        }
	}

	@Override
	protected Logger newLogger(String fullname) {
		return new Log4jLog(fullname);
	}
	
    @Override
    public String toString() {
        return _logger.toString();
    }
}
