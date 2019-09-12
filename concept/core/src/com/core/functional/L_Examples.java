package com.core.functional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author sunsingh
 */
public class L_Examples {

    public static void main(String[] args) {
        List<Employee> list = new ArrayList<>();
        list.add(new Employee(1, "John", "Admin"));
        list.add(new Employee(2, "David", "Manager"));
        list.add(new Employee(3, "Kim", "Admin"));
        list.add(new Employee(4, "Drew", "Admin"));
        
        groupingBy(list);
        collectAsList(list);

    }

    public static void groupingBy(List<Employee> list) {
        Map<String, Set<Employee>> map = list.stream().collect(Collectors.groupingBy(Employee::getRole, Collectors.toSet()));
        System.out.println(map);
    }

    public static void collectAsList(List<Employee> list) {
        List<Employee> l = list.stream().filter(e -> e.getRole().equals("Admin")).collect(Collectors.toList());
        System.out.println(l);
    }

    static class Employee {

        private int id;
        private String name;
        private String role;

        public Employee(int id, String name, String role) {
            this.id = id;
            this.name = name;
            this.role = role;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }

        @Override
        public String toString() {
            return "Employee{" + "id=" + id + ", name=" + name + ", role=" + role + '}';
        }

    }

}
