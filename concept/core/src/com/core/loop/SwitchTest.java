/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.loop;

/**
 *
 * @author Sunil
 */
public class SwitchTest {

    public static enum Day {

        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    }

    public static void execute(Day day) {
        switch (day) {
            case SUNDAY:
                System.out.println("TODAY IS SUNDAY DAY 1");
                break;
            case MONDAY:
                System.out.println("TODAY IS MONDAY DAY 2");
                break;
            case TUESDAY:
                System.out.println("TODAY IS TUESDAY DAY 3");
                break;
            case WEDNESDAY:
                System.out.println("TODAY IS WEDNESDAY DAY 4");
                break;
            case THURSDAY:
                System.out.println("TODAY IS THURSDAY DAY 5");
                break;
            case FRIDAY:
                System.out.println("TODAY IS FRIDAY DAY 6");
                break;
            case SATURDAY:
                System.out.println("TODAY IS SATURDAY DAY 7");
                break;
        }
    }

    public static void main(String[] args) {
        execute(Day.TUESDAY);
        execute(Day.FRIDAY);
    }
}
