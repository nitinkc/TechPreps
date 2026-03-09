package com.interview.algorithms;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PercentileCalculatorTest {

    @Test
    public void test90thPercentile() {
        List<Integer> values = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        double p90 = PercentileCalculator.calculate90thPercentile(values);
        // With 10 values, 90th percentile should be near 9.1 -> interpolation gives 9.1
        assertTrue(p90 >= 9.0 && p90 <= 9.2);
    }
}

