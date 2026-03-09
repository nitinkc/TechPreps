Note: This file is the full write-up for the Poles Sequence x,2x,4x problem; see `answers/coding_questions.md` for the consolidated index.

# 15. Poles Sequence x, 2x, 4x Detection

## Learning Targets
- Transform multiplicative sequence search into set/mapping lookups.
- Optimize for O(n) average time.
- Handle duplicates and ordering constraints.

## Problem
Given list of pole heights (integers), determine if there exist three heights forming x, 2x, 4x (order in list can be any, not necessarily consecutive).

## Approach 1: Set Lookup
For each height h, treat h as x. Check presence of 2h and 4h in set.
Time: O(n) average; Space: O(n).

Edge Case: Duplicates – fine; only need existence.

## Java Implementation
```java
public class PoleSequence {
    public boolean hasX2X4(int[] heights) {
        Set<Integer> set = new HashSet<>();
        for(int h: heights) set.add(h);
        for(int h: set) { // iterate unique x
            if (set.contains(2*h) && set.contains(4*h)) return true;
        }
        return false;
    }
    public static void main(String[] args){
        System.out.println(new PoleSequence().hasX2X4(new int[]{3,6,12,5})); // true (3,6,12)
        System.out.println(new PoleSequence().hasX2X4(new int[]{1,2,8})); // false (need 4)
    }
}
```

## Approach 2: Early Pruning
Sort array ascending; break when 4x exceeds max height.
- Sorting O(n log n), then binary search or two-pointer presence check.

## Complexity
Approach 1: Time O(n), Space O(n).
Approach 2: Time O(n log n), Space O(1) extra.

## Negative or Zero Heights?
If allowed and x can be zero, then sequence (0,0,0) works (since 0*2=0). Clarify domain; if heights strictly positive, ignore zero logic.

## Summary
Use hash set to test for triple (x,2x,4x) efficiently. Presence checks yield O(n) average time.
