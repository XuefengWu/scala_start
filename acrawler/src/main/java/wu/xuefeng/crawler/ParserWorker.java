
package wu.xuefeng.crawler;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

public class ParserWorker extends Thread {

	DB db = null;
	DBCollection linksColl = null;
    public void run() {
		try {
			db = new MongoClient("localhost", new MongoClientOptions.Builder()
					.cursorFinalizerEnabled(false).build()).getDB(Crawler.DB_NAME);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		linksColl = db.getCollection("linksCollection");
    	System.out.println(Thread.currentThread().getName() + " is ready to go");
        while (true) {
            parse();
        }
    }

    private void parse() {
        try {
            String cxt = Crawler.loadedFiles.poll();
            if (cxt == null || cxt.length() < 1) {
                Thread.sleep(3000);
                System.out.println("Crawler.loadedFiles: "+Crawler.loadedFiles.size());
                return;
            }

            System.out.println(Thread.currentThread().getName() + "-parse: " + Crawler.loadedFiles.size());
            List<String> links = util.PageParse.getLinksForJava(Crawler.domain,cxt);
            for (String lnk : links) {
                if (!Crawler.loadedLinks.contains(lnk)) {
                    Crawler.unloadedLinks.add(lnk);
                    linksColl.insert(new BasicDBObject("url", lnk));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
