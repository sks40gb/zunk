/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.designpattern.creational.abstractfactory.a;

/**
 *
 * @author sunil
 */
class OSXFactory implements GUIFactory {

    public Button createButton() {
        return new OSXButton();
    }
}
