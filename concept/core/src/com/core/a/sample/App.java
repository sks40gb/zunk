package com.core.a.sample;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    public static void main(String... args) throws Exception {
        Add add = (a, b) -> a + b;
        Predicate<Integer> predicate = (a) -> a > 1;

        int one = 5;
        int two = 200;

        Function<Integer, Integer> sqaure = (a) -> a * a;
        
        Supplier<Integer> times = ()-> 10; 
        
        Consumer<Integer> print = (a)->System.out.println("Value is : " +a * times.get());

        if (predicate.test(one)) {
            print.accept(sqaure.apply(one));
        }

    }

    interface Add {

        int add(int a, int b);

    }
}
