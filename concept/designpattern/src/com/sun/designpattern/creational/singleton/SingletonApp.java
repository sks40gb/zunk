package com.sun.designpattern.creational.singleton;

public class SingletonApp {

    // Private constructor prevents instantiation from other classes
    private SingletonApp() {
    }

    /**
     * SingletonHolder is loaded on the first execution of NewSingleton.getInstance() or the first access to
     * SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {

        public static final SingletonApp INSTANCE = new SingletonApp();
    }

    public static SingletonApp getInstance() {
        return SingletonHolder.INSTANCE;
    }
}

/**
 * The INSTANCE is created as soon as the NewSingleton class is initialized. That might even be long before
 * getInstance() is called. It might be (for example) when some static method of the class is used
 *
 * @author sunil
 */
class TraditionalSingleton {

    private static final TraditionalSingleton INSTANCE = new TraditionalSingleton();

    // Private constructor prevents instantiation from other classes
    private TraditionalSingleton() {
    }

    public static TraditionalSingleton getInstance() {
        return INSTANCE;
    }
}
