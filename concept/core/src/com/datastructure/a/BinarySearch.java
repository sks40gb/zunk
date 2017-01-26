
package com.datastructure.a;

/**
 *
 * @author Sunil
 */
public class BinarySearch {
    
    public static void main(String[] args) {
        System.out.println(search(8,new int[]{1,8,9},0,1));
    }
    
    public static int search(int element, int[] list, int min, int max){
        if(max < min){
            throw new RuntimeException("Element is found !");
        }else{
            int mid = (min + max)/2;
            if(list[mid] > element){
                return search(element, list, min, mid-1);
            }else if(list[mid] < element){
                return search(element, list, mid+1, max);
            }else{
                return mid;
            }
        }
    }

}
