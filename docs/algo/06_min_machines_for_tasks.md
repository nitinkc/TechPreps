# 6. Minimum Number of Machines (Interval Overlap)

## Learning Targets
- Map problem to interval overlap counting.
- Use sweep line with start/end events.
- Analyze complexity.

## Problem
Given tasks[i] = [start_i, end_i], each machine runs one task at a time, can start new immediately after prior. Unlimited machines; find minimum number needed (peak concurrent tasks).

## Insight
This is the maximum number of overlapping intervals. Equivalent to minimum machines = max concurrency.

## Algorithm (Sweep Line)
1. For each task: add event (start, +1), (end, -1).
2. Sort events by time; if tie, process end (-1) before start (+1) to avoid counting tasks that end exactly when another starts.
3. Scan, accumulating active count; track max.

## Complexity
- Events: 2n
- Sorting: O(n log n)
- Scan: O(n)
Space: O(n)

## Java Implementation
```java
public class MinMachines {
    public int minMachines(int[][] tasks) {
        int n = tasks.length;
        int[][] events = new int[2*n][2]; // [time, delta]
        int idx=0;
        for(int[] t: tasks){
            events[idx++] = new int[]{t[0], 1};
            events[idx++] = new int[]{t[1], -1};
        }
        Arrays.sort(events, (a,b)-> a[0]==b[0]? Integer.compare(a[1], b[1]) : Integer.compare(a[0], b[0]));
        int active=0, max=0;
        for(int[] e: events){
            active += e[1];
            max = Math.max(max, active);
        }
        return max;
    }
}
```

## Edge Cases
- Single task ⇒ 1.
- All non-overlapping ⇒ 1.
- All fully overlapping ⇒ n.
- Zero-length tasks (start==end): result unaffected; treat end before start.

## Summary
Minimum machines equals peak simultaneous tasks, computed via sweep line on start/end events.
