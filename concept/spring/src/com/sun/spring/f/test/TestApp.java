
package com.sun.spring.f.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Sunil
 */
public class TestApp {
    
    public static void main(String[] args) {
        ApplicationContext context =  new ClassPathXmlApplicationContext("/com/sun/spring/f/test/animal.xml");
        Animal animal = (Animal) context.getBean("dog");
        animal.voice();
        animal.color();
    }

}
