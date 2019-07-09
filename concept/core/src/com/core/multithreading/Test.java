package com.core.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunil
 */
public class Test {

    public static void main(String... rars) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                Connection connection = Connection.getConnection();
                try {
                    connection.execute();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

        }
        executorService.shutdown();
    }

    static class Connection {

        private static Connection connection;
        private Semaphore  semaphore = new Semaphore(2);

        private Connection() {

        }

        public static Connection getConnection() {
            if (connection != null) {
                return connection;
            } else {
                synchronized (Connection.class) {
                    if (connection == null) {
                        connection = new Connection();
                    }
                }
            }
            return connection;
        }

        public void execute() throws InterruptedException {
            semaphore.acquire();
            connect();
            doJob();
            disconnnect();
            semaphore.release();
        }

        public void connect() {
            synchronized (this) {
                System.out.println("Connected : " + Thread.currentThread().getName());
            }

        }

        public void disconnnect() {
            synchronized (this) {
                System.out.println("Disconnected : " + Thread.currentThread().getName());
            }

        }

        public void doJob() {
            try {
                System.out.println("Job started " + Thread.currentThread().getName());
                Thread.sleep((int) (Math.random() * 10000));
                System.out.println("Job Completed " + Thread.currentThread().getName());
            } catch (InterruptedException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
