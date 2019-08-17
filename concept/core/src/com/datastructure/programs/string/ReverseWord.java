package com.datastructure.programs.string;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author sunsingh
 */
public class ReverseWord {

    public static void main(String[] args) {
        String str = "i like this program very much";
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
        String s1 = s.substring(0, index);
        String s2 = s.substring(index + 1);
        return reverse(s2) + " " + s1;

    }

    //use iterative
    public static void reverse2() {
        String substr[] = "i like this program very much".split(" ");
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
        Deque<Character> stack = new ArrayDeque<>();
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
