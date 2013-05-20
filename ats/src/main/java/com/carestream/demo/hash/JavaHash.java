package com.carestream.demo.hash;

import java.io.File;

public class JavaHash {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("String:");
		System.out.println("// @p// @p// @p// @p// @p// @p".hashCode());
		System.out.println("// TODO Auto-generated method stub".hashCode());
		System.out.println("// @param argsb".hashCode());
		System.out.println("// @param args".hashCode());
		System.out.println("// if ((result.getImgList() != null) && (!result.getImgList().isEmpty())) {".hashCode());

		System.out.println("Double:");
		
		System.out.println(Double.doubleToLongBits(1));
		System.out.println(Double.doubleToRawLongBits(1));
		System.out.println(new Double(1).hashCode());
		System.out.println(new Double(2).hashCode());
		System.out.println(new Double(1000).hashCode());
		System.out.println(new Double(1000000).hashCode());
		System.out.println(new Double(1000000000).hashCode());
		System.out.println(new Double("1000000000000").hashCode());
		
		System.out.println("Integer:");
		System.out.println(new Integer(1000).hashCode());
		System.out.println(new Integer(1000000).hashCode());
		System.out.println(new Integer(1000000000).hashCode());
		System.out.println(new Integer("65000").hashCode());
		
		System.out.println("File:");
		System.out.println(new File("").hashCode());
		System.out.println(new File("/").hashCode());
		System.out.println(new File("/ss").hashCode());
		System.out.println(new File("/ppp").hashCode());
		
	}

}
