package com.datastructure.programs;

/**
 *
 * @author sunil
 */
public class TowerHopper {

    public static void main(String[] args) {
        int[] tower = {4, 2, 0, 0, 2, 0};
        System.out.println("is " + isHopper(tower));
    }

    public static boolean isHopper(int[] arr) {
        for (int i = arr.length-1; i >= 0; i--) {       //4
           if(i + arr[i] >= arr.length){                //4 + 2 = true
               return matchFound(arr, i);               //4
           } 
        }
        return false;
    }
    
    public static boolean matchFound(int[] arr, int matchIndex){  //4
        System.out.println("matchIndex " + matchIndex);
        boolean matched = false;
        for(int i = matchIndex-1; i>=0; i--){                     //0
            matched = i + arr[i] == matchIndex;            //0+4 = 4
           //System.out.println("matched " + matched);
           if(matched){
               matched = matchFound(arr, i);
               break;
           }
        }
        return matched;
    }

}
