package com.sun.designpattern.creational.abstractfactory;

/**
 * The abstract factory pattern is a software design pattern that provides a way to encapsulate a group of individual
 * factories that have a common theme.
 *
 * The client does not know (or care) which concrete objects it gets from each of these internal factories since it uses
 * only the generic interfaces of their products.
 *
 * @author sunil
 */
public class AbstractFactoryApp {

    public static void main(String[] args) {
        Application application = new Application(createOsSpecificFactory());
    }

    static GUIFactory createOsSpecificFactory() {
        int sys = 1; //readFromConfigFile("OS_TYPE");
        if (sys == 0) {
            return new WinFactory();
        } else {
            return new OSXFactory();
        }
    }
}

class Application {

    public Application(GUIFactory factory) {
        Button button = factory.createButton();
        button.paint();
    }
}

interface Button {

    public void paint();
}

class WinButton implements Button {

    public void paint() {
        System.out.println("I'm a WinButton");
    }
}

class OSXButton implements Button {

    public void paint() {
        System.out.println("I'm an OSXButton");
    }
}

interface GUIFactory {

    public Button createButton();
}

class WinFactory implements GUIFactory {

    public Button createButton() {
        return new WinButton();
    }
}

class OSXFactory implements GUIFactory {

    public Button createButton() {
        return new OSXButton();
    }
}


