package com.interview.algorithms;

import java.util.HashSet;
import java.util.Set;

public class PolesSequence {
    /**
     * Returns true if there exists x such that x, 2x, 4x are present in the array.
     */
    public static boolean hasX2X4(int[] heights) {
        if (heights == null || heights.length < 3) return false;
        Set<Integer> s = new HashSet<>();
        for (int h : heights) s.add(h);
        for (int h : heights) {
            long twice = (long) h * 2;
            long four = (long) h * 4;
            if (twice <= Integer.MAX_VALUE && four <= Integer.MAX_VALUE) {
                if (s.contains((int) twice) && s.contains((int) four)) return true;
            }
        }
        return false;
    }
}

