package com.core.collection;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PriorityQueSample {
    
    public static void main(String[] args) {
                
        PriorityQueue<String> pq = new PriorityQueue<String>();
        pq.add("sunday");
        pq.add("monday");
        pq.add("tuesday");
        pq.offer("WEDNESDAY");
        pq.offer("THURSDAY");
        pq.offer("FRIDAY");
        pq.offer("SATURDAY");
        
        for(String s : pq){
            System.out.println("====> " + s);
        }
        
        //get the top record from the queue
        System.out.println("peek ---------------->" + pq.peek());
        
        //poll get the record and delete it from the queque.
        System.out.println("poll ---------------->" + pq.poll());        
        System.out.println("poll ---------------->" + pq.poll());        
        System.out.println("poll ---------------->" + pq.poll());        
        System.out.println("poll ---------------->" + pq.poll());        
        System.out.println("poll ---------------->" + pq.poll());        
        System.out.println("poll ---------------->" + pq.poll());        
        System.out.println("poll ---------------->" + pq.poll());        
        System.out.println("poll ---------------->" + pq.poll());        
        System.out.println("poll ---------------->" + pq.poll());        
        System.out.println("poll ---------------->" + pq.poll());        
        System.out.println("poll ---------------->" + pq.poll());        
        
        
    }

//    public int compare(Object o1, Object o2) {
//        
//    }

}
