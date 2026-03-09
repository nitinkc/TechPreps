package com.interview.algorithms;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TextUtilsTest {

    @Test
    public void testFindKMostFrequentWords() {
        String paragraph = "apple banana apple orange banana apple";
        List<String> top2 = TextUtils.findKMostFrequentWords(paragraph, 2);
        assertEquals(2, top2.size());
        assertEquals("apple", top2.get(0));
        assertTrue(top2.contains("banana"));
    }
}

