package com.core.multithreading.concurrency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Random;

/**
 *
 * @author Sunil
 */
public class HandleThreadExceptionApp {

    public static void main(String[] args) throws IOException {
        new HandleThreadExceptionApp().execute();
    }

    public void execute() throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(new HandleUncaughtException());
        Thread t = new Thread(new PrintMedia());
        t.start();
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        System.out.println("Press any key to enterupt ..");
        String name = reader.readLine();
        System.out.println("Hello " + name);
        t.interrupt();
        System.out.println("Game over !!");

    }
    //Define class

    class PrintMedia implements Runnable {

        public void run() {
            while (true) {
                System.out.println(new Random().nextInt(200));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    class HandleUncaughtException implements UncaughtExceptionHandler {

        public void uncaughtException(Thread t, Throwable e) {
            System.out.println("Exception is handled here : " + t.getName() + " : Message : " + e.getMessage());
        }

    }
}
