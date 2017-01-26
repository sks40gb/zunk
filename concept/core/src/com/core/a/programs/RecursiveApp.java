package com.core.a.programs;

public class RecursiveApp {

    public static void main(String... args) throws Exception {
        //Factorial
        System.out.println("Factorial : " + factorial(5));

        //Power
        System.out.println("Power     : " + power(2, 5));

        // Binary Search
        int[] array = {1, 22, 43, 56, 63, 78, 99, 102};
        System.out.println(binarySearch(array, 0, array.length, 78));

        //Reverse word by word
        System.out.println(reverseWordByWord("Singh Kumar Sunil"));
        
        //Reverse Sentence 
        System.out.println(reverseSentence("Singh Kumar Sunil"));
    }

    private static final String SPACE = " ";

    /**
     * 
     * @param sentence - Singh Kumar Sunil
     * @return         - Sunil Kumar Singh
     */
    private static String reverseSentence(String sentence){
        if(sentence.contains(SPACE)){
            return sentence.substring(sentence.lastIndexOf(SPACE)) 
                    + SPACE 
                    + reverseSentence(sentence.substring(0, sentence.lastIndexOf(SPACE)));                    
        }else{
            return sentence;
        }
    }
    
    /**
     * 
     * @param text - Singh Kumar Sunil
     * @return     - hgniS ramuK linuS
     */
    private static String reverseWordByWord(String text) {
        if (text.length() == 1) {
            return text;
        } else if (text.contains(SPACE)) {   //if comment this condition, it will reverse the entire sentence not word by word
            return reverseWordByWord(text.substring(0, text.indexOf(SPACE)))
                    + SPACE
                    + reverseWordByWord(text.substring(text.indexOf(SPACE) + 1));
        } else {
            return text.charAt(text.length() - 1) + reverseWordByWord(text.substring(0, text.length() - 1));
        }
    }

    private static int factorial(int number) {
        if (number <= 1) {
            return 1;
        }
        return number * factorial(number - 1);
    }

    private static int power(int number, int power) {
        if (power == 0) {
            return 1;
        }
        return number * power(number, power - 1);
    }

    private static int binarySearch(int[] array, int start, int end, int element) {
        int middle = (start + end) / 2;
        int middleValue = array[middle];
        if (middle == 0) {
            return -1;
        } else if (middleValue == element) {
            return middle;
        } else if (element < middleValue) {
            return (binarySearch(array, 0, middle - 1, element));
        } else if (element > middleValue) {
            return binarySearch(array, middle + 1, end, element);
        }
        return -1;
    }
}
