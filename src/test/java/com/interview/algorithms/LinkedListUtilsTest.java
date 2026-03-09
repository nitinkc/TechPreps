package com.interview.algorithms;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LinkedListUtilsTest {

    @Test
    public void testReverseIterativeAndRecursive() {
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, null)));
        ListNode revIter = LinkedListUtils.reverseIterative(head);
        assertNotNull(revIter);
        assertEquals(3, revIter.val);
        assertEquals(2, revIter.next.val);
        assertEquals(1, revIter.next.next.val);

        // build again for recursive
        head = new ListNode(1, new ListNode(2, new ListNode(3, null)));
        ListNode revRec = LinkedListUtils.reverseRecursive(head);
        assertNotNull(revRec);
        assertEquals(3, revRec.val);
        assertEquals(2, revRec.next.val);
        assertEquals(1, revRec.next.next.val);
    }
}

