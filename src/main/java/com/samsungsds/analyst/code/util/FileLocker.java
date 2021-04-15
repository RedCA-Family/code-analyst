package com.samsungsds.analyst.code.util;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileLocker implements AutoCloseable {
    private final String lockFilePath;

    private FileChannel channel = null;
    private FileLock lock = null;

    public FileLocker(File file) {
        this.lockFilePath = file.getAbsoluteFile() + ".lock";

        lock();
    }

    @Override
    public void close() {
        unlock();
    }

    protected void lock() {
        try {
            channel = new FileOutputStream(lockFilePath,true).getChannel();

            lock = channel.lock();  // blocking
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    protected void unlock() {
        if (lock != null) {
            try {
                lock.release();
                lock = null;
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        if (channel != null) {
            try {
                channel.close();
                channel = null;
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        File lockFile = new File(lockFilePath);
        lockFile.delete();
    }
}
