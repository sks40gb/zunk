package com.datastructure.programs.linkedlist;

/**
 *
 * @author sunsingh
 */
public class AddTwoNumbers {

    public static void main(String[] args) {
        LinkedList first = new LinkedList();
        first.add(5);
        first.add(6);
        first.add(3);

        LinkedList second = new LinkedList();
        second.add(8);
        second.add(4);
        second.add(2);
        
        addNumbers(first.head, second.head);
    }

    public static void addNumbers(Node first, Node second) {
        LinkedList result = new LinkedList();
        int diff = LengthDiff(length(first), length(second));
        Node a = first;
        Node b = second;
        if (diff < 0) {
            a = second;
            b = first;
        }
        //Move the node at the same length;
        if (diff != 0) {
            for (int i = 0; i < Math.abs(diff); i++) {
                first = first.next;
            }
        }
        result.head = sum(first, second, 0);
        result.print();
    }

    public static Node sum(Node first, Node second, int carryOver) {
        if (first == null) {
            return null;
        }
        int sum = first.data + second.data;
        int carry = 0;
        if (sum > 10) {
            sum = sum % 10;
            carryOver = 1;
        }
        Node result = new Node(sum);
        result.next = sum(first.next, second.next, carryOver);
        return result;
    }

    public static int LengthDiff(int a, int b) {
        return a - b;
    }

    public static int length(Node node) {
        int length = 0;
        while (node != null) {
            length++;
            node = node.next;
        }
        return length;
    }
}
