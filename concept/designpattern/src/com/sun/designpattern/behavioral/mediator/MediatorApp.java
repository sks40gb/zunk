package com.sun.designpattern.behavioral.mediator;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows loose coupling by encapsulating the way disparate sets of objects interact and communicate with each other.
 * The essence of the Mediator Pattern is to "define an object that encapsulates how a set of objects interact"
 * 
 * The Mediator pattern promotes a "many-to-many relationship network"
 * 
 * Air traffic controller is a great example of mediator pattern where the airport control room works as a 
 * mediator for communication between different flights. 
 * 
 * @author sunsingh
 */
public class MediatorApp {

    public static void main(String[] args) {
        ChatMediator mediator = new ChatMediatorImpl();
        User user1 = new UserImpl(mediator, "Pankaj");
        User user2 = new UserImpl(mediator, "Lisa");
        User user3 = new UserImpl(mediator, "Saurabh");
        User user4 = new UserImpl(mediator, "David");
        mediator.addUser(user1);
        mediator.addUser(user2);
        mediator.addUser(user3);
        mediator.addUser(user4);

        user1.send("Hi All");

    }
}

interface ChatMediator {

    public void sendMessage(String msg, User user);

    public void addUser(User user);
}

class ChatMediatorImpl implements ChatMediator {

    private List<User> users;

    public ChatMediatorImpl() {
        this.users = new ArrayList<>();
    }

    @Override
    public void addUser(User user) {
        this.users.add(user);
    }

    @Override
    public void sendMessage(String msg, User user) {
        for (User u : this.users) {
            //message should not be received by the user sending it
            if (u != user) {
                u.receive(msg);
            }
        }
    }

}


abstract class User {

    protected ChatMediator mediator;
    protected String name;

    public User(ChatMediator med, String name) {
        this.mediator = med;
        this.name = name;
    }

    public abstract void send(String msg);

    public abstract void receive(String msg);
}


class UserImpl extends User {

    public UserImpl(ChatMediator med, String name) {
        super(med, name);
    }

    @Override
    public void send(String msg) {
        System.out.println(this.name + ": Sending Message=" + msg);
        mediator.sendMessage(msg, this);
    }

    @Override
    public void receive(String msg) {
        System.out.println(this.name + ": Received Message:" + msg);
    }

}
