package CyclicBarrier_15;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/*
 * Cyclic barriers can be used to perform lengthy calculations by breaking them into smaller individual tasks
 * (as demonstrated by CyclicBarrier's Javadoc example code). They're also used in multiplayer games that cannot
 * start until the last player has joined, as shown in Listing 5.
 *
 * A cyclic barrier is a thread-synchronization construct that lets a set of threads wait for each other to reach a
 * common barrier point. The barrier is called cyclic because it can be re-used after the waiting threads are
 * released.
 *
 * await() throws InterruptedException when the thread that invoked this method is interrupted while waiting.
 * This method throws BrokenBarrierException when another thread was interrupted while the invoking thread was waiting,
 * the barrier was broken when await() was called, or the barrier action (when present) failed because an exception was
 * thrown from the runnable's run() method.
 */
public class CyclicBarrierApp {

    public static void main(String[] args) {
        Runnable action = new Runnable() {

            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                System.out.printf("Thread %s executing barrier action. %n", name);
            }
        };
        final CyclicBarrier barrier = new CyclicBarrier(3, action);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                System.out.printf("%s about to join game...%n",
                        name);
                try {
                    barrier.await();
                } catch (BrokenBarrierException bbe) {
                    System.out.println("barrier is broken");
                    return;
                } catch (InterruptedException ie) {
                    System.out.println("thread interrupted");
                    return;
                }
                System.out.printf("%s has joined game%n", name);
            }
        };
        ExecutorService[] executors = new ExecutorService[]{
            Executors.newSingleThreadExecutor(),
            Executors.newSingleThreadExecutor(),
            Executors.newSingleThreadExecutor()
        };
        for (ExecutorService executor : executors) {
            executor.execute(task);
            executor.shutdown();
        }
    }
}
