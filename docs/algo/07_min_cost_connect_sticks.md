# 7. Minimum Cost to Connect Sticks

## Learning Targets
- Recognize greedy pattern minimizing pairwise sum cost.
- Use priority queue (min-heap).
- Prove optimality via Huffman-like argument.

## Problem
Combine sticks; cost of joining two = sum lengths. Goal: minimal total cost to end with one stick.
Example: 1 2 3
Optimal: (1+2)=3, cost 3; (3+3)=6, total 9.

## Approach (Greedy)
Always join two shortest sticks first → minimize incremental cost that propagates into future sums.
This is identical to building a Huffman tree (optimal merging).

## Algorithm
1. Insert all lengths into min-heap.
2. totalCost = 0.
3. While heap size > 1:
   - a = pop min, b = pop min
   - sum = a + b
   - totalCost += sum
   - push sum back
4. Return totalCost.

## Complexity
Let n sticks.
- Time: O(n log n)
- Space: O(n)

## Java Implementation
```java
public class ConnectSticks {
    public int minCost(int[] sticks) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int s: sticks) pq.offer(s);
        int cost = 0;
        while (pq.size() > 1) {
            int a = pq.poll();
            int b = pq.poll();
            int sum = a + b;
            cost += sum;
            pq.offer(sum);
        }
        return cost;
    }
    public static void main(String[] args) {
        System.out.println(new ConnectSticks().minCost(new int[]{1,2,3})); // 9
    }
}
```

## Optimality Brief Proof
Suppose we don’t pick two smallest; swapping choices always reduces later cumulative addition because sums propagate. Exchange argument confirms greedy is optimal.

## Edge Cases
- Single stick ⇒ cost 0.
- Large numbers ⇒ use long if near int overflow.

## Summary
Use a min-heap to repeatedly merge two shortest sticks, accumulating cost.
