package com.core.multithreading;

import java.util.ArrayList;
/**
 *
 * @author sunil
 */
public class ClientThread extends Thread {
     
    public ClientThread(Runnable clientTask) {
        super(clientTask);
    }    
    
    private ArrayList<String> jobList = new ArrayList<String>();
    public static void main(String[] args) {
        System.out.println("---------------> ");
        Runnable clientTask = new ClientTask();
        ClientThread t = new ClientThread(clientTask);
        t.start();    
        synchronized(clientTask){
            try{
            clientTask.wait();            
            }catch(Exception e){
                e.printStackTrace();
            }
        }        
        System.out.println("===============>");
    }    
}


class ClientTask implements Runnable{
    public void run(){
        System.out.println("inside clientTask");
        synchronized(this){
            try{                                              
               Thread.sleep(5000);
            }catch(Exception e){
                e.printStackTrace();
            }            
        notify();        
        System.out.println("outside clientTask");    
        }
    }
    
}