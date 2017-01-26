package com.sun.designpattern.structural.adapter.a;

/**
 * the class that accepts class TeaBag in it's steepTeaBag() method, and so is being adapted for.
 * @author sunil
 */
public class TeaCup {

    public void steepTeaBag(TeaBag teaBag) {
        teaBag.steepTeaInCup();
    }
}
