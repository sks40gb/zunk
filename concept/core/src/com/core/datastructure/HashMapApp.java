package com.core.datastructure;

import java.util.Objects;

/**
 *
 * @author Sunil
 */
public class HashMapApp {

    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        map.put("Name", "Sunil");
        map.put("title", "Singh");

        System.out.println(map.get("Name"));
        System.out.println(map.get("title"));
    }
}

class HashMap<K, V> {

    private static final int DEFAULT_SIZE = 10;
    private final LinkedList<K, V>[] array;

    public HashMap() {
        array = new LinkedList[DEFAULT_SIZE];
    }
    
     public HashMap(int capacity) {
        array = new LinkedList[capacity];
    }

    public void put(K k, V v) {
        //hash key
        LinkedList linkedList = getLinkedList(k);
        Node<K, V> node = getNode(linkedList, k);
        if (node != null) {
        } else {
            node = new Node<>(k);
            linkedList.add(node);
        }
        node.setValue(v);
    }

    public V get(K k) {
        LinkedList linkedList = getLinkedList(k);
        Node<K, V> node = getNode(linkedList, k);
        if (node == null) {
            return null;
        } else {
            return node.getValue();
        }
    }

    private LinkedList getLinkedList(K k) {
        int hash = hash(k);
        LinkedList linkedList = array[hash];
        if (linkedList == null) {
            linkedList = new LinkedList();
            array[hash] = linkedList;
        }

        return linkedList;
    }

    private Node getNode(LinkedList linkedList, K k) {

        //check key already exists
        Node node = linkedList.currentNode;
        while (node != null) {
            if (node.getKey().equals(k)) {
                return node;
            }
            node = linkedList.currentNode;
        }
        return null;
    }

    private int hash(K k) {
        return k.hashCode() % size();
    }

    private int size() {
        return array.length;
    }

    class LinkedList<K, V> {

        private int size;
        private Node<K, V> currentNode;

        public void add(Node node) {
            if (currentNode == null) {
                currentNode = node;
            } else {
                currentNode.next = node;
                currentNode = node;
            }
            size++;
        }

        public int size() {
            return size;
        }
    }

    class Node<K, V> {

        private Node next;
        private K key;
        private V value;

        public Node(K key) {
            this.key = key;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + Objects.hashCode(this.key);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Node<?, ?> other = (Node<?, ?>) obj;
            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Node{" + "key=" + key + ", value=" + value + '}';
        }
    }
}
