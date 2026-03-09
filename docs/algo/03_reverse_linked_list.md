# 3. Reverse a Singly Linked List

## Learning Targets
- Understand pointer manipulation in a singly linked list.
- Implement iterative and recursive reversal.
- Analyze complexity and stack implications.

## Problem
Input: 1 -> 2 -> 3 -> null
Output: 3 -> 2 -> 1 -> null

## Iterative Approach
Maintain three pointers: prev, curr, next.
Algorithm:
1. prev = null, curr = head
2. While curr != null:
   - next = curr.next
   - curr.next = prev
   - prev = curr
   - curr = next
3. prev is new head.

Time: O(n), Space: O(1).

## Recursive Approach
Recurse to tail; on unwind set head.next.next = head and head.next = null.
Time: O(n), Space: O(n) call stack.

## Java Implementation (Iterative)
```java
class ListNode {int val; ListNode next; ListNode(int v){val=v;}}

public class ReverseList {
    public static ListNode reverse(ListNode head) {
        ListNode prev = null, curr = head;
        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }
    // Utility for testing
    public static String toString(ListNode h){
        StringBuilder sb=new StringBuilder();
        while(h!=null){sb.append(h.val); if(h.next!=null) sb.append("->"); h=h.next;}
        return sb.toString();
    }
    public static void main(String[] args) {
        ListNode a=new ListNode(1); a.next=new ListNode(2); a.next.next=new ListNode(3);
        ListNode rev = reverse(a);
        System.out.println(toString(rev)); // 3->2->1
    }
}
```

## Common Mistakes
- Losing rest of list (forget to store next before rewiring).
- Returning wrong pointer (should return prev after loop).

## Summary
Reverse by iteratively redirecting next pointers. Iterative is optimal for space; recursive more elegant but uses stack.
