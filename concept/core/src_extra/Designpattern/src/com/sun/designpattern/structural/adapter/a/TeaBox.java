package com.sun.designpattern.structural.adapter.a;

/**
 * the adaptee
 * @author sunil
 */
public class TeaBox {
   boolean teaIsSteeped; 
    
   public TeaBox() {
       teaIsSteeped = false;
   }
   
   public void steepTea() {
       teaIsSteeped = true;
       System.out.println("tea box is steeping");
   }
}
