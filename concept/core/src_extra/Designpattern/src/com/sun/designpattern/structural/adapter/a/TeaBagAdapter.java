package com.sun.designpattern.structural.adapter.a;

/**
 * the adapter
 * @author sunil
 */
public class TeaBagAdapter extends TeaBag {
   private TeaBox teaBox;
   
   public TeaBagAdapter(TeaBox teaBox) {
       this.teaBox = teaBox;
       teaBagIsSteeped = this.teaBox.teaIsSteeped;
   }
    
   public void steepTeaInCup() {
       teaBox.steepTea();
       teaBagIsSteeped = true;
   }
}