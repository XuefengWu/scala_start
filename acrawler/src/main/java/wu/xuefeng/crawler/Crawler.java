
package wu.xuefeng.crawler;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

public class Crawler {

    public static final ConcurrentLinkedQueue<String> unloadedLinks = new ConcurrentLinkedQueue<String>();

    public static final ConcurrentLinkedQueue<String> loadedFiles = new ConcurrentLinkedQueue<String>();

    public static final CopyOnWriteArraySet<Integer> loadedLinks = new CopyOnWriteArraySet<Integer>();

    public static final String domain = "http://tutorialspoint.com/";

    /**
     * @param args
     * @throws UnknownHostException
     */
    public static void main(String[] args) throws UnknownHostException {
        int loadNumber = 10;
        int parserNumber = 3;

        Executor loadExecutor = Executors.newFixedThreadPool(loadNumber);
        Executor parseExecutor = Executors.newFixedThreadPool(parserNumber);

        for (int i = 0; i < loadNumber; i++) {
            loadExecutor.execute(new LoadWorker());
        }

        for (int i = 0; i < parserNumber; i++) {
            parseExecutor.execute(new ParserWorker());
        }

        DB db = new MongoClient("localhost", new MongoClientOptions.Builder().cursorFinalizerEnabled(false).build())
                .getDB("crawler");
        DBCollection linksColl = db.getCollection("linksCollection");
        DBCollection loadedLinksColl = db.getCollection("loadedLinksColl");

        DBCursor cursor = loadedLinksColl.find();
        try {
            while (cursor.hasNext()) {
                String url = cursor.next().get("url").toString();
                loadedLinks.add(url.hashCode());
            }
        } finally {
            cursor.close();
        }
        System.out.println("loadedLinks:" + loadedLinks.size());

        List<String> list = new ArrayList<String>();
        cursor = linksColl.find();
        try {
            while (cursor.hasNext()) {
                String url = cursor.next().get("url").toString();
                list.add(url);
            }
        } finally {
            cursor.close();
        }

        System.out.println("all links:" + list.size());

        for (String lnk : list) {
            if (!loadedLinks.contains(lnk.hashCode())) {
                unloadedLinks.add(lnk);
            }
        }
    }
}
