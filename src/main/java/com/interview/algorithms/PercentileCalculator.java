package com.interview.algorithms;

import java.util.*;

public class PercentileCalculator {
    /**
     * Calculate 90th percentile from a list of integers using interpolation.
     */
    public static double calculatePercentile(List<Integer> values, double percentile) {
        if (values == null || values.isEmpty()) throw new IllegalArgumentException("values empty");
        if (percentile <= 0 || percentile >= 1) throw new IllegalArgumentException("percentile should be between 0 and 1 exclusive");

        List<Double> sorted = new ArrayList<>();
        for (Integer v : values) sorted.add(v.doubleValue());
        Collections.sort(sorted);

        int n = sorted.size();
        double pos = percentile * (n - 1);
        int lower = (int) Math.floor(pos);
        int upper = (int) Math.ceil(pos);
        if (lower == upper) return sorted.get(lower);
        double lowerVal = sorted.get(lower);
        double upperVal = sorted.get(upper);
        double fraction = pos - lower;
        return lowerVal + fraction * (upperVal - lowerVal);
    }

    public static double calculate90thPercentile(List<Integer> values) {
        return calculatePercentile(values, 0.9);
    }
}

