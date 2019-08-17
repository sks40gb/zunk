package com.datastructure.programs.binarytree;

import java.util.ArrayDeque;

/**
 *
 * @author sunil
 */
public class TraversalWithoutRecursion {

    public static void main(String[] args) {

        BinarySearchTree<Integer> tree = new BinarySearchTree();
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
        //preOrder(tree);
        postOrder(tree);

    }

    public static void preOrder(BinarySearchTree<Integer> tree) {
        ArrayDeque<Node<Integer>> stack = new ArrayDeque<>();
        stack.push(tree.root);
        while (!stack.isEmpty()) {
            Node node = stack.pop();

            if (node.left != null) {
                stack.push(node.left);
            }
            System.out.println(node);
            if (node.right != null) {
                stack.push(node.right);
            }
        }
    }

    public static void postOrder(BinarySearchTree<Integer> tree) {
        ArrayDeque<Node<Integer>> stack = new ArrayDeque<>();
        stack.push(tree.root);

        while (!stack.isEmpty()) {
            Node current = stack.peek();

            if (current.isLeaf()) {
                Node node = stack.pop();
                System.out.printf("%s ", node.data);
            } else {

                if (current.right != null) {
                    stack.push(current.right);
                    current.right = null;
                }

                if (current.left != null) {
                    stack.push(current.left);
                    current.left = null;
                }
            }

        }
    }

}
