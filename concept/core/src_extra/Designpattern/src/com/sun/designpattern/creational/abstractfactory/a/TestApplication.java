/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.designpattern.creational.abstractfactory.a;


/**
 * The abstract factory pattern is a software design pattern that provides a way to encapsulate a group of individual
 * factories that have a common theme.
 *
 * The client does not know (or care) which concrete objects it gets from each of these internal factories since it
 * uses only the generic interfaces of their products.
 * 
 * @author sunil
 */
public class TestApplication {

    public static void main(String[] args) {
        new Application(createOsSpecificFactory());
    }

    public static GUIFactory createOsSpecificFactory() {
        int sys = 1 ; //readFromConfigFile("OS_TYPE");
        if (sys == 0) {
            return new WinFactory();
        } else {
            return new OSXFactory();
        }
    }
}
