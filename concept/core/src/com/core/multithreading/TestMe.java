package com.core.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sunsingh
 */
public class TestMe {

    public static void main(String[] args) {
        Counter counter = new Counter();
        Thread producer = new Thread(new CounterProducer(counter));
        Thread consumer1 = new Thread(new CounterConsumer(counter));
        Thread consumer2 = new Thread(new CounterConsumer(counter));

        producer.start();
        consumer1.start();
        consumer2.start();
    }

}

class Counter {

    private int counter;
    private static int LIMIT = 10;
    private List<Integer> list = new ArrayList();

    public void fill() {
        if (isFull()) {
            throw new RuntimeException("List is full");
        }
        list.add(counter++);
        System.out.println("PRODUCING ... " + Thread.currentThread().getName() + " COUNTER : " + counter);
    }

    public Integer consume() {
        if (!isEmpty()) {
            System.out.println("CONSUMING ... " + Thread.currentThread().getName() + " COUNTER : " + this.list.get(0));
            return this.list.remove(0);
        } else {
            throw new RuntimeException("List is empty");
        }
    }

    public boolean isFull() {
        return list.size() > LIMIT;
    }

    public boolean isEmpty() {
        return list.size() == 0;
    }
}

class CounterProducer implements Runnable {

    private Counter counter;

    public CounterProducer(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (counter) {
                try {
                    if (this.counter.isFull()) {
                        this.counter.wait();
                    } else {
                        this.counter.fill();
                        this.counter.notifyAll();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(CounterProducer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CounterProducer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}

class CounterConsumer implements Runnable {

    private Counter counter;

    public CounterConsumer(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (counter) {
                try {
                    if (this.counter.isEmpty()) {
                        this.counter.wait();
                    } else {
                        this.counter.consume();
                        this.counter.notifyAll();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(CounterProducer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CounterConsumer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
