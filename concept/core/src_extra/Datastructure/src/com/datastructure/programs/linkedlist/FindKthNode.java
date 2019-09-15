package com.datastructure.programs.linkedlist;

/**
 *
 * @author sunsingh
 */
public class FindKthNode {

    public static void main(String[] args) {
        LinkedList list = new LinkedList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        System.out.println(getNode(list.head, 2));
    }

    private static Node getNode(Node head, int k) {
        Node slow = head;
        Node fast = head;
        for (int i = 0; i < k - 1; i++) {
            fast = fast.next;
        }
        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }
        return slow;
    }
}
