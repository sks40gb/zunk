
package com.sun.designpattern.behavior.command.a;

/**
 *
 * @author Sunil
 */
public class LightOnCommand implements Command{

    Light light;
    public LightOnCommand(Light light) {
        this.light = light;
    }
    
    public void execute(){
        light.on();
    }

}
