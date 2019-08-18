package com.datastructure.programs.linkedlist;

/**
 *
 * @author sunil
 */
public class FindAndRemoveLoop {

    public static void main(String[] args) {
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        Node node3 = new Node(3);
        Node node4 = new Node(4);
        Node node5 = new Node(5);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        node5.next = node3; //loop here
        
        System.out.println("Is looped " + isLoop(node1));
        System.out.println("Starting Point of Loop " + findLoopStartingPoint(node1));
    }

    public static boolean isLoop(Node node) {
        Node slow = node;
        Node fast = node;
        while(fast!= null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;
            if(slow == fast){
                return true;
            }
        }
        return false;
    }
    
    public static Node findLoopStartingPoint(Node head){
        Node fast = head;
        Node slow = head;
        while(fast != null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;
            if(slow == fast){
                break;
            }
        }
        
        slow = head;
        while(slow != fast){
            slow = slow.next;
            fast = fast.next;
        }
        return slow;
    }

}
