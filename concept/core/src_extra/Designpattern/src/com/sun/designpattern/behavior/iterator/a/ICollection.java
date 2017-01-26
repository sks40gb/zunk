package com.sun.designpattern.behavior.iterator.a;

import java.util.Iterator;

/**
 *
 * @author Sunil
 */
public class ICollection {

    public static void main(String[] args) {
        ICollection collection = new ICollection();
        Iterator<String> itr = collection.iterator();
        while(itr.hasNext()){
            System.out.println(itr.next());
        }
    }
    
    private String[] items = {"Sunil", "1", "2", "3"};


    public Iterator<String> iterator(){
        return new CIterator();
    }
    
    public class CIterator implements Iterator<String> {

        private int index;

        public boolean hasNext() {
            return index < items.length;
        }

        public String next() {
            if (index < items.length) {                
                String s =  items[index];
                index++;
                return s;
            } else {
                return null;
            }
        }
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}

interface IIterator {

    public boolean hasNext();

    public Object Next();
}

