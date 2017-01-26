/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.designpattern.structural.bridge.b;

/** "Abstraction" */
interface Shape {

    public void draw();                             // low-level

    public void resizeByPercentage(double pct);     // high-level
}
