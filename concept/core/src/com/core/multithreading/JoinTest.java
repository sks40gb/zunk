package com.core.multithreading;

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
        firstCall();
        t.join();
        secondCall();
    }

    public static void firstCall(){
         try {
            Thread.sleep(100);
            System.out.println("====================================> Main : first call");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void secondCall() {
        try {
            Thread.sleep(100);
            System.out.println("====================================> Main : second call");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
