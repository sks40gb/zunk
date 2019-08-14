package com.datastructure.programs.binarytree;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author sunsingh
 */
public class BinaryTree {

    public static void main(String[] args) {
        BinaryTree tree = new BinaryTree();
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);
        tree.insert(2);
        tree.insert(8);
        tree.insert(4);
        tree.insert(6);
        tree.insert(9);
        tree.insert(1);
        tree.insert(10);
        BinaryTreePrinter.printNode(tree.root);
        
        System.out.println("Finding key " + tree.search(6));
    }

    private Node root;

    public void insert(int data) {
        this.root = insert(this.root, data);
    }
    
    public Node search(int key){
        return search(this.root, key);
    }

    public Node search(Node<Integer> node, int key) {
        if (node == null) {
            return null;
        } else if (node.data == key) {
            return node;
        } else if (key > node.data) {
            return search(node.right, key);
        } else if (key < node.data) {
            return search(node.left, key);
        }
        return null;
    }

    public Node insert(Node<Integer> node, int data) {
        if (node == null) {
            node = new Node(data);
            return node;
        }
        if (data < node.data) {
            node.left = insert(node.left, data);
        } else if (data > node.data) {
            node.right = insert(node.right, data);
        } else {
            throw new RuntimeException("Value is already available");
        }
        return node;
    }

    public void printBFS() {
        Queue<Node> queue = new LinkedBlockingQueue<>();
        queue.offer(this.root);
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            System.out.println(node.data);
            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }
    }

}
