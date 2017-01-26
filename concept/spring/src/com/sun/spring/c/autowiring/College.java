package com.sun.spring.c.autowiring;

public class College {

    private Student collegeStudent;

    public Student getCollegeStudent() {
        return collegeStudent;
    }

    public College(Student student) {
        this.collegeStudent = student;
    }
}
