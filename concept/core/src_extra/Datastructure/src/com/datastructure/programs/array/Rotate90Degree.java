package com.datastructure.programs.array;

/*
 * http://collabedit.com/4htbj
 *
 * Given a n x n 2d array implement a method that rotate the object 90 degrees clockwise and print to console
 *
 * Example
 *
 * [1,2,3]
 * [4,5,6]
 * [7,8,9]
 *
 * rotate 90 degrees clockwise and print to console
 *
 * 7,4,1
 * 8,5,2
 * 9,6,3
 *
 */
public class Rotate90Degree {

    public static void main(String[] args) {
        String[][] myMatrix = new String[][]{new String[]{"1", "2", "3"}, new String[]{"4", "5", "6"}, new String[]{"7", "8", "9"}};
        rotateMatrix(myMatrix);
    }

    public static void rotateMatrix(String[][] matrix) {
        String[][] rotation = new String[3][3];

        for (int row = matrix.length - 1; row >= 0; row--) {
            for (int col = 0; col < matrix[row].length; col++) {
                String v = matrix[row][col];
                rotation[col][2 - row] = v;
            }
        }

        for (int row = 0; row < rotation.length; row++) {
            for (int col = 0; col < rotation[row].length; col++) {
                System.out.print(rotation[row][col]);
            }
            System.out.println("");

        }
    }
}
