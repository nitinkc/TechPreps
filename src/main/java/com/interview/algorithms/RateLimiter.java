package com.interview.algorithms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple token-bucket rate limiter (per identifier).
 * refillRate is tokens per second, capacity is max tokens.
 */
public class RateLimiter {

  private final long capacity;
  private final long refillRatePerSecond;
  private final Map<String, Bucket> buckets;

  public RateLimiter(long capacity, long refillRatePerSecond) {
    this.capacity = capacity;
    this.refillRatePerSecond = refillRatePerSecond;
    this.buckets = new ConcurrentHashMap<>();
  }

  public boolean isAllowed(String identifier) {
    Bucket bucket = buckets.computeIfAbsent(identifier,
        k -> new Bucket(capacity, refillRatePerSecond, System.nanoTime()));
    return bucket.tryConsume();
  }
}