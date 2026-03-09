# 10. Combination Sum Problem

## Learning Targets
- Backtracking with repetition allowed.
- Pruning based on remaining target.
- Handling candidate ordering to avoid duplicates.

## Problem
Given candidates (unique positive integers) and target, find all unique combinations summing to target. A candidate may be chosen unlimited times.
Examples:
1) candidates = [2,3,6,7], target = 7 → [[2,2,3],[7]]
2) candidates = [2,3,5], target = 8 → [[2,2,2,2],[2,3,3],[3,5]]

(Note: Instruction "Just input make it 235 instead of 236723 and five" appears to clarify second example uses 2,3,5 only.)

## Approach (Backtracking)
Sort candidates to ensure non-decreasing usage; pass an index to allow reuse of same candidate and prevent permutations that duplicate sets.

## Algorithm
dfs(startIndex, currentList, remainingTarget):
- If remainingTarget == 0: add copy of currentList.
- For i from startIndex to end:
  - If candidates[i] > remainingTarget: break (prune).
  - Add candidates[i]; recurse with same i (allow reuse); then remove.

## Complexity
Let N = number of candidates, T = target.
- Worst-case time exponential: O(number of valid combinations * average length).
- Space: recursion depth up to T / minCandidate.

## Java Implementation
```java
public class CombinationSum {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        Arrays.sort(candidates); // helps pruning
        List<List<Integer>> res = new ArrayList<>();
        backtrack(candidates, target, 0, new ArrayList<>(), res);
        return res;
    }
    private void backtrack(int[] c, int remain, int start, List<Integer> path, List<List<Integer>> res){
        if (remain == 0) { res.add(new ArrayList<>(path)); return; }
        for (int i = start; i < c.length; i++) {
            if (c[i] > remain) break; // prune
            path.add(c[i]);
            backtrack(c, remain - c[i], i, path, res); // reuse allowed
            path.remove(path.size()-1);
        }
    }
    public static void main(String[] args) {
        System.out.println(new CombinationSum().combinationSum(new int[]{2,3,6,7},7));
        System.out.println(new CombinationSum().combinationSum(new int[]{2,3,5},8));
    }
}
```

## Edge Cases
- target smaller than smallest candidate ⇒ empty list.
- Single candidate divides target ⇒ one combination of repetitions.
- Large target ⇒ deeper recursion; ensure no stack overflow (Java fine for typical constraints).

## Summary
Use backtracking with sorted candidates and an index parameter to build combinations without duplicates, pruning when remaining target negative or candidate exceeds remaining.
