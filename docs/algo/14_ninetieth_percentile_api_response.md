Note: This file is the full write-up for the 90th Percentile API response time topic; see `answers/coding_questions.md` for the consolidated index.

# 14. 90th Percentile API Response Time

## Learning Targets
- Define percentile formally.
- Compute percentile efficiently for a dataset.
- Understand trade-offs (sorting vs selection algorithms).

## Definition
90th percentile (P90): value below which 90% of observations fall. If sorted ascending, index = ceil(0.90 * N) - 1 (0-based) depending on interpolation convention. Many systems use nearest-rank without interpolation.

## Approaches
1. Sort All:
   - Sort times ascending. Return element at rank r = (int)Math.ceil(0.9 * N) - 1.
   - Time: O(N log N), Space: O(1) extra.
2. Selection (Quickselect):
   - Find k-th order statistic (k = r). Average O(N), worst-case O(N^2). Useful for large streams.
3. Streaming Approximation:
   - Use T-Digest, HDR Histogram, or GK algorithm for large-scale approximate P90.

## Java Implementation (Sort-Based)
```java
public class Percentile90 {
    public static double p90(int[] times) {
        if (times.length == 0) throw new IllegalArgumentException("Empty dataset");
        int[] copy = times.clone();
        Arrays.sort(copy);
        int n = copy.length;
        int rank = (int)Math.ceil(0.9 * n) - 1; // nearest-rank method
        rank = Math.max(rank, 0);
        return copy[rank];
    }
    public static void main(String[] args){
        System.out.println(p90(new int[]{10,20,30,40,50,60,70,80,90,100})); // 90
    }
}
```

## Edge Cases
- Single value ⇒ that value.
- Duplicate values cluster—percentile may equal repeated element.
- Different conventions (linear interpolation) may produce intermediate value; clarify methodology.

## When Use Approximation
- Massive streams (> millions per interval) where O(N log N) sort costs too high.
- Need low-latency percentile queries.

## Summary
For moderate datasets, sort then pick nearest-rank element. For large-scale real-time analytics, adopt specialized streaming quantile data structures.
