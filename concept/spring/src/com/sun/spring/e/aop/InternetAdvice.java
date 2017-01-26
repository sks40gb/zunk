package com.sun.spring.e.aop;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;

public class InternetAdvice implements MethodBeforeAdvice, AfterReturningAdvice, ThrowsAdvice, MethodInterceptor {

    private CafeOwner cafeOwner;

    public void setCafeOwner(CafeOwner cafeOwner) {
        this.cafeOwner = cafeOwner;
    }

    public CafeOwner getCafeOwner() {
        return cafeOwner;
    }

    public void before(Method arg0, Object[] arg1, Object arg2)
            throws Throwable {
        System.out.println("method : " + arg0);
        System.out.println("arguement : " + arg1);
        System.out.println("Object : " + arg2);
        this.getCafeOwner().LogInTime();

    }

    public void afterReturning(Object arg0, Method arg1, Object[] arg2,
            Object arg3) throws Throwable {
        this.getCafeOwner().LogOutTime();

    }

    public void afterThrowing(Throwable throwable) {
        this.getCafeOwner().cancelBilling();
    }

    public Object invoke(MethodInvocation method) throws Throwable {
        System.out.println("Allocate a system to customer");
        method.proceed();
        System.out.println("Deallocate the system");
        return null;
    }
}
