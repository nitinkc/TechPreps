package com.interview.algorithms;

/**
 * Simple token bucket used by {@link RateLimiter}.
 */
public class Bucket {
  // Maximum tokens the bucket can hold.
  private final long capacity;

  // Refill rate in tokens per second.
  private final long refillRatePerSecond;

  // Current available tokens. Guarded by intrinsic lock.
  private long tokens;

  // Last refill timestamp in nanoseconds. Guarded by intrinsic lock.
  private long lastRefillTimeNanos;

  /*
   * Package-private constructor: created by RateLimiter in the same package.
   * initialTokens is set to capacity so new buckets start full.
   */
  Bucket(long capacity, long refillRatePerSecond, long nowNanos) {
    this.capacity = capacity;
    this.refillRatePerSecond = refillRatePerSecond;
    this.tokens = capacity;
    this.lastRefillTimeNanos = nowNanos;
  }

  /**
   * Try to consume a single token. Returns true if a token was available.
   */
  synchronized boolean tryConsume() {
    refill();
    if (tokens > 0) {
      tokens--;
      return true;
    }
    return false;
  }

  // Refill tokens based on elapsed time since last refill.
  private void refill() {
    long now = System.nanoTime();
    long nanosPassed = now - lastRefillTimeNanos;
    if (nanosPassed <= 0) {
      return;
    }
    long tokensToAdd = (nanosPassed * refillRatePerSecond) / 1_000_000_000L;
    if (tokensToAdd > 0) {
      tokens = Math.min(capacity, tokens + tokensToAdd);
      lastRefillTimeNanos = now;
    }
  }
}
