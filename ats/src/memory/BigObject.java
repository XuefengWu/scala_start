package memory;

public class BigObject {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int _1MB = 1024 * 1024;
		for(int i = 0; i < _1MB; i++){
			byte[] bytes = new byte[_1MB*16];
			System.out.println(i);
		}
	}

}
