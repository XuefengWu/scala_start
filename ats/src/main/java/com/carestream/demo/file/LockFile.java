
package com.carestream.demo.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class LockFile {

    private FileLock lock(RandomAccessFile file) throws IOException {
        FileLock fileLock = file.getChannel().tryLock();
        // if the JVM is not able to acquire a lock then a null
        // is returned, it could be because the lock is already acquired
        // by another thread or process.
        return fileLock;
    }

    public static void main(String args[]) throws IOException, InterruptedException {

        System.out.println(System.getProperty("user.dir"));

        RandomAccessFile file = new RandomAccessFile(new File(
                "C:/Users/Public/Documents/Carestream/CS Data Manager Images/database/pas/tmp/1.txt"), "rw");
        LockFile fthis = new LockFile();
        FileLock lock = fthis.lock(file);
        System.out.println("Got the lock? " + (null != lock));
        if (null == lock) {
            return;
        }
        while (true) {
            if (null != lock) {
                System.out.println("Is a valid lock? " + lock.isValid());
            }
            Thread.sleep(3000);
        }
    }

}
