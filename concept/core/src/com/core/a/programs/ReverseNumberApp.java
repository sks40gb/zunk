package com.core.a.programs;


public class ReverseNumberApp {

    public static void main(String[] args) {
        System.out.println(reverseNumber(1234));
    }


public static int reverseNumber(int number){
    int reverse = 0;
    while(number > 0){
        reverse = reverse * 10 + number % 10;
        number = number/10;
    }
    return reverse;
}

}