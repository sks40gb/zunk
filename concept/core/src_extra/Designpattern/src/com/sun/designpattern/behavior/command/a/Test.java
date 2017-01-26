
package com.sun.designpattern.behavior.command.a;

/**
 *
 * @author Sunil
 */
public class Test {

    public static void main(String[] args) {
        Light light = new Light();
        Command command = new LightOnCommand(light);
        RemoteControl control = new RemoteControl();
        control.setCommand(command);
        control.pressButton1();        
    }
    
}

