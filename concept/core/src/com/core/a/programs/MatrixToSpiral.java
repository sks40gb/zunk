package com.core.a.programs;

/**
 *
 * @author Sunil
 */
public class MatrixToSpiral {

    public static void main(String[] args) {
        int a[][] = {
            {1, 2, 3, 4, 5, 6},
            {7, 8, 9, 10, 11, 12},
            {13, 14, 15, 16, 17, 18}
        };

        printSpiral(a);

    }

    private static void printSpiral(int[][] array) {

        int R1 = 0;                     // row start index
        int R2 = array.length - 1;      // row end index
        int C1 = 0;                     // col start index
        int C2 = array[0].length - 1;   // col end index

        while (R1 <= R2 && C1 <= C2) {

            //top
            for (int i = C1; i <= C2; i++) {
                print(array[R1][i]);
            }
            R1++;

            //right
            for (int i = R1; i <= R2; i++) {
                print(array[i][C2]);
            }
            C2--;

            //bottom
            if (R1 < R2) {
                for (int i = C2; i >= C1; i--) {
                    print(array[R2][i]);
                }
                R2--;

            }

            //left
            if (C1 < C2) {
                for (int i = R2; i >= R1; i--) {
                    print(array[i][C1]);
                }
                C1++;
            }

//            break;
        }
    }

    public static void print(int element) {
        System.out.print(element + ",");
    }
}
