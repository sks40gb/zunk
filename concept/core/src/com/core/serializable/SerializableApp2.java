package com.core.serializable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *  1. Non Serialize parent constructor will always be executed
 *  2. Serialized class's constructor will never be executed while deserializing.
 *  3. private void writeObject(ObjectOutputStream os) and private void readObject(ObjectInputStream is) is for manual
 *     serializable
 *  4. os.defaultWriteObject() and is.defaultReadObject() are for default Serializable. If this methods are omitted every
 *     instance value had be maintained manually.
 *
 * @author Sunil
 */
public class SerializableApp2 {

    public static void main(String[] args) {

        write();
        read();
    }

    public static void write() {
        System.out.println("WRITING--------------");
        try {
            FileOutputStream os = new FileOutputStream("objectState.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
            Dog dog = new Dog(30, "Suffie");
            objectOutputStream.writeObject(dog);
            dog.print();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void read() {
        System.out.println("READING--------------");
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("objectState.txt"));
            Dog dog = (Dog) objectInputStream.readObject();
            dog.print();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace(); // exception from  ois.readObject();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(); // exception from FileInputStream
        } catch (IOException ex) {
            ex.printStackTrace(); // exception from ObjectInputStream
        }

    }
}

class Animal implements Serializable{

    protected int age = 5;

    public Animal() {  // Construtor will always execute since it not Serializable
        System.out.println("Aminal constuctor");   
        age = 10;
    }
}

class Dog extends Animal implements Serializable {

    private String name = "DefaulName";
    private transient String color = "red";

    Dog(int age, String name) {        // Contructor will not execute ( while Serializing) since it Serializable
        System.out.println("Dog constructor");
        this.age = age;
        this.name = name;
        this.color = "blue";
    }

    public void print() {
        System.out.println("Name : " + name + " Age : " + age + " color :" +  color);
    }

    /**
     * Manual Serialized
     * @param os
     */
    private void writeObject(ObjectOutputStream os){
        try {
            System.out.println("writeObject Method is called.");
            os.defaultWriteObject();  // default serialization should be performed
            os.writeObject(color);
            os.writeObject(name);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Manual Deserialized
     * @param is
     */
    private void readObject(ObjectInputStream is){
        try {
            System.out.println("readObject Method is called.");
            is.defaultReadObject();   //default deserilizable should be performed.
            color = String.valueOf(is.readObject());  //java.io.OptionalDataException if "color" is not saved.
            name = String.valueOf(is.readObject());
          
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
