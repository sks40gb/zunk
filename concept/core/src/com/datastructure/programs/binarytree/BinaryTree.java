package com.datastructure.programs.binarytree;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author sunsingh
 */
public class BinaryTree<T extends Integer> {

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
        System.out.println("Inorder");
        tree.inorder();
        System.out.println("");
        System.out.println("PreOrder");
        tree.preOrder();
        System.out.println("");
        System.out.println("PostOrder");
        tree.postOrder();
    }

    private Node root;

    public void insert(T data) {
        this.root = insert(this.root, data);
    }

    public Node search(T key) {
        return search(this.root, key);
    }

    public Node<T> search(Node<T> node, T key) {
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

    public Node<T> insert(Node<T> node, T data) {
        if (node == null) {
            node = new Node<T>(data);
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

    public void inorder() {
        this.inorder(root);
    }

    public void inorder(Node<T> node) {
        if (node != null) {
            inorder(node.left);
            System.out.print(node.data + "=>");
            inorder(node.right);
        }
    }

    public void preOrder() {
        this.preOrder(root);
    }

    public void preOrder(Node<T> node) {
        if (node != null) {
            System.out.print(node.data + "=>");
            inorder(node.left);
            inorder(node.right);
        }
    }

    public void postOrder() {
        this.postOrder(root);
    }

    public void postOrder(Node<T> node) {
        if (node != null) {
            inorder(node.left);
            inorder(node.right);
            System.out.print(node.data + "=>");
        }
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
