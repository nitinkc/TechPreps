package com.interview.algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple generic Binary Search Tree implementation.
 * Supports insert, contains (search), delete and inorder traversal.
 *
 * Usage: BST<Integer> bst = new BST<>();
 */
public class BST<T extends Comparable<? super T>> {
    private Node<T> root;

    /** Insert a value into the BST. Duplicates are ignored. */
    public void insert(T value) {
        if (value == null) return;
        root = insertRec(root, value);
    }

    private Node<T> insertRec(Node<T> node, T value) {
        if (node == null) return new Node<>(value);
        int cmp = value.compareTo(node.value);
        if (cmp < 0) node.left = insertRec(node.left, value);
        else if (cmp > 0) node.right = insertRec(node.right, value);
        // duplicate: do nothing
        return node;
    }

    /** Returns true if the value exists in the tree. */
    public boolean contains(T value) {
        return searchRec(root, value);
    }

    private boolean searchRec(Node<T> node, T value) {
        if (node == null || value == null) return false;
        int cmp = value.compareTo(node.value);
        if (cmp == 0) return true;
        return cmp < 0 ? searchRec(node.left, value) : searchRec(node.right, value);
    }

    /** Delete a value from the tree if present. */
    public void delete(T value) {
        if (value == null) return;
        root = deleteRec(root, value);
    }

    private Node<T> deleteRec(Node<T> node, T value) {
        if (node == null) return null;
        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = deleteRec(node.left, value);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, value);
        } else {
            // node to delete
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            // node with two children: get inorder successor (smallest in right)
            Node<T> successor = minNode(node.right);
            node.value = successor.value;
            node.right = deleteRec(node.right, successor.value);
        }
        return node;
    }

    private Node<T> minNode(Node<T> node) {
        Node<T> curr = node;
        while (curr.left != null) curr = curr.left;
        return curr;
    }

    /**
     * Returns the inorder traversal (sorted order) of the BST as a list.
     */
    public List<T> inorderTraversal() {
        List<T> result = new ArrayList<>();
        inorderRec(root, result);
        return result;
    }

    private void inorderRec(Node<T> node, List<T> result) {
        if (node != null) {
            inorderRec(node.left, result);
            result.add(node.value);
            inorderRec(node.right, result);
        }
    }

    /** Internal node class. */
    private static class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;

        Node(T value) { this.value = value; }
    }
}
