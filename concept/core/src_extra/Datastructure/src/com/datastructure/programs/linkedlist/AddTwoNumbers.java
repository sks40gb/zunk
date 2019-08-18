package com.datastructure.programs.linkedlist;

/**
 *  @TODO - yet to be implemented completely.
 * @author sunsingh
 */
public class AddTwoNumbers {

    public static void main(String[] args) {
        LinkedList first = new LinkedList();
        first.add(6);
        first.add(8);
        first.add(9);
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
        Node current = a;
        //Move the node at the same length;
        if (diff != 0) {
            for (int i = 0; i < Math.abs(diff); i++) {
                first = first.next;
            }
        }
        result.head = sum(first, second);
        
        //If first element has extra items in it.
        while(current != first){
            int sum = current.data + carry;
            carry = sum/10;
            sum = sum%10;
            Node node = new Node(sum);
            node.next = result.head;
            result.head = node;
            current = current.next;
        }
        
        if (carry > 0) {
            Node node = new Node(carry);
            node.next = result.head;
            result.head = node;
        }
        result.print();
    }

    static int carry = 0;
    
    public static Node sum(Node first, Node second) {
        if (first == null) {
            return null;
        }
        Node result = new Node(0);
        result.next = sum(first.next, second.next);
        int sum = first.data + second.data + carry;
        carry = sum / 10;
        sum = sum % 10;
        result.data = sum;
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
