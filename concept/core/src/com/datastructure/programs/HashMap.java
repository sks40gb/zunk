package com.datastructure.programs;

/**
 *
 * @author sunsingh
 */
public class HashMap<K, V> {

    public static void main(String[] args) {
        HashMap<Integer, String> map = new HashMap<>();
//        map.put(1, "Sunday");
//        map.put(2, "Monday");
//        map.put(3, "Tuesday");
//        map.put(4, "Wednesday");
//        map.put(5, "Thursday");
//        map.put(6, "Friday");
//        map.put(7, "Saturday");

        for (int i = 0; i < 20; i++) {
             map.put(i, "S"+i);
        }
        map.print();
        
        System.out.println("Find element 11 " + map.get(11));
        System.out.println("Removing element 11 "+ map.remove(11));
        System.out.println("Find element 11 " + map.get(11));
        
    }

    private Entry<K, V>[] items;
    private int size;
    private static int INITIAL_SIZE = 16; //16

    public HashMap() {
        this(INITIAL_SIZE);
    }

    public HashMap(int capacity) {
        this.items = new Entry[capacity];
    }

    public void put(K k, V v) {
        Entry<K, V> existing = this.items[hash(k)];
        if (existing == null) {
            this.items[hash(k)] = new Entry<>(k, v);
        } else {
            while (existing.next != null) {
                if (existing.next.key.equals(k)) {
                    existing.next.value = v;
                    return;
                }
                existing = existing.next;
            }
            existing.next = new Entry<>(k, v);
            size++;
        }
    }

    public int size() {
        return this.size;
    }

    public V get(K k) {
        Entry<K, V> existing = this.items[hash(k)];
        while (existing != null) {
            if (existing.key.equals(k)) {
                return existing.value;
            }
            existing = existing.next;
        }
        return null;
    }

    public boolean remove(K k) {
        int hash = hash(k);
        Entry<K, V> existing = this.items[hash];
        if (existing != null) {
            if (existing.key.equals(k)) {
                this.items[hash] = null;
            } else {
                Entry<K, V> next = existing.next;
                while (next != null) {
                    if (next.key.equals(k)) {
                        existing.next = next.next;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int hash(K k) {
        return k.hashCode() % this.items.length;
    }

    public void print() {
        for (int i = 0; i < items.length; i++) {
            Entry<K, V> item = items[i];
            while (item != null) {
                System.out.println("Hash[" + i + "] " + item);
                item = item.next;
            }
        }
    }

    class Entry<K, V> {

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        K key;
        V value;
        Entry<K, V> next;

        @Override
        public String toString() {
            return "Entry{" + "key=" + key + ", value=" + value + '}';
        }

    }
}
