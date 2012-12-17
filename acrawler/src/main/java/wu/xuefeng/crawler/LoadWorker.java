
package wu.xuefeng.crawler;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

public class LoadWorker extends Thread {

    HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());

    public void run() {

        while (true) {
            load();
        }
    }

    private void load() {

        InputStream is = null;
        FileOutputStream os = null;
        try {
            String url = Crawler.unloadedLinks.poll();
            if (url == null || url.length() < 1) {
                Thread.sleep(100);
                return;
            }
            System.out.println(Thread.currentThread().getName() + "-load: " + url);
            Crawler.loadedLinks.add(url.hashCode());
            GetMethod get = new GetMethod(url);

            int iGetResultCode = client.executeMethod(get);

            if (iGetResultCode == HttpStatus.SC_OK) {
                get.setFollowRedirects(true);
                is = get.getResponseBodyAsStream();
                String path = util.UrlFile.buildFilePath(url);
                os = new FileOutputStream(path);
                IOUtils.copy(is, os);
                Crawler.loadedFiles.add(path);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
