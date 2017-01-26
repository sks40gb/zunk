package com.sun.spring.e.aop;

public class CafeOwner {

    public void LogInTime() {
        System.out.println("Log the In Time and Name of the Customer");
    }

    public void LogOutTime() {
        System.out.println("Log Out Time");
    }

    public void issueUsageBill() {
        System.out.println("Calculate and Issue Bill");
    }

    public void cancelBilling() {
        System.out.println("Cancel Billing");
    }
}
