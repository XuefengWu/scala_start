package wu.xuefeng.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

public class LoadWorker extends Thread {

	DB db = null;
	DBCollection loadedLinksColl = null;

	public void run() {

		try {
			db = new MongoClient("localhost", new MongoClientOptions.Builder()
					.cursorFinalizerEnabled(false).build()).getDB(Crawler.DB_NAME);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		loadedLinksColl = db.getCollection("loadedLinksColl");

		System.out
				.println(Thread.currentThread().getName() + " is ready to go");
		while (true) {
			load();
		}
	}

	private boolean isHtml(String url) {
		return !(url.endsWith("gif") || url.endsWith("png") || url
				.endsWith("jpg"));
	}

	private void load() {
		HttpClientParams parm = new HttpClientParams();
		HttpClient client = new HttpClient();
		InputStream is = null;
		FileOutputStream os = null;
		GetMethod get = null;
		String url = null;
		try {
			url = Crawler.unloadedLinks.poll();
			if (url == null || url.length() < 1) {
				Thread.sleep(1000);
				System.out.println("Crawler.unloadedLinks: "+Crawler.unloadedLinks.size());
				return;
			}
			url = url.replace(" ", "%20");
			Crawler.loadedLinks.add(url);
			loadedLinksColl.insert(new BasicDBObject("url", url));

			if (Crawler.isInvalidate(url)) {
				System.out.println("Error:"+url);
				return;
			}

			System.out.println("load: " + url);
			get = new GetMethod(url);

			int iGetResultCode = client.executeMethod(get);

			if (iGetResultCode == HttpStatus.SC_OK) {
				is = get.getResponseBodyAsStream();
				String path = util.UrlFile.buildFilePath(url);

				if (isHtml(url)) {
					StringBuffer sb = new StringBuffer();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is));
					String line = reader.readLine();
					while (line != null) {
						sb.append(line);
						line = reader.readLine();
					}
					Crawler.loadedFiles.add(sb.toString());
					FileUtils.writeStringToFile(new File(path), sb.toString());
				} else {
					os = new FileOutputStream(path);
					IOUtils.copy(is, os);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
				if (get != null) {
					get.releaseConnection();
				}

			} catch (Exception e) {
				System.out.println("ERROR load: " + url);
				e.printStackTrace();
				System.out.println("ERROR load: " + url);
			}
		}
	}

}
