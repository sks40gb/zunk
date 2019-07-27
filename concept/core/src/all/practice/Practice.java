package all.practice;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;


public class Practice {

    public static void main(String[] args) {
       
        
        UnaryOperator<Integer> sqaure = (number) -> number*number;
        System.out.println("square of 4 is " + sqaure.apply(4));
        
        BinaryOperator<Integer> sum = (first, second) -> first + second;
        
        System.out.println("The sum of 5 and 4 is " + sum.apply(5, 4) );
        
        

    }
}

@FunctionalInterface
interface Math{
    
    public int calc(int a,int b, int c);
    
}