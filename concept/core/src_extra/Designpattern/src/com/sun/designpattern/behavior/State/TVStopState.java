package com.sun.designpattern.behavior.State;

/**
 *
 * @author Sunil
 */
public class TVStopState implements State {

    @Override
    public void doAction() {
        System.out.println("TV is turned OFF");
    }
}
