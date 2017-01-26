package com.core.reflaction;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author sunil
 */
public class Reflaction_2 {

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        PublicEmployee emp = new PublicEmployee("sun", 32, true);
        Class c = emp.getClass();

        System.out.println("********** FIELDS ******************");
        //accessing the public fields of class and get the values of that.
        Field fields[] = c.getFields();
        for (Field f : fields) {
            System.out.println("---------------------> " + f.get(emp));
        }

        // get the public methods of the class and get the values return by the 
        // class. Note this can be used when there is private field whose
        // value can't be accessed directly. We use the getter for that variable
        // and get the value of varible by invoking the getter method.
        System.out.println("********** METHODS ******************");
        PrivateEmployee _emp = new PrivateEmployee("sunil", 32, true);
        Class _c = _emp.getClass();
        Method methods[] = _c.getDeclaredMethods();
        for (Method m : methods) {
            // if it is not private or protect
            if (m.getModifiers() == Method.DECLARED) {
                System.out.println(m.getName() + "    ========     " + m.invoke(_emp));
            }
        }

    }
}

class PublicEmployee {

    public PublicEmployee(String name, int roll, boolean isMale) {
        this.name = name;
        this.roll = roll;
        this.isMale = isMale;
    }
    public String name;
    public int roll;
    public boolean isMale;
}

class PrivateEmployee {

    public PrivateEmployee(String name, int roll, boolean isMale) {
        this.name = name;
        this.roll = roll;
        this.isMale = isMale;
    }
    private String name;
    private int roll;
    private boolean isMale;

    public boolean isIsMale() {
        return isMale;
    }

    public String getName() {
        return name;
    }

    public int getRoll() {
        return roll;
    }

    public void print() {
        // System.out.print("PUBLIC METHOD");
    }

    private void printMe() {
        System.out.println("PRIVATE METHOD");
    }
}
