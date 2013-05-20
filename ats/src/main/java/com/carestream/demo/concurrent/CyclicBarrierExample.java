
package com.carestream.demo.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierExample {
    private static int matrix[][] = {
            {
                1
            }, {
                    2, 2
            }, {
                    3, 3, 3
            }, {
                    4, 4, 4, 4
            }, {
                    5, 5, 5, 5, 5
            }
    };

    private static int results[];

    private static class Summer extends Thread {
        int row;

        CyclicBarrier barrier;

        Summer(CyclicBarrier barrier, int row) {
            this.barrier = barrier;
            this.row = row;
        }

        public void run() {
            int columns = matrix[row].length;
            int sum = 0;
            for (int i = 0; i < columns; i++) {
                sum += matrix[row][i];
            }
            results[row] = sum;
            System.out.println("Results for row " + row + " are : " + sum);
            // wait for others
            try {
                barrier.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (BrokenBarrierException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        final int rows = matrix.length;
        results = new int[rows];
        Runnable merger = new Runnable() {
            public void run() {
                int sum = 0;
                for (int i = 0; i < rows; i++) {
                    sum += results[i];
                }
                System.out.println("Results are: " + sum);
            }
        };
        /*
         * public CyclicBarrier(int parties,Runnable barrierAction) Creates a new CyclicBarrier that will trip when the given number of
         * parties (threads) are waiting upon it, and which will execute the merger task when the barrier is tripped, performed by the last
         * thread entering the barrier.
         */
        CyclicBarrier barrier = new CyclicBarrier(rows, merger);
        for (int i = 0; i < rows; i++) {
            new Summer(barrier, i).start();
        }
        System.out.println("Waiting...");
    }
}
