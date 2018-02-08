package com.samsungsds.analyst.code.sonar;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sonar.api.CoreProperties;
import org.sonarsource.scanner.api.internal.batch.BatchIsolatedLauncher;
import org.sonarsource.scanner.api.internal.batch.IsolatedLauncher;
import org.sonarsource.scanner.api.internal.batch.LogOutput;

public class SonarAnalysisLauncher implements SonarAnalysis {
	private static final Logger LOGGER = LogManager.getLogger(SonarAnalysisLauncher.class);

	private final Map<String, String> globalProperties = new HashMap<>();
	private final LogOutput logOutput = new DefaultLogOutput();

	public SonarAnalysisLauncher(String projectBaseDir) {
		globalProperties.put("sonar.projectBaseDir", projectBaseDir);

		// sonar.projectKey, sonar.sources
	}

	public void addProperty(String key, String value) {
		globalProperties.put(key, value);
	}

	@Override
	public void run(String instanceKey) {

		String projectKey = globalProperties.get(CoreProperties.PROJECT_KEY_PROPERTY);

		globalProperties.put(CoreProperties.PROJECT_KEY_PROPERTY, projectKey + ":" + instanceKey);

		IsolatedLauncher launcher = new BatchIsolatedLauncher();

		launcher.execute(globalProperties, (formattedMessage, level) -> logOutput.log(formattedMessage, LogOutput.Level.valueOf(level.name())));

	}

	class DefaultLogOutput implements LogOutput {
		@Override
		public void log(String formattedMessage, Level level) {
			switch (level) {
			case TRACE:
			case DEBUG:
				LOGGER.debug(formattedMessage);
				break;
			case ERROR:
				LOGGER.error(formattedMessage);
				break;
			case WARN:
				LOGGER.warn(formattedMessage);
				break;
			case INFO:
			default:
				LOGGER.info(formattedMessage);
			}
		}
	}
}
