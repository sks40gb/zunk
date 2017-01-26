package com.core.multithreading;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JoinTest implements Runnable {

    static volatile int x;

    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(200);
                System.out.println("----------------" + this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Thread t = new Thread(new JoinTest());
        t.start();
        t.join();
        callMe();
    }

    public static void callMe() {
        try {
            Thread.sleep(100);
            System.out.println("====================================> THIS IS FROM MAIN");
        } catch (InterruptedException ex) {
            Logger.getLogger(JoinTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
