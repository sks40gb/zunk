package com.core.multithreading.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunil
 * 
 */
public class FutureApp {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        new FutureApp().main();
    }

    public void main() throws InterruptedException, ExecutionException {
        List<Integer> items = new ArrayList<Integer>();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        //EXECUTE IN PARALLEL
        Future<List<Integer>> future1 = executor.submit(new Task(items));
        Future<List<Integer>> future2 = executor.submit(new Task(items));
        Future<List<Integer>> future4 = executor.submit(new Task(items));
        Future<List<Integer>> future3 = executor.submit(new Task(items));
        System.out.println("FUTURE 1 : " + future1.get().size());
        System.out.println("FUTURE 2 : " + future2.get().size());
        System.out.println("FUTURE 3 : " + future3.get().size());
        System.out.println("FUTURE 4 : " + future4.get().size());

        //EXECUTES IN SEQUENCE
        /*
        Future<List<Integer>> future1 = executor.submit(new Task(items));
        System.out.println("FUTURE 1 : " + future1.get().size());
        Future<List<Integer>> future2 = executor.submit(new Task(items));
        System.out.println("FUTURE 2 : " + future2.get().size());
        Future<List<Integer>> future4 = executor.submit(new Task(items));
        System.out.println("FUTURE 3 : " + future3.get().size());
        Future<List<Integer>> future3 = executor.submit(new Task(items));
        System.out.println("FUTURE 4 : " + future4.get().size());
         * */

        executor.shutdown();
    }
}

class Task implements Callable<List<Integer>> {

    List<Integer> items;
    private static int count = 0;

    public Task(List<Integer> items) {
        this.items = items;
    }

    public List<Integer> call() {
        System.out.println("Task " + ++count + " started");
        for (int i = 0; i < 10; i++) {
            items.add(i);
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException ex) {
                Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return items;
    }
}
