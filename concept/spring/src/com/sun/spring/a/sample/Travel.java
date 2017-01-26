package com.sun.spring.a.sample;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring Basic Bean wiring
 * @author sunil
 */
public class Travel {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("com/sun/spring/a/sample/spring.xml");
        Car car = (Car) ctx.getBean("car");
        car.move();
    }
}
