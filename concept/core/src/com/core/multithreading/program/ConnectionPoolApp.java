package com.core.multithreading.program;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionPoolApp {

    public static void main(String[] args) {
        ConnectionPool connectionPool = new ConnectionPool();
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                Connection connection = connectionPool.getConnection();
                connection.execute();
                connection.disconnect();
            }).start();
        }
    }
}

class ConnectionPool {

    private final List<Connection> connections = new ArrayList<>();
    private static int MAX = 2;
    private static int INDEX = 0;

    public ConnectionPool() {
        for (int i = 0; i < MAX; i++) {
            connections.add(new Connection() {
                private boolean closed = false;
                private int id = INDEX++;
                DBConnection dbConnection = new DBConnection();

                @Override
                public int getId() {
                    return id;
                }

                @Override
                public void connect() {
                    closed = true;
                }

                @Override
                public void disconnect() {
                    closed = false;
                    synchronized (connections) {
                        connections.notifyAll();
                    }
                }

                @Override
                public boolean isClosed() {
                    return closed;
                }

                @Override
                public boolean isOpen() {
                    return !closed;
                }

                @Override
                public void execute() {
                    System.out.println("Connection " + this.getId());
                    dbConnection.execute();
                }
            });
        }
    }

    public Connection getConnection() {
        Connection connection = null;
        while (connection == null) {
            for (Connection c : connections) {
                if (c.isOpen()) {
                    connection = c;
                    break;
                }
            }
            if (connection == null) {
                synchronized (connections) {
                    try {
                        connections.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ConnectionPoolApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        connection.connect();
        return connection;
    }

}

interface Connection {

    public int getId();

    public boolean isClosed();

    public boolean isOpen();

    public void connect();

    public void disconnect();

    public void execute();

}

class DBConnection {

    public void execute() {
        System.out.println("Performing heavy duty work");
        try {
            Thread.sleep((int) (Math.random() * 10000));
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
