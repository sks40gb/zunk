
package com.sun.spring.f.test;

import java.lang.reflect.Method;
import org.springframework.beans.factory.support.MethodReplacer;

/**
 *
 * @author Sunil
 */
public class ColorMethodReplacer implements MethodReplacer{

    public Object reimplement(Object o, Method m, Object[] args){
        System.out.println("Green Color");
        return null;
    }
    
}
