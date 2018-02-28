package com.samsungsds.analyst.code.sonar.server;

import com.samsungsds.analyst.code.main.CliParser;

public interface SurrogateSonarServer {

	int startAndReturnPort(CliParser cli);

	void stop();

}
