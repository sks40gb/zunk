package com.sun.designpattern.creational.builder;

/**
* 1) too many constructors to maintain.
* 2) error prone because many fields has same type e.g. sugar and and butter are in cups so instead of 
* 2 cup sugar if you pass 2 cup butter, your compiler will not complain but will get a buttery cake 
* with almost no sugar with high cost of wasting butter.
*/
public class BuilderApp {

    public static void main(String[] args) {

        Cook cook = new Cook();

        PizzaBuilder hawaiianPizzaBuilder = new HawaiianPizzaBuilder();
        cook.setPizzaBuilder(hawaiianPizzaBuilder);
        cook.constructPizza();
        Pizza hawaiian = cook.getPizza();
        System.out.println(hawaiian);

        PizzaBuilder spicyPizzaBuilder = new SpicyPizzaBuilder();
        cook.setPizzaBuilder(spicyPizzaBuilder);
        cook.constructPizza();
        Pizza spicy = cook.getPizza();
        System.out.println(spicy);
    }
}

/**
 * "Product"
 */
class Pizza {

    private String dough;
    private String sauce;
    private String topping;
    
    public void setDough(String dough) {
        this.dough = dough;
    }

    public void setSauce(String sauce) {
        this.sauce = sauce;
    }

    public void setTopping(String topping) {
        this.topping = topping;
    }

    @Override
    public String toString() {
        return "Pizza{" + "dough=" + dough + ", sauce=" + sauce + ", topping=" + topping + '}';
    }

}

/**
 * "Abstract Builder"
 */
abstract class PizzaBuilder {

    protected Pizza pizza;

    public Pizza getPizza() {
        return pizza;
    }

    public void createNewPizzaProduct() {
        pizza = new Pizza();
    }

    public abstract void buildDough();

    public abstract void buildSauce();

    public abstract void buildTopping();
}

/**
 * "ConcreteBuilder"
 */
class HawaiianPizzaBuilder extends PizzaBuilder {

    public void buildDough() {
        pizza.setDough("cross");
    }

    public void buildSauce() {
        pizza.setSauce("mild");
    }

    public void buildTopping() {
        pizza.setTopping("pineapple");
    }
}

/**
 * "ConcreteBuilder"
 */
class SpicyPizzaBuilder extends PizzaBuilder {

    public void buildDough() {
        pizza.setDough("pan baked");
    }

    public void buildSauce() {
        pizza.setSauce("hot");
    }

    public void buildTopping() {
        pizza.setTopping("salami");
    }
}

class Cook {

    private PizzaBuilder pizzaBuilder;

    public void setPizzaBuilder(PizzaBuilder pb) {
        pizzaBuilder = pb;
    }
    
    public Pizza getPizza() {
        return pizzaBuilder.getPizza();
    }
    
    public void constructPizza() {
        pizzaBuilder.createNewPizzaProduct();
        pizzaBuilder.buildDough();
        pizzaBuilder.buildSauce();
        pizzaBuilder.buildTopping();
    }
}


