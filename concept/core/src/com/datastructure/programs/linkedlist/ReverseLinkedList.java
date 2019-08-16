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
        reverseIterative(linkedList);
        System.out.println("After reverse");
        linkedList.print();   
    }
    public static void reverseIterative(LinkedList linkedList){
        Node pre = null;
        Node current = linkedList.head;
        Node next = null;
        while(current != null){
            next = current.next;
            current.next = pre;
            pre = current;
            current = next;
        }
        linkedList.head = pre;
       
    }

}


