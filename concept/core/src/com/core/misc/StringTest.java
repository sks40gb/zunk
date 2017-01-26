
package com.core.misc;

/** 
 *
 * @author Sunil
 */
public class StringTest {
    
    public static void main(String[] args) {
        compareString();
    }

    private static void compareString(){
        String s1 = "ABC";
        String s2 = "ABC";
        String s3 = new String("ABC");
        String s4 = new String("ABC");

        System.out.println(s1 == s2);
        System.out.println(s3 == s4);

    }

}
