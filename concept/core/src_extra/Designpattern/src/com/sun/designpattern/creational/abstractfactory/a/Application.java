/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.designpattern.creational.abstractfactory.a;

/**
 *
 * @author sunil
 */
class Application {

    public Application(GUIFactory factory) {
        Button button = factory.createButton();
        button.paint();
    }
}
