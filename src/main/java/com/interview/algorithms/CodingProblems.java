package com.interview.algorithms;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Lightweight test harness that demonstrates the refactored algorithm classes.
 * The heavy implementations were moved to individual files for maintainability.
 */
public class CodingProblems {

    public static void main(String[] args) {
        // LRU Cache demo
        System.out.println("Testing LRU Cache:");
        LRUCache cache = new LRUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println("Get 1: " + cache.get(1)); // 1
        cache.put(3, 3); // evicts key 2
        System.out.println("Get 2: " + cache.get(2)); // -1

        // Rate Limiter demo
        System.out.println("\nTesting Rate Limiter:");
        RateLimiter limiter = new RateLimiter(5, 1); // capacity 5, refill 1 token/sec
        String user = "user123";
        for (int i = 0; i < 7; i++) {
            System.out.println("Request " + (i+1) + ": " + (limiter.isAllowed(user) ? "Allowed" : "Blocked"));
        }

        // Banking system demo
        System.out.println("\nTesting Banking System:");
        BankingSystem bank = new BankingSystem();
        bank.deposit("user1", 1000);
        bank.deposit("user2", 1500);
        bank.deposit("user3", 800);
        System.out.println("Top 2 customers: " + bank.getTopKCustomers(2));

        // Text utilities demo
        System.out.println("\nTesting K Most Frequent Words:");
        String paragraph = "the quick brown fox jumps over the lazy dog the fox is quick";
        List<String> topWords = TextUtils.findKMostFrequentWords(paragraph, 3);
        System.out.println("Top 3 words: " + topWords);

        // 90th percentile demo
        System.out.println("\nTesting 90th Percentile:");
        List<Integer> responseTimes = Arrays.asList(100,200,300,400,500,600,700,800,900,1000);
        double p90 = PercentileCalculator.calculate90thPercentile(responseTimes);
        System.out.println("90th percentile: " + p90);

        // Poles sequence demo
        System.out.println("\nTesting Poles Sequence:");
        int[] heights = {1,2,4,8,3};
        System.out.println("Has x,2x,4x: " + PolesSequence.hasX2X4(heights));

        // BST demo
        System.out.println("\nTesting BST inorder traversal:");
        BST<Integer> bst = new BST<>();
        bst.insert(5); bst.insert(3); bst.insert(7); bst.insert(1);
        System.out.println(bst.inorderTraversal());

        // Linked list reverse demo
        System.out.println("\nTesting LinkedList reverse:");
        ListNode n1 = new ListNode(1, new ListNode(2, new ListNode(3)));
        ListNode rev = LinkedListUtils.reverseIterative(n1);
        printList(rev);
    }

    private static void printList(ListNode head) {
        List<Integer> vals = new ArrayList<>();
        ListNode cur = head;
        while (cur != null) { vals.add(cur.val); cur = cur.next; }
        System.out.println(vals);
    }
}
