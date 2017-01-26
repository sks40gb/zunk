package com.core.datastructure;

/**
 *
 * @author Sunil
 */
public class LinkedListApp {

    public static void main(String[] args) {
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("11");
        linkedList.add("22");
        linkedList.add("33");
        linkedList.add("44");
        linkedList.reverseByRecursive();

//        linkedList.reversePrint();
//      linkedList.remove(1);
        Iterator iterator = linkedList.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}

class LinkedList<T> {

    private Node first;
    private int size;

    public void add(T item) {
        Node node = new Node(item);
        if (first == null) {
            first = node;
        } else {
            Node temp = first;
            while (temp.getNext() != null) {
                temp = temp.getNext();
            }
            temp.setNext(node);
        }
        size++;
    }

    public boolean remove(int index) {
        if (index > size || index < 0) {
            throw new ArrayIndexOutOfBoundsException("Size : " + size + " : passed index : " + index);
        } else if (index == 0) {
            first = first.getNext();
        } else {
            Node temp = first;
            for (int i = 0; i < index - 1; i++) {
                temp = temp.getNext();
            }
            temp.setNext(temp.getNext().getNext());
        }
        size--;
        return true;
    }

    public int size() {
        return size;
    }

    //** Utility functions **/
    public void reversePrint() {
        reversePrint(first);
    }

    private void reversePrint(Node node) {
        if (node != null) {
            reversePrint(node.getNext());
            System.out.println(node.getData());
        }
    }

    public void reverseByRecursive() {
        reverseByRecursive(first);
    }

    public void reverseByRecursive(Node current) {
        if (current.getNext() == null) {
            first = current;
            return;
        }
        reverseByRecursive(current.getNext());
        current.getNext().setNext(current);
        current.setNext(null);
    }

    public void reverse() {
        Node current = first;
        Node prev = null;
        Node next;

        while (current != null) {
            next = current.getNext();
            current.setNext(prev);
            prev = current;
            current = next;
        }
        //reset head
        first = prev;

    }

    /**
     * Iterator *
     */
    public Iterator iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;
            private Node<T> currentNode = first;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public T next() {
                T data = currentNode.getData();
                currentNode = currentNode.getNext();
                currentIndex++;
                return data;
            }

        };
    }

}

interface Iterator<T> {

    public boolean hasNext();

    public T next();
}

class Node<T> {

    private final T data;
    private Node next;

    public Node(T value) {
        this.data = value;
    }

    public T getData() {
        return data;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public String toString() {
        return data.toString();
    }
}
