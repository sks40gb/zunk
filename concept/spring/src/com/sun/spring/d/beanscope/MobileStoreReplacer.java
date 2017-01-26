package com.sun.spring.d.beanscope;

import java.lang.reflect.Method;

import org.springframework.beans.factory.support.MethodReplacer;

public class MobileStoreReplacer implements MethodReplacer {

    public Object reimplement(Object arg0, Method arg1, Object[] arg2)
            throws Throwable {
        return "Bought an iPhone";

    }
}
