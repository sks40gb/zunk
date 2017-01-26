package com.sun.spring.a.sample;


public class Car {
	Wheel wheel;

	public Wheel getWheel() {
		return wheel;
	}

	public void setWheel(Wheel wheel) {
		this.wheel = wheel;
	}
	public void move(){
		System.out.println("Moving.......");
		this.getWheel().rotate();
	}
}
