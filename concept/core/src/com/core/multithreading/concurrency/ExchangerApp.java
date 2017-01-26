package com.core.multithreading.concurrency;

/**
 *
 * @author Sunil
 */
import java.util.concurrent.Exchanger;

public class ExchangerApp {

    Exchanger<String> exchanger = new Exchanger();

    private class Producer implements Runnable {

        @Override
        public void run() {
            try {
                //create tasks & fill the queue
                //exchange the full queue for a empty queue with Consumer
                String queue = exchanger.exchange("Ready Queue");
                System.out.println(Thread.currentThread().getName() + " now has " + queue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Consumer implements Runnable {

        @Override
        public void run() {
            try {
                //do procesing & empty the queue
                //exchange the empty queue for a full queue with Producer
                String queue = exchanger.exchange("Empty Queue");
                System.out.println(Thread.currentThread().getName() + " now has " + queue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void start() {
        new Thread(new Producer(), "Producer").start();
        new Thread(new Consumer(), "Consumer").start();
    }

    public static void main(String[] args) {
        new ExchangerApp().start();
    }

}
