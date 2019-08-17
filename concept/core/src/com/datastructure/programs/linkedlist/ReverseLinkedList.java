package com.datastructure.programs.linkedlist;

/**
 *
 * @author sunsingh
 */
public class ReverseLinkedList {

    public static void main(String[] args) {

        LinkedList linkedList = new LinkedList();
        linkedList.add(1);
        linkedList.add(2);
        linkedList.add(3);
        linkedList.add(4);

        linkedList.print(); //     1->2->3->4
        //reverseIterative(linkedList);
       linkedList.head = reverseRecursively(linkedList.head);
        System.out.println("After reverse");
        linkedList.print();
    }

    public static void reverseIterative(LinkedList linkedList) {
        Node pre = null;
        Node current = linkedList.head;
        Node next = null;
        while (current != null) {
            next = current.next;
            current.next = pre;
            pre = current;
            current = next;
        }
        linkedList.head = pre;

    }

    //[1]->[2]->[3]->[4]      
    public static Node reverseRecursively(Node node) {  //1 ->2 ->3 ->4
         //base case - tail of original linked list 
        if ((node.next == null)) {
            return node;
        }
        Node newHead = reverseRecursively(node.next);
        //reverse the link e.g. C->D->null will be null 
        node.next.next = node;                        
        node.next = null;
        return newHead;
    }

}
