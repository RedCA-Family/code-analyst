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
package com.samsungsds.analyst.code.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FindFileUtils {
	private static final Logger LOGGER = LogManager.getLogger(FindFileUtils.class);
	
	public static final String COMMA_SPLITTER = "\\s*,\\s*";

	public static class Finder extends SimpleFileVisitor<Path> {
		private String path;
		
		private final PathMatcher matcher;
		private int numMatches = 0;

		Finder(String pattern) {
			matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
			LOGGER.info("Find File Pattern = glob:{}", pattern);
		}

		// Compares the glob pattern against the file or directory name.
		void find(Path file) {
			if (file != null && matcher.matches(file)) {
				numMatches++;
				LOGGER.info("Found file : {}", file);
				path = file.toString();
			}
		}

		// Prints the total number of matches to standard out.
		void done() {
			if (numMatches > 1) {
				LOGGER.info("Matched : {} (Last one selected)", numMatches);	
			} else {
				LOGGER.info("Matched : {}", numMatches);
			}
			
		}

		// Invoke the pattern matching method on each file.
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			find(file);
			return FileVisitResult.CONTINUE;
		}

		// Invoke the pattern matching method on each directory.
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
			find(dir);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) {
			LOGGER.warn("Failed : {}", exc);
			return FileVisitResult.CONTINUE;
		}
	}
	
	public static String getDirectoryWithFilenamePattern(String startDirectory, String pattern) throws IOException {

		if (startDirectory.contains(",")) {
			throw new IOException("Multiple source directories not supported : " + startDirectory);
		}

		Path startingDir = Paths.get(startDirectory);

		Finder finder = null;
		if (File.separator.equals("\\")) {
			finder = new Finder("**\\\\" + pattern + ".java");
		} else {
			finder = new Finder("**/" + pattern + ".java");
		}
		Files.walkFileTree(startingDir, finder);
		finder.done();

		if (finder.path == null) {
			throw new IOException("No files : " + pattern);
		}

		return finder.path;
	}

	public static String[] getFullDirectories(String prefix, String directoriesWithComma) {
		String[] directories = directoriesWithComma.split(COMMA_SPLITTER);

		List<String> list = new ArrayList<>();

		for (String dir : directories) {
			list.add(prefix + File.separator + dir);
		}

		return list.toArray(new String[0]);
	}

	public static String getMultiDirectoriesWithComma(String prefix, String directoriesWithComma) {
		return String.join(",", getFullDirectories(prefix, directoriesWithComma));
	}
}
