package com.sun.designpattern.structural.decorator.a;

/**
 * In object-oriented programming, the decorator pattern is a design pattern that allows new/additional behavior to
 * be added to an existing object dynamically.
 *
 * steps---
 * 1. Subclass the original "Component" class into a "Decorator" class (see UML diagram);
 * 2. In the Decorator class, add a Component pointer as a field;
 * 3. Pass a Component to the Decorator constructor to initialize the Component pointer;
 * 4. In the Decorator class, redirect all "Component" methods to the "Component" pointer; and
 * 5. In the ConcreteDecorator class, override any Component method(s) whose behavior needs to be modified.
 *
 * Motivation --
 * As an example, consider a window in a windowing system. To allow scrolling of the window's contents, we may wish to
 * add horizontal or vertical scrollbars to it, as appropriate. Assume windows are represented by instances of the
 * Window class, and assume this class has no functionality for adding scrollbars. We could create a subclass
 * ScrollingWindow that provides them, or we could create a ScrollingWindowDecorator that adds this functionality
 * to existing Window objects. At this point, either solution would be fine.
 * Now let's assume we also desire the ability to add borders to our windows. Again, our original Window class has
 * no support. The ScrollingWindow subclass now poses a problem, because it has effectively created a new kind of
 * window. If we wish to add border support to all windows, we must create subclasses WindowWithBorder and
 * ScrollingWindowWithBorder. Obviously, this problem gets worse with every new feature to be added. For the
 * decorator solution, we simply create a new BorderedWindowDecorator—at runtime, we can decorate existing
 * windows with the ScrollingWindowDecorator or the BorderedWindowDecorator or both, as we see fit.
 * 
 * @author sunil
 */
public class TestMain {

    public static void main(String[] args) {
        Coffee sampleCoffee = new SimpleCoffee();
        System.out.println("Cost: " + sampleCoffee.getCost() + " Ingredient: " + sampleCoffee.getIngredient());

        sampleCoffee = new Milk(sampleCoffee);
        System.out.println("Cost: " + sampleCoffee.getCost() + " Ingredient: " + sampleCoffee.getIngredient());

        sampleCoffee = new Whip(sampleCoffee);
        System.out.println("Cost: " + sampleCoffee.getCost() + " Ingredient: " + sampleCoffee.getIngredient());
    }
}
