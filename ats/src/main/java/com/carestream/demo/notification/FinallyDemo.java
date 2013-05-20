package com.carestream.demo.notification;

public class FinallyDemo {

	public static void main(String[] args) {
		try{
			Object o = null;
			o.hashCode();
		} catch (NullPointerException np){
			System.out.println("NullPointException");
		} finally {
			System.out.println("finally");
		}
	}
	
}
