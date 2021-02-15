package com.sun.designpattern.behavioral.chainofresponsibility;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Sunil
 * In Chain of Responsibility pattern, receivers are chained and pass any request messages from object to object 
 * until it reaches an object capable of handling the message.
 * 
 * Java Exception handling class hierarchy is an example of chain of responsibility.
 */


public class ChainOfResponsibilityApp {

    public static void main(String[] args) {
        ManagerPower manager = new ManagerPower();
        DirectorPower director = new DirectorPower();
        PresidentPower president = new PresidentPower();
        manager.setSuccessor(director);
        director.setSuccessor(president);

        //enter ctrl+c to kill.
        try {
            while (true) {
                System.out.println("Enter the amount to check who should approve your expenditure.");
                System.out.print(">");
                double d = Double.parseDouble(
                    new BufferedReader(new InputStreamReader(System.in)).readLine());
                manager.processRequest(new PurchaseRequest(0, d, "General"));
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }
}

abstract class PurchasePower {

    protected final double base = 10100;
    protected PurchasePower successor;

    public void setSuccessor(PurchasePower successor) {
        this.successor = successor;
    }

    abstract public void processRequest(PurchaseRequest request);
}

class ManagerPower extends PurchasePower {

    private final double ALLOWABLE = 10 * base;

    public void processRequest(PurchaseRequest request) {
        if (request.getAmount() < ALLOWABLE) {
            System.out.println("Manager will approve $" + request.getAmount());
        } else if (successor != null) {
            successor.processRequest(request);
        }
    }
}

class DirectorPower extends PurchasePower {

    private final double ALLOWABLE = 20 * base;

    public void processRequest(PurchaseRequest request) {
        if (request.getAmount() < ALLOWABLE) {
            System.out.println("Director will approve $" + request.getAmount());
        } else if (successor != null) {
            successor.processRequest(request);
        }
    }
}

class PresidentPower extends PurchasePower {

    private final double ALLOWABLE = 30 * base;

    public void processRequest(PurchaseRequest request) {
        if (request.getAmount() < ALLOWABLE) {
            System.out.println("President will approve $" + request.getAmount());
        } else {
            System.out.println("Your request for $" + request.getAmount() + " needs a board meeting!");
        }
    }
}

class PurchaseRequest {

    private int number;
    private double amount;
    private String purpose;

    public PurchaseRequest(int number, double amount, String purpose) {
        this.number = number;
        this.amount = amount;
        this.purpose = purpose;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amt) {
        amount = amt;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String reason) {
        purpose = reason;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int num) {
        number = num;
    }
}
