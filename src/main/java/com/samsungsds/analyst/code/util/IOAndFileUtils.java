package com.samsungsds.analyst.code.util;

import java.io.*;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class IOAndFileUtils {
	private final static int BUFFER_SIZE = 8192;
	
	public final static String CR_LF = System.getProperty("line.separator");
	
	public static int findFreePort() {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		} catch (IOException e) {
			// no-op
		}
		return -1;
	}

	public static void writeString(OutputStream output, String str) throws IOException {
		output.write(str.getBytes("UTF-8"));
	}
	
	public static long write(OutputStream output, String resourcePath) throws IOException {
		URL url = IOAndFileUtils.class.getResource(resourcePath);
		
		long nread = 0L;
		
		try (InputStream in = url.openStream()) {
			
	        byte[] buf = new byte[BUFFER_SIZE];
	        int n;
	        while ((n = in.read(buf)) > 0) {
	        	output.write(buf, 0, n);
	            nread += n;
	        }
		}
		
		return nread;
	}
	
	public static long write(OutputStream output, File file) throws IOException {		
		long nread = 0L;
		
		try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
			
	        byte[] buf = new byte[BUFFER_SIZE];
	        int n;
	        while ((n = in.read(buf)) > 0) {
	        	output.write(buf, 0, n);
	            nread += n;
	        }
		}
		
		return nread;
	}
	
	public static File extractFileToTemp(String filename) {
		
		URL url = IOAndFileUtils.class.getResource(filename);

		if (filename.lastIndexOf("/") > 0) {
			filename = filename.substring(filename.lastIndexOf("/") + 1);
		}
		
		int lastIndex = filename.lastIndexOf(".");
		String filenameWithoutType = lastIndex > 0 ? filename.substring(0, lastIndex) : filename;
		String fileType = lastIndex > 0 ? filename.substring(lastIndex + 1) : "tmp";
		
		try {
			Path copy = Files.createTempFile(filenameWithoutType, fileType);
			try (InputStream in = url.openStream()) {
				Files.copy(in, copy, StandardCopyOption.REPLACE_EXISTING);
			}
			
			copy.toFile().deleteOnExit();
			return copy.toFile();
		} catch (Exception ex) {
			throw new IllegalStateException("Fail to extract " + filename, ex);
		}
	}
	
	public static boolean deleteDirectory(File path) {
		if (path == null || !path.exists()) {
			return false;
		}
		
		if (!path.isDirectory()) {
			return path.delete();
		}
		
		File[] files = path.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				deleteDirectory(file);
			} else {
				file.delete();
			}
		}
		return path.delete();
	}
	
	public static File saveResourceFile(String resource, String prefix, String suffix) {
	
		File file;
		try {
			file = File.createTempFile(prefix, suffix);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		file.deleteOnExit();

		try (OutputStream outStream = new BufferedOutputStream(new FileOutputStream(file))) {
			IOAndFileUtils.write(outStream, resource);
		} catch (FileNotFoundException ex) {
			throw new IllegalStateException(ex);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}

		return file;
	}
	
	public static String getFilenameWithoutExt(File file) {
		String outputFile;
		try {
			outputFile = file.getCanonicalPath();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		
		String csvFile = outputFile.substring(0, outputFile.lastIndexOf("."));
		return csvFile;
	}

	public static int getJavaFileCount(Path dir) {
		try (Stream<Path> paths =  Files.walk(dir)) {
			return (int) paths
					.parallel()
					.filter(p -> !p.toFile().isDirectory())
					.filter(p -> p.toFile().getName().endsWith(".java"))
					.count();
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	public static int getFileCountWithExt(Path dir, String... ext) {
		try (Stream<Path> paths =  Files.walk(dir)) {
			return (int) paths
					.parallel()
					.filter(p -> !p.toFile().isDirectory())
					.filter(p -> checkFileExt(p.toFile().getName(), ext))
					.count();
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	private static boolean checkFileExt(String filename, String... extVarargs) {
		for (String ext : extVarargs) {
			if (filename.endsWith("." + ext)) {
				return true;
			}
		}

		return false;
	}
}
