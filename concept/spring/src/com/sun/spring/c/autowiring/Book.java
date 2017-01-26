package com.sun.spring.c.autowiring;


public class Book {
		private String title;
	public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
	public String bookTitle(){
		return this.getTitle();
	}
}
