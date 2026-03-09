# 5. Subarray Product < k

## Learning Targets
- Apply sliding window for multiplicative constraint.
- Handle shrinking window logic correctly.
- Analyze complexity.

## Problem
Given nums and k, count contiguous subarrays whose product < k.
Example: nums = [10,5,2,6], k=100
Valid subarrays: [10],[5],[2],[6],[10,5],[5,2],[2,6],[5,2,6] ⇒ count = 8.

## Constraints/Assumptions
- Positive integers (critical for sliding window monotonicity). If zeros or negatives present, logic changes.
- k > 1, else answer 0 (no positive product < 1).

## Approach (Sliding Window)
Maintain window [left..right] and current product. Expand right; while product ≥ k shrink from left. Each time product < k, all subarrays ending at right and starting from any index in [left..right] are valid ⇒ add (right - left + 1) to answer.

## Complexity
Time: O(n) (each index enters/exits window once). Space: O(1).

## Java Implementation
```java
public class SubarrayProductLessThanK {
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1) return 0;
        long prod = 1;
        int left = 0, count = 0;
        for (int right = 0; right < nums.length; right++) {
            prod *= nums[right];
            while (prod >= k && left <= right) {
                prod /= nums[left++];
            }
            count += right - left + 1;
        }
        return count;
    }
    public static void main(String[] args) {
        System.out.println(new SubarrayProductLessThanK()
            .numSubarrayProductLessThanK(new int[]{10,5,2,6}, 100)); // 8
    }
}
```

## Edge Cases
- k ≤ 1 ⇒ 0.
- Single element < k ⇒ count increments by 1.
- Large products: use long to avoid overflow if nums[i] can be large.

## Summary
The sliding window leverages positivity to maintain a minimal left bound whose product stays < k; each step counts new valid endings.
