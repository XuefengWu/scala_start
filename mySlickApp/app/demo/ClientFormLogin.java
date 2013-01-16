package demo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


/**
 * A example that demonstrates how HttpClient APIs can be used to perform
 * form-based logon.
 */
public class ClientFormLogin {

    public static void main(String[] args) throws Exception {

        final DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            final HttpGet httpget = new HttpGet("http://www.cnhd.com/bonuscard.php");

            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            System.out.println("Login form get: " + response.getStatusLine());
            //EntityUtils.consume(entity);
            System.out.println("result:");
            System.out.println(inputStream2String(entity.getContent()));

            System.out.println("Initial set of cookies:");
            List<Cookie> cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }

            HttpPost httpost = new HttpPost("http://www.cnhd.com/takelogin.php");

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("password", "wubiancnhd"));
            nvps.add(new BasicNameValuePair("username", "benewu"));
            nvps.add(new BasicNameValuePair("returnto", "bonuscard.php"));

            httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            response = httpclient.execute(httpost);
            entity = response.getEntity();

            System.out.println("Login form get: " + response.getStatusLine());
            //EntityUtils.consume(entity);

            System.out.println("bonuscard:");
            System.out.println(inputStream2String(entity.getContent()));

            System.out.println("Post logon cookies:");
            cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }


            InputStreamReader istream = new InputStreamReader(System.in);
            BufferedReader bufRead = new BufferedReader(istream);

            System.out.println("Please Enter In bonus hash: ");
            final String hash = bufRead.readLine();
            System.out.println("Image bonus hash is: " + hash);

            final List<String> imgHashs = new ArrayList<String>();
            final List<String> captchas = new ArrayList<String>();

            for (int i = 0; i < 10; i++) {
                response = httpclient.execute(httpget);
                entity = response.getEntity();

                String cont = inputStream2String(entity.getContent());
                String imgHash = extractImageHash(cont);
                System.out.println("Image hash is: " + imgHash);
                if (imgHash == null || imgHash.length() < 1) {
                    System.out.println("Please Enter In image hash: ");
                    imgHash = bufRead.readLine();
                }
                imgHashs.add(imgHash);
                System.out.println("http://www.cnhd.com/image.php?action=regimage&imagehash=" + imgHash);

                System.out.println("Please Enter In image captcha: ");
                final String captcha = bufRead.readLine();
                System.out.println("Image captcha is: " + captcha);
                captchas.add(captcha);
            }


            final CountDownLatch startLatch = new CountDownLatch(10);

            List<Thread> threads = new ArrayList<Thread>();
            for (int i = 0; i < 10; i++) {
                final int finalI = i;
                final Thread thread = new Thread() {
                    public void run() {
                        try {
                            DefaultHttpClient httpclient2 = new DefaultHttpClient();
                            httpclient2.setCookieStore(httpclient.getCookieStore());

                            HttpPost httpost2 = new HttpPost("http://www.cnhd.com/bonuscard.php");

                            List<NameValuePair> nvps2 = new ArrayList<NameValuePair>();
                            nvps2.add(new BasicNameValuePair("hash", hash));
                            nvps2.add(new BasicNameValuePair("captcha", captchas.get(finalI)));
                            nvps2.add(new BasicNameValuePair("imagehash", imgHashs.get(finalI)));

                            httpost2.setEntity(new UrlEncodedFormEntity(nvps2, "UTF-8"));

                            startLatch.countDown();
                            startLatch.await();
                            System.out.println(Thread.currentThread().getName() + "\t"+ System.currentTimeMillis());
                            HttpResponse response2 = httpclient2.execute(httpost2);
                            HttpEntity entity2 = response2.getEntity();

                            System.out.println("result:");
                            System.out.println(inputStream2String(entity2.getContent()));
                            httpclient2.getConnectionManager().shutdown();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                };
                threads.add(thread);

            }

            for (Thread thread : threads) {
                thread.start();
            }


        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

    private static String inputStream2String(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    private static String extractImageHash(String text) {
        Pattern pattern = Pattern.compile("action=regimage&amp;imagehash=(.*)\" border=");
        Matcher matcher = pattern.matcher(text);
        String res = "";
        if (matcher.find()) {
            res = matcher.group(1);
        }

        return res;
    }
}