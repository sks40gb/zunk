package com.core.functional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Lambda Expressions Multithreading & Collections
 *
 *
 * @author sunsingh
 */
public class B_LambdaExpressionsMultithreadingAndCollections {

    public static void main(String[] args) {
        // multhreadingExample();
        collectionExample();
    }

    private static void multhreadingExample() {
        Runnable r = () -> System.out.println("HELLO PRINT FROM RUNNABLE CLASS");

        Thread t = new Thread(r);
        t.start();
    }

    private static void collectionExample() {
        List<Employee> employeeList = new ArrayList();
        employeeList.add(new Employee(1, "Ashok", 100d));
        employeeList.add(new Employee(2, "CK", 300d));
        employeeList.add(new Employee(3, "Bill", 400d));

        //Sort by name
        Comparator<Employee> compareByName = (a, b) -> a.getName().compareTo(b.getName());
        Collections.sort(employeeList, compareByName);
        System.out.println("EmployeeList after sorting by name " + employeeList);

    }

}

class Employee {

    private int id;
    private String name;
    private double salary;

    public Employee(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "\nEmployee{" + "id=" + id + ", name=" + name + ", salary=" + salary + '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }

}
