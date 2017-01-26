package com.sun.spring.d.beanscope;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ReplaceMethodDemo {
	public static void main(String[] args) {
	ApplicationContext context = new ClassPathXmlApplicationContext("com/sun/spring/d/beanscope/Method-Injection.xml");
	
	//Method Replacer
	MobileStore mobileStore = (MobileStore)context.getBean("mobileStore");
	System.out.println(mobileStore.buyMobile());
	
	}
}
