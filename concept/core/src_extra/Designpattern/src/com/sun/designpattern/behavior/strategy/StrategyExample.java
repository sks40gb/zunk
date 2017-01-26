package com.sun.designpattern.behavior.strategy;

/**
 * For instance, a class that performs validation on incoming data may use a strategy pattern to select a validation
 * algorithm based on the type of data, the source of the data, user choice, or other discriminating factors. These
 * factors are not known for each case until run-time, and may require radically different validation to be performed
 *
 * @author Sunil
 */
/**
 * The classes that implement a concrete strategy should implement this. The Context class uses this to call the
 * concrete strategy.
 */
interface Strategy {

    int execute(int a, int b);
}

/**
 * Implements the algorithm using the strategy interface
 */
class Add implements Strategy {

    public int execute(int a, int b) {
        System.out.println("Called Add's execute()");
        return a + b;  // Do an addition with a and b
    }
}

class Subtract implements Strategy {

    public int execute(int a, int b) {
        System.out.println("Called Subtract's execute()");
        return a - b;  // Do a subtraction with a and b
    }
}

class Multiply implements Strategy {

    public int execute(int a, int b) {
        System.out.println("Called Multiply's execute()");
        return a * b;   // Do a multiplication with a and b
    }
}

// Configured with a ConcreteStrategy object and maintains
// a reference to a Strategy object 
class Context {

    private Strategy strategy;

    public Context(Strategy strategy) {
        this.strategy = strategy;
    }

    public int executeStrategy(int a, int b) {
        return this.strategy.execute(a, b);
    }
}

/**
 * Tests the pattern
 */
class StrategyExample {

    public static void main(String[] args) {
        Context context;
        // Three contexts following different strategies
        context = new Context(new Add());
        int resultA = context.executeStrategy(3, 4);

        context = new Context(new Subtract());
        int resultB = context.executeStrategy(3, 4);

        context = new Context(new Multiply());
        int resultC = context.executeStrategy(3, 4);

        System.out.println("Result A : " + resultA);
        System.out.println("Result B : " + resultB);
        System.out.println("Result C : " + resultC);
    }
}
