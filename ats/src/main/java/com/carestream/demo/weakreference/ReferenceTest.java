
package com.carestream.demo.weakreference;

import java.lang.ref.WeakReference;

public class ReferenceTest {

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        WeakReference<String> r = new WeakReference<String>(new String("I'm here 1"));
        WeakReference<String> sr = new WeakReference<String>("I'm here 2");
        WeakReference<String> ssr = new WeakReference<String>(new String("I'm here 3"));
        System.out.println("before gc: r=" + r.get() + ", static=" + sr.get() + ", static=" + ssr.get());
        System.gc();
        Thread.sleep(100);

        // only r.get() becomes null
        System.out.println("before gc: r=" + r.get() + ", static=" + sr.get() + ", static=" + ssr.get());
    }

}
