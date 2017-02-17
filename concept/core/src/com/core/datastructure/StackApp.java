package com.core.datastructure;

/**
 * 	It can be implemented by Array or Linked List
*	Array - it has the limitation of fixed size
*	Linked Size - Dynamic size is available
*	
*	Array
*	Push(Item)         - best O(1) and worst O(n) in case of resigning the array
*	Pop()              - O(1)  fetch and remove
*	Peek()             - O(1)  fetch
*	isEmpty()          - if Empty
*	
*	Linked List - in case if the item is being added and removed from the end of list
*	Push(Item)         - best O(n) 
*	Pop()              - O(n)  fetch and remove
*	Peek()             - O(n)  fetch
*       isEmpty()          - if Empty
* 
*	Linked List - in case if the item is being added and removed from the start of the list
*	Push(Item)         - best O(1) 
*	Pop()              - O(1)  fetch and remove
*	Peek()             - O(1)  fetch
*       isEmpty()          - if Empty
* 
 * @author Sunil
 */
public class StackApp {

    public static void main(String[] args) {
        Stack theStack = new Stack(10);
        theStack.push(10);
        theStack.push(20);
        theStack.push(30);
        theStack.push(40);
        theStack.push(50);
        while (!theStack.isEmpty()) {
            long value = theStack.pop();
            System.out.print(value);
            System.out.print(" ");
        }
        System.out.println("");
    }
}

class Stack {

    private final int maxSize;
    private final long[] array;
    private int top;

    public Stack(int s) {
        maxSize = s;
        array = new long[maxSize];
        top = -1;
    }

    public void push(long j) {
        array[++top] = j;
    }

    public long pop() {
        return array[top--];
    }

    public long peek() {
        return array[top];
    }

    public boolean isEmpty() {
        return (top == -1);
    }

    public boolean isFull() {
        return (top == maxSize - 1);
    }
}
