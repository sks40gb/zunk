package com.core.multithreading;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {

    private int id;
    private String name;

    public void print(String format) {
        System.out.println("Format : " + format + " Id : " + id + " name : " + name);
    }

    public static void main(String... rars) throws InterruptedException, ExecutionException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        Test t = new Test();
        t.id = 10;
        t.name = "Sunil";

        Arrays.stream(t.getClass().getDeclaredMethods()).forEach(f -> {
            System.out.println("Method " + f.getName() + " return type " + f.getReturnType());
        });

        Method method = t.getClass().getMethod("print", String.class);
        method.invoke(t, "---");

        Arrays.stream(t.getClass().getFields()).forEach((Field f) -> {
            try {
                System.out.println("Field " + f.getName() + " value : " + f.get(t));
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }
}
