package com.sun.designpattern.structural.adapter.a;

/**
 * In the Adapter Design Pattern, a class converts the interface of one class to be what another class expects.
 * The adapter does this by taking an instance of the class to be converted (the adaptee) and uses the methods the
 * adaptee has available to create the methods which are expected.In this example we have a TeaBall
 * class which takes in an instance of LooseLeafTea. The TeaBall class uses the steepTea method from LooseLeafTea
 * and adapts it to provide the steepTeaInCup method which the TeaCup class requires.
 * @author sunil
 */
class TestTeaBagAdaptation {

    public static void main(String[] args) {
        TeaCup teaCup = new TeaCup();

        System.out.println("Steeping tea bag");
        TeaBag teaBag = new TeaBag();
        teaCup.steepTeaBag(teaBag);

         System.out.println("Steeping tea box");
        TeaBox teaBox = new TeaBox();
        TeaBagAdapter teaBall = new TeaBagAdapter(teaBox);
        teaCup.steepTeaBag(teaBall);
    }
}
