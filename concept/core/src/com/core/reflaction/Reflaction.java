package com.core.reflaction;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author sunil
 */
public class Reflaction {

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        User user = new User(111, "0k", true);
        Class userClass = user.getClass();
        Field[] fields = userClass.getFields();

        for (Field field : fields) {
            Class c1 = field.getType();
            if (c1.getName().equals("int")) {
                // System.out.println("------------------> " + field.get(user));
                field.setInt(user, 2222);
            }

            if (c1 == String.class || c1.getName().equals("int") || c1.getName().equals("boolean")) {
                System.out.println("------------------> " + field.get(user));
                //field.setInt(user,2222);
            }
        }

        Method[] methods = userClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterTypes().length == 0) {
                System.out.println("method-->" + method.getName());
                method.invoke(user);
            }
        }
    }
}

class User {

    public int id;
    public String name;
    public boolean isMale;

    public User(int id, String name, boolean isMale) {
        this.id = id;
        this.name = name;
        this.isMale = isMale;
    }

    public int getId() {
        System.out.println("============ callme  ");
        return id;
    }
}
