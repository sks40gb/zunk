package com.core.multithreading.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunil
 */
public class CountDownLatchApp {

    CountDownLatch countDownLatch = new CountDownLatch(4);
    private int count;

    public static void main(String[] args) throws InterruptedException {
        new CountDownLatchApp().execute();
    }

    public void execute() throws InterruptedException{
        ExecutorService executor = Executors.newFixedThreadPool(4);
        System.out.println("MAIN THREAD STARTS");
        for (int i = 0; i < 4; i++) {
            executor.execute(new Player());
            Thread.sleep(1000);
            countDownLatch.countDown();
        }
        executor.shutdown();
        executor.awaitTermination(400, TimeUnit.DAYS);
        System.out.println("MAIN THREAD ENDS");
    }

    class Player implements Runnable {

        public void run() {
            System.out.println("Waiting Player " + ++count);
            try {
                countDownLatch.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(CountDownLatchApp.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Ready Player  " + count);
        }
    }
}
