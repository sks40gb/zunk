package com.sun.spring.c.autowiring;

public class Student {

    private Book javaBook;
    private String studentName;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Book getJavaBook() {
        return javaBook;
    }

    public void setJavaBook(Book javaBook) {
        this.javaBook = javaBook;
    }
}
