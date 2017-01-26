package com.sun.designpattern;

public class Test {

    public static void main(String[] args) {
        RegularFood rice = new RegularFood(2);
        Tobacco tobacco = new Tobacco(4);

        RegularTax regularTax = new RegularTax();
        HolidayTax holidayTax = new HolidayTax();

        System.out.println(rice.accept(regularTax));
        System.out.println(tobacco.accept(regularTax));
        System.out.println("HOLIDAY");
        System.out.println(rice.accept(holidayTax));
        System.out.println(tobacco.accept(holidayTax));
    }

}

interface Visitable {

    public double accept(Visitor visitor);

    public double getPrice();
}

class RegularFood implements Visitable {

    private double price;

    public RegularFood(double price) {
        this.price = price;
    }

    public double accept(Visitor visitor) {
        return visitor.visit(this);
    }

    public double getPrice() {
        return price;
    }
}

class Tobacco implements Visitable {

    private double price;

    public Tobacco(double price) {
        this.price = price;
    }

    public double accept(Visitor visitor) {
        return visitor.visit(this);
    }

    public double getPrice() {
        return price;
    }
}

interface Visitor {

    public double visit(RegularFood regularFood);

    public double visit(Tobacco tobacco);

}

class RegularTax implements Visitor {

    public double visit(RegularFood regularFood) {
        return regularFood.getPrice() * 1;
    }

    public double visit(Tobacco tobacco) {
        return tobacco.getPrice() * 1.2;
    }
}

class HolidayTax implements Visitor {

    public double visit(RegularFood regularFood) {
        return regularFood.getPrice() * 1.1;
    }

    public double visit(Tobacco tobacco) {
        return tobacco.getPrice() * 2;
    }
}
