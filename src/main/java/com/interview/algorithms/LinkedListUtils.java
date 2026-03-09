package com.interview.algorithms;

public class LinkedListUtils {
    public static ListNode reverseIterative(ListNode head) {
        ListNode prev = null;
        ListNode current = head;
        while (current != null) {
            ListNode nextTemp = current.next;
            current.next = prev;
            prev = current;
            current = nextTemp;
        }
        return prev;
    }

    public static ListNode reverseRecursive(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode p = reverseRecursive(head.next);
        head.next.next = head;
        head.next = null;
        return p;
    }
}

