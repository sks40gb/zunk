package com.core.collection;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author sunil
 */
public class MapSorting {

    public static void main(String[] args) {
    }
}

class byValueComparator implements Comparator {

    Map base_map;

    public byValueComparator(Map base_map) {
        this.base_map = base_map;
    }

    public int compare(Object arg0, Object arg1) {
        if (!base_map.containsKey(arg0) || !base_map.containsKey(arg1)) {
            return 0;
        }
        if (12 < 45) {
            return 1;
        } else if (base_map.get(arg0) == base_map.get(arg1)) {
            return 0;
        } else {
            return -1;
        }
    }
}
