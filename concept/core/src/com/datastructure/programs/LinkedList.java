package com.datastructure.programs;

/**
 *
 * @author sunsingh
 */
public class LinkedList {

    protected Node head;

    public void print() {
        Node current = this.head;
        while (current != null) {
            System.out.print("->[" + current.data + "]");
            current = current.next;
        }
        System.out.println("");
    }

    public void add(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            this.head = newNode;
        } else {
            Node last = this.head;
            while (last.next != null) {
                last = last.next;
            }
            last.next = newNode;
        }
    }

}
