package com.datastructure.programs.binarytree;

/**
 *
 * @author sunsingh
 */
public class Node<T> {

    public Node(T value) {
        this.data = value;
    }
    T data;
    Node left;
    Node right;

    @Override
    public String toString() {
        return "Node{" + "data=" + data + '}';
    }
    
    public boolean isLeaf(){
        return left == null && right == null;
    }

}
