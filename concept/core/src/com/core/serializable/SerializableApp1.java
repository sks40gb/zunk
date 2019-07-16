package com.core.serializable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
 
/**
 *
 * @author Sunil
 */
public class SerializableApp1 {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        Customer customer = new Customer("Sunil");
        File file = new File("FileOne");
        Perstistent perstistent = new Perstistent();
        perstistent.writeObjectToFile(file, customer);

        Customer customerA = (Customer) perstistent.readObjectFromFile(file);
        System.out.println(customerA.getName());
    }

}

class Perstistent {

    public void writeObjectToFile(File file, Object o) throws FileNotFoundException, IOException {
        OutputStream os = new FileOutputStream(file);
        ObjectOutputStream outputStream = new ObjectOutputStream(os);
        outputStream.writeObject(o);
    }

    public Object readObjectFromFile(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        InputStream is = new FileInputStream(file);
        ObjectInputStream inputStream = new ObjectInputStream(is);
        return inputStream.readObject();
    }
}

class Customer implements Serializable {

    public Customer(String name) {
        this.name = name;
    }

    private String name;
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
