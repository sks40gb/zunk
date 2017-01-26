package com.sun.designpattern.creational.prototype;

/**
 * The prototype pattern is a creational design pattern used in software development when the type of objects to
 * create is determined by a prototypical instance, which is cloned to produce new objects.
 *
 * This pattern is used to:---------------
 * 1. avoid subclasses of an object creator in the client application, like the abstract factory pattern does.
 * 2. avoid the inherent cost of creating a new object in the standard way (e.g., using the 'new' keyword) when it is
 *    prohibitively expensive for a given application.
 *
 * Prototype Class
 */


/**
 * Client Class
 */
public class PrototypeApp {

    private Prototype example; // Could have been a private Cloneable example.

    public PrototypeApp(Prototype example) {
        this.example = example;
    }

    public Prototype makeCopy() throws CloneNotSupportedException {
        return (Prototype) this.example.clone();
    }

    public static void main(String args[]) {
        try {
            Prototype tempExample = null;
            int num = 1000;
            Prototype prot = new PrototypeImpl(1000);
            PrototypeApp cm = new PrototypeApp(prot);
            for (int i = 0; i < 10; i++) {
                tempExample = cm.makeCopy();
                tempExample.prototype(i * num);
                tempExample.printValue();
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}

abstract class Prototype implements Cloneable {

    public Object clone() throws CloneNotSupportedException {
        // call Object.clone()
        Prototype copy = (Prototype) super.clone();
        //In an actual implementation of this pattern you might now change references to
        //the expensive to produce parts from the copies that are held inside the prototype.
        return copy;
    }

    abstract void prototype(int x);

    abstract void printValue();
}

/**
 * Concrete Prototypes to clone
 */
class PrototypeImpl extends Prototype {

    int x;

    public PrototypeImpl(int x) {
        this.x = x;
    }

    @Override
    void prototype(int x) {
        this.x = x;
    }

    public void printValue() {
        System.out.println("Value :" + x);
    }
}
