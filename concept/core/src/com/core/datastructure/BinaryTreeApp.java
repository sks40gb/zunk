package com.core.datastructure;

/**
 *
 * @author Sunil 
 * Java Program to Implement Binary Tree
 */
import com.core.datastructure.TreePrinter.PrintableNode;
import java.util.Scanner;

/* Class BTNode */
class BTNode implements PrintableNode{

    BTNode left, right;
    int data;

    /* Constructor */
    public BTNode() {
        left = null;
        right = null;
        data = 0;
    }

    /* Constructor */
    public BTNode(int n) {
        this();
        data = n;
    }

    public void setLeft(BTNode n) {
        left = n;
    }

    public void setRight(BTNode n) {
        right = n;
    }

    public BTNode getLeft() {
        return left;
    }

    public BTNode getRight() {
        return right;
    }

    public void setData(int d) {
        data = d;
    }

    public int getData() {
        return data;
    }

    @Override
    public String getText() {
        return new Integer(data).toString();
    }

}

class BinaryTree {

    private BTNode root;

    public BinaryTree() {
        root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void insert(int data) {
        root = insert(root, data);
    }

    private BTNode insert(BTNode node, int data) {
        if (node == null) {
            node = new BTNode(data);
        } else if (node.right == null) {
            node.right = insert(node.right, data);
        } else {
            node.left = insert(node.left, data);
        }
        return node;
    }

    public int countNodes() {
        return countNodes(root);
    }

    private int countNodes(BTNode r) {
        if (r == null) {
            return 0;
        } else {
            int count = 1;
            count += countNodes(r.getLeft());
            count += countNodes(r.getRight());
            return count;
        }
    }

    public boolean search(int val) {
        return search(root, val);
    }

    private boolean search(BTNode r, int val) {
        if (r.getData() == val) {
            return true;
        }

        if (r.getLeft() != null) {
            if (search(r.getLeft(), val)) {
                return true;
            }
        }

        if (r.getRight() != null) {
            if (search(r.getRight(), val)) {
                return true;
            }
        }
        return false;

    }

    public void inorder() {
        inorder(root);
    }

    private void inorder(BTNode r) {
        if (r != null) {
            inorder(r.getLeft());
            System.out.print(r.getData() + " ");
            inorder(r.getRight());
        }
    }

    public void preorder() {
        preorder(root);
    }

    private void preorder(BTNode r) {
        if (r != null) {
            System.out.print(r.getData() + " ");
            preorder(r.getLeft());
            preorder(r.getRight());
        }

    }

    public void postorder() {
        postorder(root);
    }

    private void postorder(BTNode r) {
        if(r != null){
            postorder(r.getLeft());
            postorder(r.getRight());
            System.out.println(r.getData() + " ");
        }
    }

    public BTNode getRoot() {
        return root;
    }
}

public class BinaryTreeApp {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        BinaryTree bt = new BinaryTree();
        bt.insert(12);
        bt.insert(33);
        bt.insert(202);
        bt.insert(15);
        bt.insert(1);
        bt.insert(9);
        bt.insert(5);
        TreePrinter.print(bt.getRoot());
        bt.postorder();


    }

}
