package com.sun.designpattern.structural.flyweight.a;

public class TeaOrderContext {

    int tableNumber;

    TeaOrderContext(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getTable() {
        return this.tableNumber;
    }
}
