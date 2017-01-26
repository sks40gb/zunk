
package com.sun.designpattern.behavior.command.a;

/**
 *
 * @author Sunil
 */
public class RemoteControl {

    private Command command;
    
    public void setCommand(Command command){
        this.command = command;
    }
    
    public void pressButton1(){
        command.execute();
    }
}
