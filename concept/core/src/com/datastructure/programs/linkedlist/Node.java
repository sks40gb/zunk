package com.datastructure.programs.linkedlist;

/**
 *
 * @author sunsingh
 */
public class Node {

    public Node(int data) {
        this.data = data;
    }

    public int data;
    public Node next;

    @Override
    public String toString() {
        return "Node{" + "data=" + data + '}';
    }
    
    
}
