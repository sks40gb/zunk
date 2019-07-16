package com.core.datastructure;

public class QueueByArray {

    public static void main(String... args) throws Exception {
        Queue queque = new Queue();
        queque.enqueque("11");
        queque.enqueque("22");
        queque.enqueque("33");

        System.out.println(queque.dequeue());
        System.out.println(queque.dequeue());
        System.out.println(queque.dequeue());

    }

    static class Queue {

        private final String array[] = new String[10];
        private int headIndex = -1;
        private int tailIndex = -1;
 
        public void enqueque(String item) {
            System.out.println("enqueue : " + item);
            if (isEmpty()) {
                array[0] = item;
                headIndex = 0;
                tailIndex = 0;
            } else if (isFull()) {
                throw new RuntimeException("Array is filled");
            } else {
                tailIndex = (tailIndex + 1) % array.length;
                array[tailIndex] = item;
            }
        }

        public String dequeue() {
            if (isEmpty()) {
                throw new RuntimeException("Queue is empty");
            } else if (headIndex == tailIndex) {
                String value = array[headIndex];
                headIndex = -1;
                tailIndex = -1;
                return value;
            } else {
                return array[headIndex++];
            }
        }

        public boolean isEmpty() {
            return (headIndex == -1 && tailIndex == -1);
        }

        public boolean isFull() {
            return (tailIndex + 1) % array.length == headIndex;
        }
    }
}
