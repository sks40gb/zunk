package com.core.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunil
 */
public class EvenOddNumberThreadTest {

    public static void main(String[] args) throws InterruptedException {
        List<Integer> count =  new ArrayList();
        Thread evenThread = new NumberThread("Even",count);
        Thread oddThread = new NumberThread("Odd",count);
        oddThread.start();
        Thread.sleep(5000);
        evenThread.start();
    }

}


class NumberThread extends Thread{
    List<Integer> count;
    public NumberThread(String threadName, List<Integer> count){
        this.count = count;
        setName(threadName);
    }
    public void run(){
        while(true){
            synchronized(count){
                count.add(count.size()+1);
                System.out.println("Thread "  + getName() + " : " + count.size());
                count.notifyAll();
                try {
                    count.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(NumberThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}