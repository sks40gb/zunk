package com.util.RandomGUID;


public class RandomGUIDdemo {
    // Generate 20 of 'em!
    public static void main(String[] args) {
	for(int i=1; i<=20; i++) {
	    RandomGUID myguid = new RandomGUID(false);
	    System.out.println(i + " " + myguid.toString());
	}
    }
}

