package com.carestream.demo.weakreference;

public class ExistJvm {

    /**
     * @param args
     */
    public static void main(String[] args) {
	System.out.println("hello");
	System.out.println(1000 * 60 * 60 * 24 * 7);
	System.out.println(1000l * 60 * 60 * 24 * 30);
	System.out.println(System.currentTimeMillis());
	System.exit(-1);
	System.out.println("world");
    }

}
