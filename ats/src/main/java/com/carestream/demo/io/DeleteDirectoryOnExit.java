
package com.carestream.demo.io;

import java.io.File;
import java.io.IOException;

public class DeleteDirectoryOnExit {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        File dir = new File("D:/tmp/target", "IO276");
        dir.mkdirs();
        File file = new File(dir, "IO276.txt");
        file.createNewFile();
        // FileUtils.forceDeleteOnExit(dir);
        // Files (or directories) are deleted in the reverse order that they are registered.
        new File("D:/tmp/target/IO276/IO276.txt").deleteOnExit();
        new File("D:/tmp/target/IO276").deleteOnExit();
        System.out.println("D:/tmp/target/IO276 exist:" + new File("D:/tmp/target/IO276").exists());
    }

}
