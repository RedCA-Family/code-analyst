package com.samsungsds.analyst.code.sonar.server;

public interface SurrogateSonarServer {
	int startAndReturnPort();
	
	void stop();
}
