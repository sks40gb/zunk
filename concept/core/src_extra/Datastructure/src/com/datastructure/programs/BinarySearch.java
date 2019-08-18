package com.datastructure.programs;

/**
 *
 * @author sunil
 */
public class BinarySearch {

    public static void main(String[] args) {
        int[] items = {1, 3, 7, 9, 20, 26, 39, 49, 59, 78, 99, 101};
        int key =  78;
        System.out.println(search(items, key, 0, items.length-1));
    }

    public static int search(int[] items, int key, int start, int end) {
        int mid = (start + end )/ 2;
        int midValue = items[mid];
        if(midValue == key){
            return mid;
        } else if(key >  midValue){
            return search(items, key, mid+1, end);
        }else if(key < midValue){
            return search(items, key, start, mid-1);
        }
        
        return -1;
    }

}
