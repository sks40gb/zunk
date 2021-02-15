package com.sun.designpattern.behavioral.iterator;

/**
 * In object-oriented programming, the iterator pattern is a design pattern in which an iterator is used to traverse a
 * container and access the container's elements.
 *
 * @author Sunil
 */
public class IteratorApp {

    public static void main(String[] args) {
        String[] colors = {"Red", "Green", "Blue"};
        Collection<String> coll = new ColorCollection<>(colors);
        Iterator<String> iterator = coll.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.getNext());
        }
    }
}

interface Iterator<T> {

    boolean hasNext();

    T getNext();

    void remove();
}

interface Collection<T> {

    Iterator<T> iterator();
}

class ColorCollection<T> implements Collection<T> {

    private final T[] data;

    public ColorCollection(T[] data) {
        this.data = data;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int currentIndex = -1;

            @Override
            public boolean hasNext() {
                return currentIndex < (data.length - 1);
            }

            @Override
            public T getNext() {
                currentIndex++;
                return data[currentIndex];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(
                    "Not supported yet.");
            }
        };         
    }
}


