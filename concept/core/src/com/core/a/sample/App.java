package com.core.a.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    public static void main(String... args) throws Exception {
        
         ConnectionPool pool = new ConnectionPool();
        for (int i = 0; i < 10; i++) {
          
           new Thread(()->{
                System.out.println("Getting connection ");
               Connection connection = null;
               try {
                   connection = pool.getConnection();
               } catch (InterruptedException ex) {
                   Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
               }
               System.out.println("Connecion aquired " + connection.getIndex());
               try {
                   Thread.sleep(5000);
               } catch (InterruptedException ex) {
                   Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
               }
               pool.disconnect(connection);
              
           }).start();
            
        }
        
    }

}

class ConnectionPool {

    private int MAX = 2;
    private int size = 0;
    List<Connection> connectionList = new ArrayList<>();
    Semaphore semaphore = new Semaphore(MAX);

    public ConnectionPool() {
        for (int i = 0; i < MAX; i++) {
            connectionList.add(Connection.getConnection());
        }
    }

    public Connection getConnection() throws InterruptedException {
      
        this.semaphore.acquire();
        System.out.println("Aquiring lock....x");
        for (Connection c : connectionList) {
            if (c.isFree()) {
                c.connect();
                return c;
            }
        }
        return null;
    }
    
    public void disconnect(Connection connection){
         System.out.println("Relasing lock " + connection.getIndex());
        connection.release();
        this.semaphore.release();
    }

}

class Connection {

    public int index;
    private static int globalIndex;
    private boolean inUse;

    public Connection() {
       this.index = globalIndex++;
        System.out.println("global " + globalIndex);
    }
    
    public static Connection getConnection() {
     
        return new Connection();
    }

    public boolean isFree() {
        return inUse == false;
    }

    public void connect() {
        this.inUse = true;
    }

    public void release() {
        this.inUse = false;
    }
    
    public int getIndex(){
        return this.index;
    }
}
