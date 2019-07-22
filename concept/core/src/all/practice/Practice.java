package all.practice;

public class Practice {

    public static void main(String[] args) {
        int[] items = {1, 3, 6, 99, 102, 120};
        System.out.println(binarySearch(items, 99, 0, items.length - 1));
    }

    public static int binarySearch(int[] array, int key, int fromIndex, int toIndex) {
        while (fromIndex <= toIndex) {
            int midIndex = fromIndex + (toIndex - fromIndex) / 2;
            if (key == array[midIndex]) {
                return midIndex;
            } else if (key > array[midIndex]) {
                fromIndex = midIndex + 1;
            } else {
                toIndex = midIndex - 1;
            }

        }
        return -1;
    }

}
