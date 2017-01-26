package com.core.a.sample;

import com.core.datastructure.TreePrinter;

public class App {

    public static void main(String... args) throws Exception {
        BinaryTree bt = new BinaryTree();
        for(int i = 1; i < 10; i++){
            bt.insert(i+"");
        }
        bt.print();
    }

}

class Node implements TreePrinter.PrintableNode {

    public Node left;
    public Node right;
    public String text;

    public Node(String text) {
        this.text = text;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Node{" + "text=" + text + '}';
    }
    
    
}

class BinaryTree {

    public Node root;

    public void insert(String element) {
        System.out.println("-----");
       root = BinaryTree.this.insert(root, element);
    }
    public Node insert(Node parent, String element) {
        Node node = new Node(element);
        if (parent == null) {
            parent = node;
        } else if (parent.left == null) {
            parent.left = node;
        } else if (parent.right == null) {
            parent.right = node;
        }else{
            insert(parent.left,element);
        }
        System.out.println(parent);
        return parent;
    }

    public void print() {
        TreePrinter.print(root);
    }

}
