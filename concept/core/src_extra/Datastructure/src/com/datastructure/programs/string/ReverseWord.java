package com.datastructure.programs.string;

import java.util.Stack;

/**
 * Input : "I love Java Programming".
 *
 * Output :"Programming Java love I".
 */
public class ReverseWord {

    public static void main(String[] args) {
        String str = "I love Java Programming";
//        String reversed = reverse(str);
        String reversed = reverse3(str);
        System.out.println(reversed);
    }

    //use recursive
    public static String reverse(String s) {
        int index = s.indexOf(" ");
        if (index == -1) {
            return s;
        }
        String word = s.substring(0, index);
        String subString = s.substring(index + 1);
        return reverse(subString) + " " + word;

    }

    //use iterative
    public static void reverse2(String str) {
        String substr[] = str.split(" ");
        String ans = "";
        for (int i = substr.length - 1; i >= 0; i--) {
            ans += substr[i] + " ";
        }
        System.out.println("Reversed String:");
        System.out.println(ans.substring(0, ans.length() - 1));
    }

    //Using stack
    public static String reverse3(String str) {
        String reversed = "";
        Stack<Character> stack = new Stack<>();
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == ' ') {
                while (!stack.isEmpty()) {
                    reversed += stack.pop();
                }
                reversed += " ";
            } else {
                stack.push(str.charAt(i));
            }
        }
        while (!stack.isEmpty()) {
            reversed += stack.pop();
        }
        return reversed;
    }

}
