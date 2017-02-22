package com.core.multithreading.program;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunil
 */
public class Main {
    public static void main(String[] args) throws Exception{
        final IPool iPool = new IPool(2);
        for (int i = 0; i < 50; i++) {
           new Thread(new Runnable() {
               @Override
               public void run() {
                  IConnection connection;
                   System.out.println("Thread starting : " + Thread.currentThread().getName());
                   try {
                       connection = iPool.getConnection();
                       Thread.sleep(2000);
                       iPool.disconnection(connection);
                   } catch (InterruptedException ex) {
                       Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }
           }).start();
        }
    }
}


class IPool{
    
    private final List<IConnection> connections;
    private final int MAX_SIZE;
    private final Semaphore semaphore;

    public IPool(int size) {
       this.connections = new ArrayList();
       semaphore = new Semaphore(size);
       MAX_SIZE = size;
    
       for (int i = 0; i < MAX_SIZE; i++) {
            this.connections.add(new IConnection(i));
        }
    }
    
    public IConnection getConnection() throws InterruptedException{
        semaphore.acquire();
        for(IConnection connection : connections){
            if(!connection.isConnected()){
                connection.connect();
                System.out.println("connecting " + connection);
                return connection;
            }
        }
        return null;
    }
    
     public void disconnection(IConnection _connection) throws InterruptedException{
       
        for(IConnection connection : connections){
            if(connection == _connection){
                connection.disconnect();
                System.out.println("disconnecting " + connection);
                semaphore.release();
            }
        }
    }
    
    
    
}

class IConnection{
    private String name;
    private boolean connected;
    private int id;

    public IConnection(int id) {
        this.id = id;
    }
    public void connect(){
       this.connected = true;
    }
    
    public void disconnect(){
        this.connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    
    
    @Override
    public String toString() {
        return "IConnection{" + "connected=" + connected + ", id=" + id + '}';
    }
    
    
}