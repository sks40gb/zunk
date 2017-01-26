package com.sun.spring.b.beanwiring;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring Basic Bean wiring
 * @author sunil
 */
public class BeanWiringDemo {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/sun/spring/b/beanwiring/BasicBeanWiring.xml");

        College college = (College) context.getBean("college");

        System.out.println("College Name:" + college.getCollegeName() + " Established Year" + college.getEstablishedYear());

        List<Student> students = college.getStudents();
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            System.out.println("Student Name :" + student.getName() + " Age: " + student.getAge() + " Department :" + student.getDepartment());

        }

        Iterator<Books> booksIterator = college.getBooks().iterator();
        while (booksIterator.hasNext()) {
            Books book = booksIterator.next();
            System.out.println("Book Title" + book.getBookTitle() + "  Author:" + book.getAuthor());
        }

        Map<String, Student> studentContacts = college.getStudentContacts();

        Iterator<String> contactIterator = studentContacts.keySet().iterator();
        while (contactIterator.hasNext()) {
            String contact = contactIterator.next();
            Student student = studentContacts.get(contact);
            System.out.println("Contact :" + contact + "  Student name:" + student.getName());
        }

        Properties faculties = college.getFaculties();

        System.out.println("Faculty-Richard" + faculties.getProperty("Robert"));
        System.out.println("Faculty-Casper" + faculties.getProperty("Casper"));

    }
}
