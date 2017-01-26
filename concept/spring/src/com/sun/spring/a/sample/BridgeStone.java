package com.sun.spring.a.sample;


public class BridgeStone implements Wheel{
	private String name;
	
	public void rotate() {
		System.out.println("ROTATES on "+this.getName());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
