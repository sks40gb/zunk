package com.core.a.programs;

public class ReverseString {

    public static void main(String[] args) {
        System.out.println(new ReverseString().reverse("ABC123"));
    }

    public String reverse(String str) {
        if (str.length() == 1) {
            return str;
        } else {
            return str.charAt(str.length() - 1) + reverse(str.substring(0, str.length() - 1));
        }
    }

}
