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
        String[][] result = new String[3][3];
        for(int i=0; i<matrix.length; i++){
            int rowIndex = matrix.length - i -1;
            for(int j=0; j<matrix[rowIndex].length; j++){
                result[j][i] = matrix[rowIndex][j];  
            }
        }
        for (int i = 0; i < result.length; i++) {
            String[] strings = result[i];
            for (int j = 0; j < strings.length; j++) {
                System.out.print(result[i][j]);
            }
            System.out.println("");
            
        }

    }
}
