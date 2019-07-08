package com.core.multithreading;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JoinTest {

    public static void main(String[] args) throws InterruptedException {

        Thread number = new Thread(() -> {
            int[] numbers = {1, 2, 3, 4};
            Arrays.stream(numbers).forEach(item -> {
                System.out.println(Thread.currentThread().getName() + " : " + item);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JoinTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });

        Thread letter = new Thread(() -> {
            String[] letters = {"A", "B", "C", "D"};
            Arrays.stream(letters).forEach(item -> {
                System.out.println(Thread.currentThread().getName() + " : " + item);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JoinTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
        System.out.println("Main thread started");

        number.start();
        letter.start();

        number.join();
        letter.join();

        System.out.println("Main thread ended");

    }
}
