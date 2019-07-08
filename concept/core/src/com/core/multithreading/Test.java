package com.core.multithreading;

/**
 *
 * @author Sunil
 */
public class Test {

    private static ThreadLocal<Integer> localNumber = new ThreadLocal<Integer>() {
        @Override
        public Integer initialValue() {
            return (int) (Math.random() * 10);
        }
    };

    public static void main(String[] args) throws InterruptedException {
        Printer printer = Test::printNumber;
        new Thread(() -> printer.print()).start();
        new Thread(() -> printer.print()).start();
    }

    public static void printNumber() {
        for (int i = 0; i < 4; i++) {
            System.out.println(Thread.currentThread().getName() + " Number : " + localNumber.get());
        }
    }

}

interface Printer {

    public void print();
}
