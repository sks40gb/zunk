package com.sun.designpattern.creational.prototype;

import java.util.HashMap;
import java.util.Map;

/**
 * Prototype allows us to hide the complexity of making new
 * instances from the client. The concept is to copy an existing
 * object rather than creating a new instance from scratch,
 * something that may include costly operations.
 */
public class PrototypeApp {

    public static void main(String args[]) {
        ColorStore.getColor("blue").addColor();
        ColorStore.getColor("black").addColor();
        ColorStore.getColor("black").addColor();
        ColorStore.getColor("blue").addColor();
    }
}

abstract class Color implements Cloneable {

    protected String colorName;

    abstract void addColor();

    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}

class BlueColor extends Color {

    public BlueColor() {
        this.colorName = "blue";
    }

    @Override
    void addColor() {
        System.out.println("Blue color added");
    }

}

class BlackColor extends Color {

    public BlackColor() {
        this.colorName = "black";
    }

    @Override
    void addColor() {
        System.out.println("Black color added");
    }
}

class ColorStore {

    private static final Map<String, Color> colorMap =
        new HashMap<String, Color>();

    static {
        colorMap.put("blue", new BlueColor());
        colorMap.put("black", new BlackColor());
    }

    public static Color getColor(String colorName) {
        return (Color) colorMap.get(colorName).clone();
    }
}
