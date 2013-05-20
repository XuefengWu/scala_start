
package com.carestream.demo.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class DeleteDirectory {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        File dir = new File("D:/tmp/target", "IO276");
        dir.mkdirs();
        File file = new File(dir, "IO276.txt");

        file.createNewFile();
        writeContent(file, "test1");
        BufferedReader in = new BufferedReader(new FileReader(file));
        in.readLine();
        in.close();

        writeContent(file, "test2");

        in = new BufferedReader(new FileReader(file));
        in.readLine();
        in.close();

        FileUtils.deleteDirectory(new File("D:/tmp/target/IO276"));
        System.out.println("D:/tmp/target/IO276 exist:" + new File("D:/tmp/target/IO276").exists());
    }

    private static void writeContent(File file, String fileContent) {
        FileOutputStream to;
        try {
            to = new FileOutputStream(file);
            to.write(fileContent.getBytes());
            to.flush();
            to.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
