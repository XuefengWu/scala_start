package memory;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * -XX:PermSize=<value> -XX:MaxPermSize=<value> Getting Information on the
 * Permanent Generation
 * http://www.oracle.com/technetwork/java/javase/tooldescr-136044.html#gblmm
 * 
 * @author orna
 * 
 */
public class PermanentSpace {

	private static byte[] btyes = new byte[1024 * 1024];

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int _1MB = 1024 * 1024;
		for (int t = 0; t < _1MB; t++) {
			new Thread() {
				public void run() {
					URL[] url = new URL[] {};
					ClassLoader cl = new URLClassLoader(url);
					System.out.println(cl);
					try {
						Class clz = cl.loadClass("memory.PermanentSpace");
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					try {
						sleep(100000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
}
