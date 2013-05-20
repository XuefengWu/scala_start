package memory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * java.net.SocketException: Too many open files
 * 
 * @author orna
 *
 */
public class SocketBuffer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		final int _1MB = 1024 * 1024;
		for (int t = 8000; t < _1MB; t++) {
			final int port = t;
			new Thread() {
				public void run() {
					try {
						ServerSocket ss = new ServerSocket(port);
						
						while (true){
							Socket socket = ss.accept();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	 

	}

}
