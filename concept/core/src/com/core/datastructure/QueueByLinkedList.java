package com.core.datastructure;

public class QueueByLinkedList {

    public static void main(String... args) throws Exception {
        Queue queque = new Queue();
        queque.enqueue("11");
        queque.enqueue("22");
        queque.enqueue("33");

        System.out.println(queque.dequeue());
        System.out.println(queque.dequeue());
        System.out.println(queque.dequeue());
        System.out.println(queque.dequeue());

    }

    static class Queue {

        private Node head;
        private Node tail;

        /**
         * Add item to tail
         * @param item 
         */
        public void enqueue(String item) {
            Node node = new Node(item);
            if (isEmpty()) {
                head = tail = node;
            } else {
                tail.next = node;
                tail = node;
            }
        }

        /**
         * Remove from the head
         * @return 
         */
        public String dequeue() {
            if (isEmpty()) {
                throw new RuntimeException("queue is empty");
            } else if (head == tail) {
                String data = head.data;
                head = tail = null;
                return data;
            } else {
                String data = head.data;
                head = head.next;
                return data;
            }
        }

        public boolean isEmpty() {
            return head == null && tail == null;
        }

        private static class Node {

            public Node next;
            public String data;

            public Node(String data) {
                this.data = data;
            }
        }
    }

}
