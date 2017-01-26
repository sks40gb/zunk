package com.sun.designpattern.structural.adapter.a;
/**
 * the class which the adapter will make the adaptee adapt to
 * @author sunil
 */
public class TeaBag {

    boolean teaBagIsSteeped;

    public TeaBag() {
        teaBagIsSteeped = false;
    }

    public void steepTeaInCup() {
        teaBagIsSteeped = true;
        System.out.println("tea bag is steeping in cup");
    }
}
