package com.core.datastructure;

public class StackByLinkedList {

    public static void main(String... args) throws Exception {
        Stack stack = new Stack();
        stack.push("11");
        stack.push("22");
        stack.push("33");

        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());

    }

    static class Stack {

        private Node head;
        private int size;

        class Node {

            public Node(String value) {
                this.value = value;
            }
            public String value;
            public Node next;
        }

        public void push(String value) {
            Node node = new Node(value);
            if (head != null) {
                node.next = head;
            }
            head = node;
            size++;
        }

        public String pop() {
            if (isEmpty()) {
                return null;
            } else {
                String value = head.value;
                if (size == 1) {
                    head = null;
                } else {
                    head = head.next;
                }
                size--;
                return value;
            }

        }

        public boolean isEmpty() {
            return size == 0;
        }

    }
}
