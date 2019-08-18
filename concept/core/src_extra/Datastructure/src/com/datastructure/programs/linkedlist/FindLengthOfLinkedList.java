package com.datastructure.programs.linkedlist;

/**
 *
 * @author sunsingh
 */
public class FindLengthOfLinkedList {

    public static void main(String[] args) {
        LinkedList list = new LinkedList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        System.out.println("Length of linked list " + getLength(list.head));
    }

    public static int getLength(Node node) {
        if (node == null) {
            return 0;
        }
        return 1 + getLength(node.next);
    }
}
