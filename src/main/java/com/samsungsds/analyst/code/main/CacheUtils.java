package com.samsungsds.analyst.code.main;

import com.google.common.io.Files;
import com.samsungsds.analyst.code.util.FileLocker;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sonar.api.utils.ZipUtils;

import java.io.*;
import java.util.Objects;

public class CacheUtils {
    private static final Logger LOGGER = LogManager.getLogger(CacheUtils.class);

    public static File getUnzippedCacheDirectory(String instanceKey, File zipFile, String softwareName) {
        if (System.getProperty("noCache", "false").equalsIgnoreCase("true")) {
            File dir = Files.createTempDir();
            MeasuredResult.getInstance(instanceKey).addTempFileToBeDeleted(dir);

            try {
                unzipFileToDirectory(zipFile, dir);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }

            return dir;
        } else {
            File cacheDir = IOAndFileUtils.mkdirCacheDir();

            File dir = new File(cacheDir, softwareName + "-" + Version.CODE_ANALYST);

            if (dir.exists()) {
                LOGGER.info("Cached {} directory used.", dir.toString());

                return dir;
            }

            File tmp = new File(cacheDir, softwareName + "-" + Version.CODE_ANALYST + ".tmp");
            tmp.mkdir();
            try (FileLocker ignored = new FileLocker(tmp)) {
                // Double check
                if (tmp.list() == null) {
                    throw new IllegalStateException(tmp.getAbsolutePath() + " error");
                }
                if (Objects.requireNonNull(tmp.list()).length == 0) {
                    try {
                        LOGGER.info("Cached {} directory created.", tmp.toString());
                        unzipFileToDirectory(zipFile, tmp);
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                }

                boolean renamed = tmp.renameTo(dir);
                if (!renamed) {
                    throw new IllegalStateException("rename error : " + tmp + " -> " + dir);
                }
                LOGGER.info("Cached directory renamed : {} -> {}", tmp.toString(), dir.toString());
            }

            return dir;
        }
    }

    private static void unzipFileToDirectory(File zipFile, File where) throws IOException {
        if (zipFile.getName().toLowerCase().endsWith(".zip")) {
            ZipUtils.unzip(zipFile, where);
        } else if (zipFile.getName().toLowerCase().endsWith(".tar.gz")) {
            try (TarArchiveInputStream fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(zipFile)))) {
                saveArchiveFile(fin, where);
            }
        } else if (zipFile.getName().toLowerCase().endsWith(".tar.xz")) {
            try (TarArchiveInputStream fin = new TarArchiveInputStream(new XZCompressorInputStream(new FileInputStream(zipFile)))) {
                saveArchiveFile(fin, where);
            }
        } else {
            throw new IllegalStateException("zip file extension error : " + zipFile);
        }
    }

    private static void saveArchiveFile(TarArchiveInputStream fin, File dir) throws IOException {
        TarArchiveEntry entry;
        while ((entry = fin.getNextTarEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            File curfile = new File(dir, entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            IOUtils.copy(fin, new FileOutputStream(curfile));
        }
    }
}
