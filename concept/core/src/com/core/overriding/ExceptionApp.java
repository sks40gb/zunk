package com.core.overriding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.collections.transformation.SortedList;

/**
 *
 * @author Sunil
 */
public class ExceptionApp {

}

interface Colorable {

    public List color() throws IOException;
}

class Toy implements Colorable {

    @Override
    public ArrayList color() {
        System.out.println("coloring Toy");
        return null;
    }

}

class Machine implements Colorable {

    @Override
    public LinkedList color() throws RuntimeException {
        System.out.println("coloring machine");
        return null;
    }

}

class Device implements Colorable {

    @Override
    public SortedList color() throws IOException, RuntimeException {
        System.out.println("coloring Device");
        return null;
    }

}
