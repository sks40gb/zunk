package com.sun.spring.d.beanscope;


public class Student {
		private static int count;
	public Student(){
		count++;
	}
	
	public int getCount(){
		return count;
	}
}
