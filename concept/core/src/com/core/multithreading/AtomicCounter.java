package com.core.multithreading;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Sunil
 */
public class AtomicCounter {

    AtomicInteger integer;

    public AtomicCounter(AtomicInteger integer) {
        this.integer = integer;
    }

    public void increment() {
        integer.incrementAndGet();
    }

    public void decrement() {
        integer.decrementAndGet();
    }

    public int value() {
        return integer.get();
    }
}
