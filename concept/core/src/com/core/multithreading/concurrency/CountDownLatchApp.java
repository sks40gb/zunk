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
        System.out.println("Main thread starts.");
        for (int i = 0; i < 4; i++) {
            executor.execute(new Player("Player "+i));
            Thread.sleep(1000);
            countDownLatch.countDown();
        }
        executor.shutdown();
        executor.awaitTermination(400, TimeUnit.DAYS);
        System.out.println("Main thread ends");
    }

    class Player implements Runnable {
        private String name;

        public Player(String name) {
            this.name = name;
        }

        public void run() {
            System.out.println(name  +" is waiting");
            try {
                countDownLatch.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(CountDownLatchApp.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(name + " is done.");
        }
    }
}
