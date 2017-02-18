package com.core.multithreading.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunil
 */
public class SemaphoreApp {

    public static void main(String[] args) throws InterruptedException {
//        java.util.concurrent.ExecutorService
        System.out.println("start");
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {

                @Override
                public void run() {
                    Connection connection = Connection.getConnection();
                    try {
                        connection.execute();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SemaphoreApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(4, TimeUnit.DAYS);
        System.out.println("shutdown");

    }
}

class Connection {

    private volatile int count = 3;
    private Semaphore semaphore = new Semaphore(3);
    private static Connection connection;

    private Connection() {
    }

    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        }else{
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
        disconnect();
         semaphore.release();
    }

    private void connect() throws InterruptedException {
        synchronized (this) {
            count++;
            System.out.println("CONNECT " + Thread.currentThread().getName() + " : " + count);
        }
    }

    private void disconnect() {
        synchronized (this) {
            count--;
            System.out.println("DISCONNECT " + Thread.currentThread().getName() + " : " + count);
        }
    }

    private void doJob() throws InterruptedException {
        Thread.sleep(4000);
    }
}
