package com.datastructure.programs;

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
public class A2_Array_90_Degree {

    public static void main(String[] args) {
        String[][] myMatrix = new String[][]{new String[]{"1", "2", "3"}, new String[]{"4", "5", "6"}, new String[]{"7", "8", "9"}};
        rotateMatrix(myMatrix);
    }

    public static void rotateMatrix(String[][] myMatrix) {
        String[][] matrix = new String[3][3];
        for (int i = (myMatrix.length - 1), k =0; i >= 0; i--, k++) {
            for (int j = 0; j < myMatrix[i].length; j++) {
                //System.out.println(myMatrix[i][j]);
                matrix[j][k] = myMatrix[i][j];
            }
        }
        
        for (int i = 0; i < matrix.length; i++) {
            String[] strings = matrix[i];
            for (int j = 0; j < strings.length; j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println("----");
            
        }

    }
}
