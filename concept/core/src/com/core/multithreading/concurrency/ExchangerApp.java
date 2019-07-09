package com.core.multithreading.concurrency;

/**
 *
 * @author Sunil
 */
import java.util.concurrent.Exchanger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExchangerApp {

    public static void main(String[] args) {
        Exchanger<Country> exchanger = new Exchanger();
        // Starting two threads
        new Thread(new Producer(exchanger)).start();
        new Thread(new Consumer(exchanger)).start();
    }

    static class Country {

        private String countryName;

        public Country(String countryName) {
            super();
            this.countryName = countryName;
        }

        public String getCountryName() {
            return countryName;
        }
    }

    static class Producer implements Runnable {

        Exchanger<Country> ex;

        Producer(Exchanger ex) {
            this.ex = ex;

        }

        @Override
        public void run() {
            for (int i = 0; i < 2; i++) {
                Country country = null;
                if (i == 0) {
                    country = new Country("India");
                } else {
                    country = new Country("Bhutan");
                }
                // exchanging with an dummy Country object
                Country dummyCountry;
                try {
                    dummyCountry = ex.exchange(country);
                    System.out.println("Got country object from Consumer thread : " + dummyCountry.getCountryName());
                } catch (InterruptedException ex) {
                    Logger.getLogger(ExchangerApp.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    static class Consumer implements Runnable {

        Exchanger<Country> ex;

        Consumer(Exchanger<Country> ex) {
            this.ex = ex;
        }

        @Override
        public void run() {
            for (int i = 0; i < 2; i++) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ExchangerApp.class.getName()).log(Level.SEVERE, null, ex);
                }
                // Getting Country object from producer thread
                // giving dummy country object in return
                Country country;
                try {
                    country = ex.exchange(new Country("Dummy"));
                    System.out.println("Got country object from Producer thread : " + country.getCountryName());
                } catch (InterruptedException ex) {
                    Logger.getLogger(ExchangerApp.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

}
