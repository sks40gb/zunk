/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.collection;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author sunil
 */
public class DequeueTest {

    public static void main(String[] args) {
        Deque ad = new ArrayDeque();
        ad.add("01");
        ad.offer("02");
        ad.offer("03");
        //remove first element
        ad.poll();
        //add the element at the top
        ad.offerFirst("04");

        for (Object o : ad) {
            ad.peek();
            System.out.println("-------------> " + o);
        }
    }
}
