package com.datastructure.programs.linkedlist;

import com.datastructure.programs.LinkedList;
import com.datastructure.programs.Node;

/**
 *
 * @author sunsingh
 */
public class FindMiddleNode extends LinkedList {

    public static void main(String[] args) {
        FindMiddleNode list = new FindMiddleNode();
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
