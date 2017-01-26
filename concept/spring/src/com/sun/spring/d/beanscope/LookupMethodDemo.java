package com.sun.spring.d.beanscope;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LookupMethodDemo {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/sun/spring/d/beanscope/Method-Injection.xml");

        //Look up Method
        BookStore book = (BookStore) context.getBean("book");

        System.out.println(book.orderBook().bookTitle());
    }
}
