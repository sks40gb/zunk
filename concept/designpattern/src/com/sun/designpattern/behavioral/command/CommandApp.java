package com.sun.designpattern.behavioral.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Command decouples the object that invokes the operation from the one that knows how to perform it. 
 *
 * @author Sunil
 */
public class CommandApp {

    public static void main(String[] args) throws IOException {
        Light lamp = new Light();
        Command switchUp = new FlipUpCommand(lamp);
        Command switchDown = new FlipDownCommand(lamp);

        // See criticism of this model above:
        // The switch itself should not be aware of lamp details (switchUp, switchDown)
        // either directly or indirectly
        Switch _switch = new Switch(switchUp, switchDown);
        System.out.println("Enter ON of OFF.");
        while (true) {
            try {
                String input = new BufferedReader(new InputStreamReader(System.in)).readLine();
                if (input.equalsIgnoreCase("ON")) {
                    _switch.flipUp();
                } else if (input.equalsIgnoreCase("OFF")) {
                    _switch.flipDown();
                } else {
                    System.out.println("\"ON\" or \"OFF\" is required.");
                }
            } catch (Exception e) {
                System.out.println("Arguments required.");
            }
        }
    }
}


/* The Receiver class */
class Light {

    public Light() {
    }

    public void turnOn() {
        System.out.println("The light is on");
    }

    public void turnOff() {
        System.out.println("The light is off");
    }
}

/* The Command interface */
interface Command {

    void execute();
}

/* The Command for turning the light off in North America, or turning the light on in most other places */
class FlipDownCommand implements Command {

    private Light theLight;

    public FlipDownCommand(Light light) {
        this.theLight = light;
    }

    public void execute() {
        theLight.turnOff();
    }
}

class FlipUpCommand implements Command {

    private Light theLight;

    public FlipUpCommand(Light light) {
        this.theLight = light;
    }

    public void execute() {
        theLight.turnOn();
    }
}

/* The Invoker class */
class Switch {

    private final Command flipUpCommand;
    private final Command flipDownCommand;

    public Switch(Command flipUpCmd, Command flipDownCmd) {
        this.flipUpCommand = flipUpCmd;
        this.flipDownCommand = flipDownCmd;
    }

    public void flipUp() {
        flipUpCommand.execute();
    }

    public void flipDown() {
        flipDownCommand.execute();
    }
}
