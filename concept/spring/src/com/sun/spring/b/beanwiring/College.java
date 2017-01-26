package com.sun.spring.b.beanwiring;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class College {

    private String collegeName;
    private String establishedYear;
    private List<Student> students;
    private Map<String, Student> studentContacts;
    private Set<Books> books;
    private Properties faculties;

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public College(String collName) {
        this.collegeName = collName;
    }

    public String getEstablishedYear() {
        return establishedYear;
    }

    public void setEstablishedYear(String establishedYear) {
        this.establishedYear = establishedYear;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Map<String, Student> getStudentContacts() {
        return studentContacts;
    }

    public void setStudentContacts(Map<String, Student> studentContacts) {
        this.studentContacts = studentContacts;
    }

    public Set<Books> getBooks() {
        return books;
    }

    public void setBooks(Set<Books> books) {
        this.books = books;
    }

    public Properties getFaculties() {
        return faculties;
    }

    public void setFaculties(Properties faculties) {
        this.faculties = faculties;
    }

    public void initMethod() {
        System.out.println("INIT METHOD");
    }
}
