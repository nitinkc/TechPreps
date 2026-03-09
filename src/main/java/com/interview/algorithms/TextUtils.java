package com.interview.algorithms;

import java.util.*;

public class TextUtils {
    /**
     * K Most Frequent Words in a paragraph.
     */
    public static List<String> findKMostFrequentWords(String paragraph, int k) {
        if (paragraph == null || paragraph.isEmpty() || k <= 0) return Collections.emptyList();

        String[] words = paragraph.toLowerCase()
                .replaceAll("[^a-zA-Z\\s]", "")
                .split("\\s+");

        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String word : words) {
            if (!word.isEmpty()) frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
        }

        PriorityQueue<Map.Entry<String, Integer>> heap = new PriorityQueue<>((a, b) -> {
            if (a.getValue().equals(b.getValue())) return b.getKey().compareTo(a.getKey());
            return a.getValue() - b.getValue();
        });

        for (Map.Entry<String, Integer> e : frequencyMap.entrySet()) {
            heap.offer(e);
            if (heap.size() > k) heap.poll();
        }

        List<String> result = new ArrayList<>();
        while (!heap.isEmpty()) result.add(0, heap.poll().getKey());
        return result;
    }
}

