package com.core.multithreading.program;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
public class ConnectionPool {

    public static void main(String[] args) throws InterruptedException {
        final Pool pool = new Pool();
        ExecutorService executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
//                        System.out.println("Thread " + Thread.currentThread() + " is ready : ");
                        Connection connection = pool.getConnection();
                        Thread.sleep(new Random().nextInt(2000));
                        pool.releaseConnection(connection);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
        executor.awaitTermination(1, TimeUnit.MINUTES);
        executor.shutdown();
    }

}

class Pool {

    private final int MAX_CONNECTION = 10;
    private final Semaphore semaphore = new Semaphore(MAX_CONNECTION);
    private final List<Connection> connections = new ArrayList<Connection>();

    public Pool() {
        for (int i = 0; i < MAX_CONNECTION; i++) {
            connections.add(new Connection());
        }
    }

    public Connection getConnection() throws InterruptedException {
        semaphore.acquire();
        return getNextConnection();
    }

    public void releaseConnection(Connection connection) {
        if (markUnused(connection)) {
            semaphore.release();
            System.out.println("RELEASING : " + connection.getId() + " by " + Thread.currentThread().getName());
        }
    }

    public synchronized boolean markUnused(Connection connection) {
        for (Connection c : connections) {
            if (c == connection) {
                connection.setUsed(false);
                return true;
            }
        }
        return false;
    }

    private synchronized Connection getNextConnection() {
        for (Connection c : connections) {
            if (!c.isUsed()) {
                System.out.println("CONNECTING : " + c.getId() + " by " + Thread.currentThread().getName());
                c.setUsed(true);
                return c;
            }
        }
        return null;
    }

}

class Connection {

    private static int CONNECTION_COUNT;
    private boolean used;
    private int id;

    public Connection() {
        CONNECTION_COUNT++;
        id = CONNECTION_COUNT;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

}
