package Exchangers_16;

import java.util.ArrayList;
import java.util.List;

/**
 * An exchanger (also known as a rendezvous) is a thread-synchronization construct that lets a pair of threads exchange
 * data items. An exchanger is similar to a cyclic barrier whose count is set to 2 but also supports exchange of data
 * when both threads reach the barrier.
 *
 * Exchanger's Javadoc states that this synchronizer may be useful in genetic algorithms and pipeline designs, where one
 * thread fills a buffer and the other thread empties the buffer. When both threads meet at the exchange point,
 * they swap their buffers.
 */
import java.util.concurrent.Exchanger;

public class ExchangerApp {

    static Exchanger<DataBuffer> exchanger = new Exchanger <DataBuffer> ();
    static DataBuffer initialEmptyBuffer = new DataBuffer();
    static DataBuffer initialFullBuffer = new DataBuffer("ITEM");

    public static void main(String[] args) {
        class FillingLoop implements Runnable {

            int count = 0;

            @Override
            public void run() {
                DataBuffer currentBuffer = initialEmptyBuffer;
                try {
                    while (true) {
                        addToBuffer(currentBuffer);
                        if (currentBuffer.isFull()) {
                            System.out.println("filling loop thread wants to exchange");
                            currentBuffer = exchanger.exchange(currentBuffer);
                            System.out.println("filling loop thread observes an exchange");
                        }
                    }
                } catch (InterruptedException ie) {
                    System.out.println("filling loop thread interrupted");
                }
            }

            void addToBuffer(DataBuffer buffer) {
                String item = "NEWITEM" + count++;
                System.out.printf("Adding %s%n", item);
                buffer.add(item);
            }
        }

        class EmptyingLoop implements Runnable {

            @Override
            public void run() {
                DataBuffer currentBuffer = initialFullBuffer;
                try {
                    while (true) {
                        takeFromBuffer(currentBuffer);
                        if (currentBuffer.isEmpty()) {
                            System.out.println("emptying loop thread wants to exchange");
                            currentBuffer = exchanger.exchange(currentBuffer);
                            System.out.println("emptying loop thread observes an exchange");
                        }
                    }
                } catch (InterruptedException ie) {
                    System.out.println("emptying loop thread interrupted");
                }
            }

            void takeFromBuffer(DataBuffer buffer) {
                System.out.printf("taking %s%n", buffer.remove());
            }
        }

        new Thread(new EmptyingLoop()).start();
        new Thread(new FillingLoop()).start();
    }
}

class DataBuffer {

    private final static int MAX = 10;
    private List<String> items = new ArrayList<String>();

    DataBuffer() {
    }

    DataBuffer(String prefix) {
        for (int i = 0; i < MAX; i++) {
            String item = prefix + i;
            System.out.printf("Adding %s%n", item);
            items.add(item);
        }
    }

    void add(String s) {
        if (!isFull()) {
            items.add(s);
        }
    }

    boolean isEmpty() {
        return items.size() == 0;
    }

    boolean isFull() {
        return items.size() == MAX;
    }

    String remove() {
        if (!isEmpty()) {
            return items.remove(0);
        }
        return null;
    }
}
