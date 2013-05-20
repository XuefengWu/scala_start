package memory;

/**
 * -XX:+UseTLAB -XX:TLABSize=<size in kb> -XX:+PrintTLAB
 * heap and perm size do not increase, but jvm process memory increased
 * 
 * -Xss Tread Stack
 * @author orna
 * 
 */
public class ThreadLocalAllocationBuffer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int _1MB = 1024 * 1024;
		for (int i = 0; i < _1MB; i++) {
			new Thread() {
				public void run() {
					try {
						sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

}
