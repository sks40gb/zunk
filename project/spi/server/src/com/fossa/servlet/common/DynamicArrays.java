/* $Header: /home/common/cvsarea/ibase/dia/src/common/DynamicArrays.java,v 1.1 2004/02/12 16:21:27 weaston Exp $ */
package com.fossa.servlet.common;

import java.lang.reflect.Array;

/**
 * A utility class for modifying arrays.  Intended for
 * infrequently modified, but frequently accesses, arrays.
 * Currently, implemented only for Object and int arrays,
 * but could be implemented for other primitive types
 * by following the implementation for int.
 * <p>
 * Null arrays are treated as arrays of length zero.  For an array
 * of an elementary type, null is returned by remove() instead of
 * a zero-length array.  For an array of a reference type, remove()
 * returns a zero-length array, in order to retain the run-time
 * type of the array.  
 */
public class DynamicArrays {

    private DynamicArrays() {}

    ////////// Dynamic arrays of a reference type

    /**
     * Create a new array of a reference type by appending a new element.
     * The returned array has the same runtime type as the given array.
     * If the given array is null, the returned array's runtime type
     *   is an array of the type of the given element.
     */
    public static Object[] append(Object[] arr, Object item) {
        Object[] newArr;
        if (arr == null) {
            newArr = (Object[]) Array.newInstance(item.getClass(), 1);
        } else {
            newArr = newArray(arr, arr.length + 1);
            System.arraycopy(arr, 0, newArr, 0, arr.length);
        }
        newArr[newArr.length - 1] = item;
        return newArr;
    }

    /**
     * Create a new array of a reference type by inserting a new element.
     * The returned array has the same runtime type as the given array.
     * The given array may not be null.
     */
    public static Object[] insert (Object[] arr, int index, Object item) {
        Object[] newArr = newArray(arr, arr.length + 1);
        System.arraycopy(arr, 0, newArr, 0, index);
        newArr[index] = item;
        System.arraycopy(arr, index, newArr, index + 1, arr.length - index);
        return newArr;
    }

    /**
     * Create a new array of a reference type by deleting an element.
     * The returned array has the same runtime type as the given array.
     * The given array may not be null.
     */
    public static Object[] remove (Object[] arr, int index) {
        Object[] newArr = newArray(arr, arr.length - 1);
        System.arraycopy(arr, 0, newArr, 0, index);
        System.arraycopy(arr, index + 1, newArr, index, arr.length - index - 1);
        return newArr;
    }

    // Make a new array with the element type of a given array
    // and a given size.
    // The given array may not be null.
    private static Object[] newArray(Object[] arr, int size) {
        return (Object[])Array.newInstance(
                            arr.getClass().getComponentType(), size);
    }

    /**
     * Create a new int array of by deleting starting at a given position.
     */
    public static Object[] truncate(Object[] arr, int index) {
        if (index < 0 || index > arr.length) {
            throw new ArrayIndexOutOfBoundsException("not 0 <= "+index+" < "+arr.length);
        }
        if (index == arr.length) {
            return arr;
        } else {
            Object[] newArr = newArray(arr, index);
            System.arraycopy(arr, 0, newArr, 0, index);
            return newArr;
        }
    }

    ////////// Dynamic arrays of int

    /**
     * Create a new int array of by appending a new element.
     * If the given array is null, a new one-element array is created.
     */
    public static int[] append(int[] arr, int item) {
        int[] newArr;
        if (arr == null) {
            newArr = new int[] { item };
        } else {
            newArr = new int[arr.length + 1];
            System.arraycopy(arr, 0, newArr, 0, arr.length);
            newArr[arr.length] = item;
        }
        return newArr;
    }

    /**
     * Create a new int array by inserting a new element at a given position.
     */
    public static int[] insert(int[] arr, int index, int item) {
        if (arr == null && index == 0) {
            return append(null, item);
        }
        int[] newArr = new int[arr.length + 1];
        System.arraycopy(arr, 0, newArr, 0, index);
        newArr[index] = item;
        System.arraycopy(arr, index, newArr, index + 1, arr.length - index);
        return newArr;
    }

    /**
     * Create a new int array of by deleting the element at a given position.
     * if index == 0, null is returned;
     */
    public static int[] remove(int[] arr, int index) {
        if (arr == null) {
            throw new ArrayIndexOutOfBoundsException("not 0 <= "+index+" < 0");
        } else if (index < 0 || index > arr.length) {
            throw new ArrayIndexOutOfBoundsException("not 0 <= "+index+" < "+arr.length);
        }
        if (arr.length == 1) {
            return null;
        } else {
            int[] newArr = new int[arr.length - 1];
            System.arraycopy(arr, 0, newArr, 0, index);
            System.arraycopy(arr, index + 1, newArr, index, arr.length - index - 1);
            return newArr;
        }
    }

    /**
     * Create a new int array of by deleting starting at a given position.
     */
    public static int[] truncate(int[] arr, int index) {
        if (arr == null) {
            throw new ArrayIndexOutOfBoundsException("not 0 <= "+index+" < 0");
        } else if (index < 0 || index > arr.length) {
            throw new ArrayIndexOutOfBoundsException("not 0 <= "+index+" < "+arr.length);
        }
        if (index == 0) {
            return null;
        } else if (index == arr.length) {
            return arr;
        } else {
            int[] newArr = new int[index];
            System.arraycopy(arr, 0, newArr, 0, index);
            return newArr;
        }
    }

    /**
     * Get the length of an int array, treating null as an array
     * of length zero.
     */
    public static int length(int[] arr) {
        return (arr == null ? 0 : arr.length);
    }
}
