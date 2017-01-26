package com.sun.designpattern.structural.bridge.a;

/**
 * 1. An abstraction and implementation are in different class hierarchies.
 * 2. Bridge is a synonym for the "handle/body" idiom [Coplien, C++ Report, May 95, p58]. This is a design mechanism
 * that encapsulates an implementation class inside of an interface class. The former is the body, and the latter is
 * the handle. The handle is viewed by the user as the actual class, but the work is done in the body.
 * 3. The bridge pattern is a design pattern used in software engineering which is meant to "decouple an
 * abstraction from its implementation so that the two can vary independently"
 * @author sunil
 */

class TestBridge {

    public static void testCherryPlatform() {
        SodaImpSingleton sodaImpSingleton =
                new SodaImpSingleton(new CherrySodaImp());
        System.out.println(
                "testing medium soda on the cherry platform");
        MediumSoda mediumSoda = new MediumSoda();
        mediumSoda.pourSoda();
        System.out.println(
                "testing super size soda on the cherry platform");
        SuperSizeSoda superSizeSoda = new SuperSizeSoda();
        superSizeSoda.pourSoda();
    }

    public static void testGrapePlatform() {
        SodaImpSingleton sodaImpSingleton =
                new SodaImpSingleton(new GrapeSodaImp());
        System.out.println(
                "testing medium soda on the grape platform");
        MediumSoda mediumSoda = new MediumSoda();
        mediumSoda.pourSoda();
        System.out.println(
                "testing super size soda on the grape platform");
        SuperSizeSoda superSizeSoda = new SuperSizeSoda();
        superSizeSoda.pourSoda();
    }

    public static void testOrangePlatform() {
        SodaImpSingleton sodaImpSingleton =
                new SodaImpSingleton(new OrangeSodaImp());
        System.out.println(
                "testing medium soda on the orange platform");
        MediumSoda mediumSoda = new MediumSoda();
        mediumSoda.pourSoda();
        System.out.println(
                "testing super size soda on the orange platform");
        SuperSizeSoda superSizeSoda = new SuperSizeSoda();
        superSizeSoda.pourSoda();
    }

    public static void main(String[] args) {
        testCherryPlatform();
        testGrapePlatform();
        testOrangePlatform();
    }
}
