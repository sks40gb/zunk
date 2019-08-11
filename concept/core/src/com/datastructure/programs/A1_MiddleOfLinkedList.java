package com.datastructure.programs;

/**
 *
 * @author sunsingh
 */
public class A1_MiddleOfLinkedList extends LinkedList {

    public static void main(String[] args) {
        A1_MiddleOfLinkedList list = new A1_MiddleOfLinkedList();
        for (int i = 0; i < 15; i++) {
            list.add(i);
        }

        list.print();

        System.out.println("Middle " + list.findMiddle().data);
    }

    /**
     * Find middle of Linked List
     * @return 
     */
    public Node findMiddle() {
        Node slower = this.head;
        Node faster = this.head;
        while (faster != null & faster.next != null && faster.next.next != null) {
            slower = slower.next;
            faster = faster.next.next;
        }
        return slower;
    }

}
