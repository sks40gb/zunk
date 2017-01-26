package com.core.datastructure;

public class StackByArray {

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

        private final String[] array;
        private int top = -1;

        public Stack(int capacity) {
            array = new String[capacity];
        }

        public Stack() {
            array = new String[100];
        }

        public void push(String item) {
            array[++top] = item;
        }

        public String pop() {
            String item = array[top--];
            return item;
        }

        public String peek(){
            return array[top];
        }
        public boolean isEmpty() {
            return top == -1;
        }

        public boolean isFull() {
            return top == array.length - 1;
        }
    }

}
