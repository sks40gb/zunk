/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.designpattern.creational.singleton.a;

public class NewSingleton {

    // Private constructor prevents instantiation from other classes
    private NewSingleton() {
    }

    /**
     * SingletonHolder is loaded on the first execution of NewSingleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {

        public static final NewSingleton INSTANCE = new NewSingleton();
    }

    public static NewSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}

/**
 * The INSTANCE is created as soon as the NewSingleton class is initialized. That might even be long before getInstance()
 * is called. It might be (for example) when some static method of the class is used
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
