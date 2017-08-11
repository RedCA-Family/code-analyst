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
}
