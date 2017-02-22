package com.core.multithreading.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunil
 */
public class ReentranctLockApp {

    public static void main(String[] args) throws InterruptedException {
        final Processor_ processor = new Processor_();
        System.out.println("Main is starting.");
        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    processor.produce();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ReentranctLockApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    processor.consume();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ReentranctLockApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Thread t3 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    processor.consume();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ReentranctLockApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        Thread t4 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    processor.consume();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ReentranctLockApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();
//        t4.start();


        t1.join();
        t2.join();
        t3.join();
//        t4.join();
        System.out.println("Main is existing.");
    }

}


class Processor_ {

    private final static int MAX_SIZE = 10;
    private volatile List<Integer> list = new ArrayList(MAX_SIZE);
    private int count;
    private Random random = new Random();
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    public void produce() throws InterruptedException {
        while (true) {
            try {
                lock.lock();
                if (list.size() == MAX_SIZE) {
                    condition.await();
                }
                list.add(count++);
                System.out.println("ADDING : " + count + " TOTAL ZISE : " + list.size());
            } finally {
                condition.signalAll();
                lock.unlock();
            }
            Thread.sleep(random.nextInt(1000));
        }

    }

    public void consume() throws InterruptedException {
        while (true) {
            lock.lock();
            System.out.println("LIST SIZE >>>>>>>>>>>>>  : " + list.size());
            if (list.isEmpty()) {
                condition.await();
            } else {
                System.out.println("REMOVING BY " + Thread.currentThread().getName() + " --- " + +list.get(0));
                list.remove(0);
                lock.unlock();
                condition.signalAll();
            }
        }
    }
}
