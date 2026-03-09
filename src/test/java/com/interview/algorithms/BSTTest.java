package com.interview.algorithms;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BSTTest {

    @Test
    public void testInsertContainsAndInorder() {
        BST<Integer> bst = new BST<>();
        bst.insert(5);
        bst.insert(3);
        bst.insert(7);
        bst.insert(1);
        bst.insert(4);

        assertTrue(bst.contains(5));
        assertTrue(bst.contains(1));
        assertFalse(bst.contains(2));

        List<Integer> inorder = bst.inorderTraversal();
        assertArrayEquals(new Integer[]{1,3,4,5,7}, inorder.toArray(new Integer[0]));
    }

    @Test
    public void testDeleteLeafAndNodeWithOneOrTwoChildren() {
        BST<Integer> bst = new BST<>();
        bst.insert(5);
        bst.insert(3);
        bst.insert(7);
        bst.insert(2);
        bst.insert(4);
        bst.insert(6);
        bst.insert(8);

        // delete a leaf
        bst.delete(2);
        assertFalse(bst.contains(2));

        // delete node with one child
        bst.delete(3); // 3 had child 4
        assertFalse(bst.contains(3));

        // delete node with two children
        bst.delete(7);
        assertFalse(bst.contains(7));

        List<Integer> inorder = bst.inorderTraversal();
        assertArrayEquals(new Integer[]{4,5,6,8}, inorder.toArray(new Integer[0]));
    }
}

