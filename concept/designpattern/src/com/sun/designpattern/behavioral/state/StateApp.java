package com.sun.designpattern.behavioral.state;

/**
 * In State pattern a class behavior changes based on its state. This type of design pattern comes under behavior
 * pattern.
 *
 * In State pattern, we create objects which represent various states and a context object whose behavior varies as its
 * state object changes.
 *
 * difference between state and strategy pattern is stateful vs stateless. State pattern is always stateful but Strategy
 * will not. States store a reference to the context object that contains them. Strategies do not.
 *
 *
 * The State pattern deals with what (state or type) an object is (in) -- it encapsulates state-dependent behavior,
 * whereas the Strategy pattern deals with how an object performs a certain task -- it encapsulates an algorithm.
 *
 * 
 * The State pattern deals with what (state or type) an object is (in) -- it encapsulates state-dependent behavior, whereas
 * the Strategy pattern deals with how an object performs a certain task -- it encapsulates an algorithm.
 * 
 * @author Sunil
 */
public class StateApp {

    public static void main(String[] args) {
        TVContext context = new TVContext();
        State tvStartState = new TVStartState();
        State tvStopState = new TVStopState();

        context.setState(tvStartState);
        context.doAction();

        context.setState(tvStopState);
        context.doAction();

    }
}

interface State {

    public void doAction();
}

class TVContext implements State {

    private State tvState;

    public void setState(State state) {
        this.tvState = state;
    }

    @Override
    public void doAction() {
        this.tvState.doAction();
    }
}

class TVStopState implements State {

    @Override
    public void doAction() {
        System.out.println("TV is turned OFF");
    }
}

class TVStartState implements State {

    @Override
    public void doAction() {
        System.out.println("TV is turned ON");
    }
}
