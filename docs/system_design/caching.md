# Caching (Cheat-sheet)

Summary
- Purpose: Reduce latency and backend load by storing frequently-read data closer to the consumer.

Types of caches
- Client-side (browser/mobile), CDN (edge caching), reverse-proxy (Varnish/CDN), in-memory app cache (Redis, Memcached), and DB-level caching.

Cache patterns
- Cache-aside (lazy): app checks cache, on miss reads DB and populates cache.
- Read-through / Write-through: cache handles reads/writes and keeps data current.
- Write-behind: write to cache and asynchronously persist to DB.

Key considerations
- Eviction policies (LRU, TTL), cache invalidation strategies (time-based, event-based, versioned keys), cache warming, and cache stampede protection (locks, request coalescing, jitter).
- Consistency trade-offs: stale reads vs lower latency.

Interview points
- Explain cache invalidation strategies and how to prevent a stampede when a hot key expires.

## Example: Redis cache-aside (Java - Jedis)

```java
// Maven dependency: redis.clients:jedis
import redis.clients.jedis.Jedis;
import com.fasterxml.jackson.databind.ObjectMapper;

Jedis jedis = new Jedis("localhost", 6379);
ObjectMapper mapper = new ObjectMapper();

public User getUser(long userId) throws Exception {
    String key = "user:" + userId;
    String cached = jedis.get(key);
    if (cached != null) {
        return mapper.readValue(cached, User.class);
    }
    // simulate DB fetch
    User user = db.fetchUser(userId);
    jedis.setex(key, 60, mapper.writeValueAsString(user));
    return user;
}
```

## Preventing cache stampede (basic lock - Java/Jedis)

```java
String lockKey = "lock:" + key;
String ok = jedis.set(lockKey, "1", SetParams.setParams().nx().ex(5));
if ("OK".equals(ok)) {
    try {
        String value = db.fetch();
        jedis.set(key, value);
    } finally {
        jedis.del(lockKey);
    }
} else {
    // wait and retry or return stale cache
    Thread.sleep(50);
}
```
