package com.interview.algorithms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RateLimiterTest {

    @Test
    public void testSimpleRateLimiter() {
        RateLimiter limiter = new RateLimiter(2, 1); // capacity 2
        String id = "u1";
        assertTrue(limiter.isAllowed(id));
        assertTrue(limiter.isAllowed(id));
        assertFalse(limiter.isAllowed(id));
    }
}

