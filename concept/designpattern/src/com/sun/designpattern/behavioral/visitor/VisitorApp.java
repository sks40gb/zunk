package com.sun.designpattern.behavioral.visitor;

import java.text.DecimalFormat;

/**
 * Note : Its moves the functionality to external class Visitors.
 * If any change is required concrete class will not be
 * impacted instead Visitor class has to be modified.
 */
public class VisitorApp {

    public static void main(String[] args) {

        TaxVisitor taxCalc = new TaxVisitor();
        TaxHolidayVisitor taxHolidayCalc = new TaxHolidayVisitor();

        Necessity milk = new Necessity(3.47);
        Liquor vodka = new Liquor(11.99);
        Tobacco cigars = new Tobacco(19.99);

        System.out.println(milk.accept(taxCalc) + "\n");
        System.out.println(vodka.accept(taxCalc) + "\n");
        System.out.println(cigars.accept(taxCalc) + "\n");

        System.out.println("TAX HOLIDAY PRICES\n");

        System.out.println(milk.accept(taxHolidayCalc) + "\n");
        System.out.println(vodka.accept(taxHolidayCalc) + "\n");
        System.out.println(cigars.accept(taxHolidayCalc) + "\n");

    }
}

interface Visitor {

    // Created to automatically use the right
    // code based on the Object sent
    // Method Overloading
    double visit(Liquor liquorItem);

    double visit(Tobacco tobaccoItem);

    double visit(Necessity necessityItem);

}

// Concrete Visitor Class
class TaxVisitor implements Visitor {

    // This formats the item prices to 2 decimal places
    DecimalFormat df = new DecimalFormat("#.##");

    // This is created so that each item is sent to the
    // right version of visit() which is required by the
    // Visitor interface and defined below
    public TaxVisitor() {
    }

    // Calculates total price based on this being taxed
    // as a liquor item
    public double visit(Liquor liquorItem) {
        System.out.println("Liquor Item: Price with Tax");
        return Double.parseDouble(df.format((liquorItem.getPrice() * .18)
            + liquorItem.getPrice()));
    }

    // Calculates total price based on this being taxed
    // as a tobacco item
    public double visit(Tobacco tobaccoItem) {
        System.out.println("Tobacco Item: Price with Tax");
        return Double.parseDouble(df.format((tobaccoItem.getPrice() * .32)
            + tobaccoItem.getPrice()));
    }

    // Calculates total price based on this being taxed
    // as a necessity item
    public double visit(Necessity necessityItem) {
        System.out.println("Necessity Item: Price with Tax");
        return Double.parseDouble(df.format(necessityItem.getPrice()));
    }

}

interface Visitable {
    
    double accept(Visitor visitor);

}

class Liquor implements Visitable {

    private final double price;

    Liquor(double item) {
        price = item;
    }

    public double accept(Visitor visitor) {
        return visitor.visit(this);
    }

    public double getPrice() {
        return price;
    }

}

class Necessity implements Visitable {

    private final double price;

    Necessity(double item) {
        price = item;
    }

    public double accept(Visitor visitor) {
        return visitor.visit(this);
    }

    public double getPrice() {
        return price;
    }

}

class Tobacco implements Visitable {

    private final double price;

    Tobacco(double item) {
        price = item;
    }

    public double accept(Visitor visitor) {
        return visitor.visit(this);
    }

    public double getPrice() {
        return price;
    }

}

class TaxHolidayVisitor implements Visitor {

    // This formats the item prices to 2 decimal places
    DecimalFormat df = new DecimalFormat("#.##");

    // This is created so that each item is sent to the
    // right version of visit() which is required by the
    // Visitor interface and defined below
    public TaxHolidayVisitor() {
    }

    // Calculates total price based on this being taxed
    // as a liquor item
    public double visit(Liquor liquorItem) {
        System.out.println("Liquor Item: Price with Tax");
        return Double.parseDouble(df.format((liquorItem.getPrice() * .10)
            + liquorItem.getPrice()));
    }

    // Calculates total price based on this being taxed
    // as a tobacco item
    public double visit(Tobacco tobaccoItem) {
        System.out.println("Tobacco Item: Price with Tax");
        return Double.parseDouble(df.format((tobaccoItem.getPrice() * .30)
            + tobaccoItem.getPrice()));
    }

    // Calculates total price based on this being taxed
    // as a necessity item
    public double visit(Necessity necessityItem) {
        System.out.println("Necessity Item: Price with Tax");
        return Double.parseDouble(df.format(necessityItem.getPrice()));
    }

}
