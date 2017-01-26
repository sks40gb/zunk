package com.core.multithreading.concurrency;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunil
 */
public class BlockingQueueApp {

    BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

    public static void main(String[] args) {
        new BlockingQueueApp().main();
    }

    public void main() {
        final Processor processor = new Processor();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    processor.produce();
                } catch (InterruptedException ex) {
                    Logger.getLogger(BlockingQueueApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    processor.consume();
                } catch (InterruptedException ex) {
                    Logger.getLogger(BlockingQueueApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    processor.consume();
                } catch (InterruptedException ex) {
                    Logger.getLogger(BlockingQueueApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        executorService.shutdown();

    }

    class Processor {

        public void produce() throws InterruptedException {
            while (true) {
                Integer integer = new Random().nextInt(10);
                queue.put(integer);
                System.out.println("FILLING BY : " + Thread.currentThread().getName() + " : " + integer + " SIZE : " + queue.size());
                Thread.sleep(1000);
            }
        }

        public void consume() throws InterruptedException {
            while (true) {
                Integer integer = queue.take();
                System.out.println("TAKING BY : " + Thread.currentThread().getName() + " : " + integer + " SIZE : " + queue.size());
            }
        }
    }
}
