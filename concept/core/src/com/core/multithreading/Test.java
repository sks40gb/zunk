package com.core.multithreading;

/**
 *
 * @author Sunil
 */
public class Test {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Started");
        long currentTime = System.currentTimeMillis();
        Thread currentThread = Thread.currentThread();
        Thread t = new Thread(new Job("First"));
        t.start();
        while (t.isAlive()) {
            Thread.sleep(1000);
            t.join(1000);
            if ((System.currentTimeMillis() - currentTime > 3000) && t.isAlive()) {
                System.out.println("Tired of waiting..");
                t.interrupt();
                t.join();
            }
        }
        System.out.println("completed");

    }
}

class Job implements Runnable {

    Thread waitFor;
    String name;

    public Job(String name) {
        this.name = name;
    }

    public void run() {

        try {
            for (int i = 0; i < 10; i++) {

                Thread.sleep(1000);

                System.out.println("Name : " + name + " count : " + i);
            }
        } catch (InterruptedException e) {
            System.out.println("I wasn't completed");
        }


    }
}
