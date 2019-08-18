package com.datastructure.programs.string;

/**
 *
 * @author sunsingh
 */
public class StringRotation {

    public static void main(String[] args) {
        String s1 = "IndiaUSAEngland";
        String s2 = "USAEnglandIndia";
        System.out.println(checkRotatation(s1, s2));
    }

    /**
     * This method check is given strings are rotation of each other
     *
     * @param original
     * @param rotation
     * @return true or false
     */
    public static boolean checkRotatation(String original, String rotation) {
        if (original.length() != rotation.length()) {
            return false;
        }
        String concatenated = original + original;
        return concatenated.contains(rotation);
    }
}
