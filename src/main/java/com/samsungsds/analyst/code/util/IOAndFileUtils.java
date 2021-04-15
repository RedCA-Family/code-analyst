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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class IOAndFileUtils {
    private static final Logger LOGGER = LogManager.getLogger(IOAndFileUtils.class);

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
		output.write(str.getBytes(StandardCharsets.UTF_8));
	}

	public static long write(OutputStream output, String resourcePath) throws IOException {
		URL url = IOAndFileUtils.class.getResource(resourcePath);

		if (url == null) {
		    throw new IOException(resourcePath + " not found");
        }

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
	    // SonarQube 자체적으로 Cache가 되기 때문에 명시적으로 Code Analyst Cache는 적용하지 않음
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
		if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
		return path.delete();
	}

	public static File getSystemTempDir() {
	    return new File(System.getProperty("java.io.tmpdir"));
    }

    public static File getUserHomeDir() {
	    return new File(System.getProperty("user.home"));
    }

	public static File saveResourceFile(String resource, String prefix, String suffix) {
        File file;
        if (System.getProperty("noCache", "false").equalsIgnoreCase("true")) {
            LOGGER.info("No Cache for resource file");
            try {
                file = File.createTempFile(prefix, suffix);
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
            file.deleteOnExit();

            try (OutputStream outStream = new BufferedOutputStream(new FileOutputStream(file))) {
                IOAndFileUtils.write(outStream, resource);
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        } else {
            File cacheDir = mkdirCacheDir();

            file = getCachedResourceFile(cacheDir, resource);

            if (file.exists() && file.length() > 0) {
                LOGGER.info("Cached {} file used.", file.toString());
                return file;
            }

            try (FileLocker ignored = new FileLocker(file)) {
                // Double check
                if (file.exists() && file.length() > 0) {
                    LOGGER.info("Cached {} file used.", file.toString());
                    return file;
                }

                LOGGER.info("Cached {} file created.", file.toString());
                try (OutputStream outStream = new BufferedOutputStream(new FileOutputStream(file))) {
                    IOAndFileUtils.write(outStream, resource);
                } catch (IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }

        return file;
	}

    public static File mkdirCacheDir() {
        File codeAnalystCache = new File(getUserHomeDir(), ".code-analyst-cache");

        if (codeAnalystCache.exists()) {
            return codeAnalystCache;
        }

        try (FileLocker ignored = new FileLocker(codeAnalystCache)) {
            // Double check
            if (!codeAnalystCache.exists()) {
                codeAnalystCache.mkdir();

                File readme = new File(codeAnalystCache, "_DO_NOT_EDIT_FILES_JUST_DELETE_FILES.txt");
                try {
                    writeString(new FileOutputStream(readme), "Don't edit files. If you want to problem, the just delete files or delete this 'cache' directory.");
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }
        }

        return codeAnalystCache;
    }

    private static File getCachedResourceFile(File cacheDir, String resource) {
        String[] paths = resource.replace("\\", "/").split(Pattern.quote("/"));
        String fileName = paths[paths.length - 1];

	    return new File(cacheDir, fileName);
    }

    public static String getFilenameWithoutExt(File file) {
		String outputFile;
		try {
			outputFile = file.getCanonicalPath();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

        return outputFile.substring(0, outputFile.lastIndexOf("."));
	}

	public static String getFileExtension(File file) {
	    String filename = file.getAbsolutePath();

	    if (filename.lastIndexOf(".") > 0) {
	        return filename.substring(filename.lastIndexOf("."));
        }
	    return "";
    }

    public static int getFileCount(Path dir, String ext) {
        try (Stream<Path> paths =  Files.walk(dir)) {
            return (int) paths
                .parallel()
                .filter(p -> !p.toFile().isDirectory())
                .filter(p -> p.toFile().getName().endsWith(ext))
                .count();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

	public static int getJavaFileCount(Path dir) {
		return getFileCount(dir, ".java");
	}

	public static int getJSFileCount(Path dir) {
	    return getFileCount(dir, ".js");
    }

    public static int getCSharpFileCount(Path dir) {
        return getFileCount(dir, ".cs");
    }

    public static int getPythonFileCount(Path dir) {
        return getFileCount(dir, ".py");
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

	public static String getNormalizedPath(String path) {
		return path.replace("/", File.separator).replace("\\", File.separator);
	}

	public static String getPrefixRemovedPath(String path, String prefix) {

		path = IOAndFileUtils.getNormalizedPath(path);
		prefix = IOAndFileUtils.getNormalizedPath(prefix);

		if (!prefix.endsWith(File.separator)) {
			prefix += File.separator;
		}
		if (path.startsWith(prefix)) {
			path = path.substring(prefix.length());
		}

		return path;
	}

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignore) {
            // no operation
        }
    }
}
