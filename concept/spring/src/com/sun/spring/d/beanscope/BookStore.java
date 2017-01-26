package com.sun.spring.d.beanscope;

public class BookStore {

    //public  Book orderBook();
    public Book orderBook() {
        Book storyBook = new StoryBook();
        return storyBook;
    }
}
