package com.core.java8;

/**
 *
 * @author sks
 */
public class LambdaWithCustomApp {

    private int serialId = 10;

    public static void main(String[] args) {
        new LambdaWithCustomApp().print();
    }

    public void print() {

        int originalNumber = 18;
        //double the value
        new Executers().start((value) -> value * 2, originalNumber);

        //half the value
        new Executers().start((value) -> value / 2, originalNumber);

        //this represents the current class, which is LambdaWithCustomApp in this case.
        new Executers().start((value) -> {
            System.out.println(this.serialId);
            this.serialId = 100;
            return originalNumber;

        }, originalNumber);

        System.out.println("serial new value : " + this.serialId);

    }

}

class Executers {

    public void start(Executable e, int value) {
        System.out.println("Printing information : " + e.execute(value));
    }
}

interface Executable {

    public int execute(int value);
}
