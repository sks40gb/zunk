package com.sun.designpattern.behavior.State;

/**
 *
 * @author Sunil
 */
public class TVStartState implements State {

    @Override
    public void doAction() {
        System.out.println("TV is turned ON");
    }
}
