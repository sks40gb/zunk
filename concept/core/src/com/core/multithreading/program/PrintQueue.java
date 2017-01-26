/*
 * The following class is a model of a common concurrent utility: a work queue.
 * It has one method to enqueue tasks and another method to work them out. Before
 * removing a task from the queue, the work() method checks to see if it is empty
 * and if so, waits. The enqueue() method notifies all waiting threads (if any). 
 * To make this example simple, the tasks are just strings and the work is to 
 * print them. Again, main() serves as a unit test. By the way, this class has 
 * a bug. 
 */
package com.core.multithreading.program;

import java.util.LinkedList;

/**
 *
 * @author sunil
 */
public class PrintQueue {

    private LinkedList<String> queue = new LinkedList<String>();
    private final Object lock = new Object();

    public void enqueue(String str) {
        synchronized (lock) {
            queue.addLast(str);
            lock.notifyAll();
        }
    }

    public void work() {
        String current = "";
        synchronized (lock) {
            if (queue.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    assert (false);
                }
            }
            if (!queue.isEmpty()) {
                current = queue.removeFirst();            //<<------- NoSuchElementException may occured here.
                System.out.println(current);

            }
        }
    }

    public static void main(String[] args) {
        final PrintQueue pq = new PrintQueue();

        Thread producer1 = new Thread() {

            public void run() {
                        try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                pq.enqueue("AAAAAAAA_one");
                pq.enqueue("AAAAAAAA_two");
                pq.enqueue("AAAAAAAA_three");
            }
        };

        Thread producer2 = new Thread() {

            public void run() {
                pq.enqueue("BBBBBBBB_one");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                pq.enqueue("BBBBBBBB_two");
                pq.enqueue("BBBBBBBB_three");
            }
        };

        Thread consumer1 = new Thread() {

            public void run() {
                pq.work();
                pq.work();
                pq.work();
                pq.work();
            }
        };

        Thread consumer2 = new Thread() {

            public void run() {
                pq.work();
                pq.work();
            }
        };

        producer1.start();
        consumer1.start();
        consumer2.start();
        producer2.start();
    }
}
