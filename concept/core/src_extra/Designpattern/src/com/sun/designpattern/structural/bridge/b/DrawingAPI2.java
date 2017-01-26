/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.designpattern.structural.bridge.b;

/** "ConcreteImplementor" 2/2 */
class DrawingAPI2 implements DrawingAPI {

    public void drawCircle(double x, double y, double radius) {
        System.out.printf("API2.circle at %f:%f radius %f\n", x, y, radius);
    }
}
