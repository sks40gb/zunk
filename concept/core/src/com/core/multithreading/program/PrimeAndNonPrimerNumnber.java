package com.core.multithreading.program;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunil
 */
public class PrimeAndNonPrimerNumnber {

    public static void main(String[] args) throws InterruptedException {
        AtomicNumber number = new AtomicNumber();
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        executor.execute(new PrintPrime(lock, condition, number));
        executor.execute(new PrintNonPrime(lock, condition, number));
        executor.execute(new PrintNonPrime(lock, condition, number));
        executor.execute(new PrintNonPrime(lock, condition, number));
        executor.awaitTermination(1, TimeUnit.DAYS);
    }
}

class PrintNonPrime implements Runnable {

    public PrintNonPrime(Lock lock, Condition condition, AtomicNumber number) {
        this.lock = lock;
        this.condition = condition;
        this.number = number;
    }

    private AtomicNumber number;
    private final Lock lock;
    private final Condition condition;

    public void run() {
        System.out.println("STARTING  : " + Thread.currentThread().getName());
        while (true) {
            lock.lock();
            try {
                print();
            } finally {
                lock.unlock();
            }
            try {
                Thread.sleep(new Random().nextInt(2000));
            } catch (InterruptedException ex) {
                Logger.getLogger(PrintNonPrime.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void print() {

        if (!number.isPrimeNumber()) {
            System.out.println("Non Prime  : " + number.getNumber() + " by " + Thread.currentThread().getName());
            number.inrement();
            condition.signalAll();
//                System.out.println(getClass() + " SINGAL");
        } else {
            try {
//                    System.out.println(getClass() + " AWAIT");
                condition.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(PrintNonPrime.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}

class PrintPrime implements Runnable {

    private AtomicNumber number;
    private final Lock lock;
    private final Condition condition;

    public PrintPrime(Lock lock, Condition condition, AtomicNumber number) {
        this.number = number;
        this.lock = lock;
        this.condition = condition;
    }

    public void run() {
        System.out.println("STARTING  : " + Thread.currentThread().getName());
        while (true) {
            lock.lock();
            try {
                print();
            } finally {
                lock.unlock();
            }
            try {
                Thread.sleep(new Random().nextInt(2000));
            } catch (InterruptedException ex) {
                Logger.getLogger(PrintNonPrime.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void print() {

        if (number.isPrimeNumber()) {
            System.err.println("Prime Number  : " + number.getNumber() + " by " + Thread.currentThread().getName()); 
            number.inrement();
            condition.signalAll();
           
//                System.out.println(getClass() + " SINGAL");
        } else {
            try {
                condition.await();
//                    System.out.println(getClass() + " WAIT");
            } catch (InterruptedException ex) {
                Logger.getLogger(PrintNonPrime.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

class AtomicNumber {

    int number;

    public void inrement() {
        number++;
    }

    public int getNumber() {
        return number;
    }

    public boolean isPrimeNumber() {
        for (int count = 2; count < Math.sqrt(number); count++) {
            if (number % count == 0) {
                return false;
            }
        }
        return true;
    }
}
