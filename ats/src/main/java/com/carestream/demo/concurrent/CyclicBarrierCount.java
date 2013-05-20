
package com.carestream.demo.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierCount {

    /**
     * @param args
     */
    public static void main(String[] args) {

        Runnable merger = new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName() + " merge");
            }
        };

        final CyclicBarrier barrier = new CyclicBarrier(10, merger);

        for (int i = 0; i < 10; i++) {
            Thread counter = new Thread() {
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        System.out.println(Thread.currentThread().getName() + " count:" + i);
                        try {
                            barrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            counter.start();
        }
        System.out.println("Waiting...");
    }

}
