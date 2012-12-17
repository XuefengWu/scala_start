
package wu.xuefeng.crawler;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class ParserWorker extends Thread {

    public void run() {
        while (true) {
            parse();
        }
    }

    private void parse() {
        try {
            String path = Crawler.loadedFiles.poll();
            if (path == null || path.length() < 1) {
                Thread.sleep(1000);
                return;
            }

            System.out.println(Thread.currentThread().getName() + "-parse: " + path);
            List<String> links = util.PageParse.getLinksForJava(Crawler.domain,
                    FileUtils.readFileToString(new File(path)));
            for (String lnk : links) {
                if (!Crawler.loadedLinks.contains(lnk.hashCode())) {
                    Crawler.unloadedLinks.add(lnk);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
