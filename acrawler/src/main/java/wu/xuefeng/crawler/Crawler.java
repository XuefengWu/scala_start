
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

    public static final CopyOnWriteArraySet<String> loadedLinks = new CopyOnWriteArraySet<String>();

    public static final String domain = "http://www.java2s.com";

    public static final String DB_NAME = "java2s";
    /**
     * @param args
     * @throws UnknownHostException
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        int loadNumber = 100;
        int parserNumber = 5;
        
        unloadedLinks.add(domain);
        new LoadWorker().start();

        DB db = new MongoClient("localhost", new MongoClientOptions.Builder().cursorFinalizerEnabled(false).build())
                .getDB(DB_NAME);
        DBCollection linksColl = db.getCollection("linksCollection");
        DBCollection loadedLinksColl = db.getCollection("loadedLinksColl");

        DBCursor cursor = loadedLinksColl.find();
        try {
            while (cursor.hasNext()) {
                String url = cursor.next().get("url").toString();
                loadedLinks.add(url);
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


        for (int i = 0; i < loadNumber; i++) {
        	new LoadWorker().start();
        }

        for (int i = 0; i < parserNumber; i++) {
        	new ParserWorker().start();
        }

        
        for (String lnk : list) {
            if (!loadedLinks.contains(lnk)) {
                unloadedLinks.add(lnk);
            }
        }
        
        System.out.println("all started");
    }
    
    
	public static boolean isInvalidate(String url) {
		return !url.startsWith(Crawler.domain)
				|| url.contains("/listtutorials/")
				|| url.contains("/listtutorial/") || url.contains("/rate/")
				|| url.contains("/tag/") || url.contains("/cgi-bin/") 
				|| url.contains("/shorttutorials/") || url.contains("mailto:") 
				|| url.contains("#ts-fab-")|| url.contains("/viewtutorial/")
				|| url.contains("/favorite/")|| url.contains("/wp-content/")
				|| url.contains("/newtutorials/")|| url.contains("/wp-content/")
				|| url.contains("/ftp:/")|| url.contains("/go/")
				|| url.contains("/viewtopic.php?")|| url.contains("/profile.php?")
				|| url.contains("/viewtopic.php?")|| url.contains("/profile.php?")
				|| url.contains("/login.php?")|| url.contains("/groupcp.php?")
				|| url.contains("/memberlist.php?")|| url.contains("/../");
	}
}
