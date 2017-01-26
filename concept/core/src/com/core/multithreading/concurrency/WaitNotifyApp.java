package com.core.multithreading.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunil
 */
public class WaitNotifyApp {

    public static void main(String[] args) throws InterruptedException {
        final Processor processor = new Processor();
        System.out.println("Main is starting.");
        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    processor.produce();
                } catch (InterruptedException ex) {
                    Logger.getLogger(WaitNotifyApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //Inner thread class
        class Consumer implements Runnable{
              @Override
            public void run() {
                try {
                    processor.consume();
                } catch (InterruptedException ex) {
                    Logger.getLogger(WaitNotifyApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        Thread t2 = new Thread(new Consumer());
        Thread t3 = new Thread(new Consumer());
        Thread t4 = new Thread(new Consumer());
        
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();
        System.out.println("Main is existing.");
    }
}

class Processor {

    private static final int MAX_SIZE = 10;
    private volatile List<Integer> list = new ArrayList<>(MAX_SIZE);
    private int count;
    private final Random random = new Random();

    public void produce() throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (list.size() == MAX_SIZE) {
                    wait();
                }
                list.add(count++);
                notifyAll();
                System.out.println("ADDING : " + count + " TOTAL ZISE : " + list.size());
                Thread.sleep(random.nextInt(1000));
            }
        }

    }

    public void consume() throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (list.isEmpty()) {
                    wait();
                }  
                //check again if Thread gets resumed and meanwhile list is empty by other consumer thread.
                if (!list.isEmpty()) {
                    System.out.println("REMOVING BY " + Thread.currentThread().getName() + " --- " + +list.get(0));
                    list.remove(0);
                    notifyAll();
                }
            }
        }
    }
}
