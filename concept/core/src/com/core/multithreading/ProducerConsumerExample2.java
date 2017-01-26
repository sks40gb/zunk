package com.core.multithreading;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sunil
 */
public class ProducerConsumerExample2 {

    private List<Integer> jobList;
    private int count;

    public ProducerConsumerExample2() {
        jobList = new ArrayList<Integer>();
    }

    public static void main(String[] args) {
        new ProducerConsumerExample2().start();
    }

    void start() {
        Thread producer = new Thread(new Producer());
        producer.start();
        new Consumer("A").start();
        new Consumer("B").start();
        new Consumer("C").start();
    }

    class Producer implements Runnable {

        public void run() {
            while (true) {
                synchronized (jobList) {
                    System.out.println("PRODUCER : A : +" + count);
                    jobList.add(count++);
                    jobList.notifyAll();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Consumer extends Thread {

        public Consumer(String name) {
            setName(name);
        }

        public void run() {
            while (true) {
                try {
                    synchronized (jobList) {
                        if (jobList.isEmpty()) {
                            jobList.wait();
                        } else {
                            System.out.println("CONSUMER : " + getName() + " : -" + jobList.remove(0));
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
