package test;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sunsingh
 */
public class App {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Connection connection = Connection.getConnection();
                    connection.execute();
                }
            });
            t.start();
        }
    }

}

class Connection {

    private static int count = 0;
    private static final int LIMIT = 1;
    private static Connection connection;
    private final Semaphore semaphore = new Semaphore(2);

    private Connection() {

    }

    public static Connection getConnection() {
        if (connection == null) {
            synchronized (Connection.class) {
                if (connection == null) {
                    connection = new Connection();
                }
            }
        }
        return connection;
    }

    private void connect() {
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        synchronized (semaphore) {
            count++;
            System.out.println("Connection equired : " + count + " by : " + Thread.currentThread().getName());
        }

    }

    private void disconnect() {
        count--;
        System.out.println("Connection released : " + count + " by : " + Thread.currentThread().getName());
        semaphore.release();

    }

    private void doJob() {
        try {
            Thread.sleep(new Random().nextInt(5000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        connect();
        doJob();
        disconnect();
    }
}
