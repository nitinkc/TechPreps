# System Design (Essentials)

Summary
- High-level approach: clarify requirements, define constraints (scale, latency, cost), identify major components, and draw data flow and interaction diagrams.
- See detailed messaging trade-offs (Kafka vs NATS vs Event Hubs) in `messaging_kafka_nats_eventhub.md`.

Checklist for interviews
- Requirements: functional, non-functional (SLAs), scale targets, data retention.
- Components: API layer, processing layer, storage, caches, messaging, and monitoring.
- Trade-offs: consistency vs availability, complexity vs time-to-market, cost vs performance.

Common patterns
- Load balancers, stateless services, message queues for decoupling, partitioning/sharding for scale, and caches for latency.
- Data pipelines for analytics, event sourcing for auditability, and CQRS for separating read/write concerns.
- Selecting a messaging backbone influences delivery semantics (NATS = ultra-low latency ephemeral, Kafka/Event Hubs = durable ordered partitions) and ecosystem (stream processing, schema registry).

How to present
- Start with a simple design, then iterate to add redundancy, scaling, caching, and failure handling. Use diagrams and justify choices.

## Example: Simple scalable URL shortener (high-level)

Components
- API Gateway (ingress)
- Shortener service (stateless) behind a load balancer
- Database for mapping (sharded or partitioned for scale)
- Cache (Redis) for hot lookups
- Async job queue for analytics processing

Data flow (simplified)
1. Client POST /shorten -> API Gateway -> Shortener service
2. Shortener generates key, writes to DB, returns short URL
3. GET /{key} -> Shortener checks Redis cache -> if miss, read DB and populate cache -> redirect

Trade-offs
- Use DB with high write throughput; use consistent hashing or user-based sharding to distribute keys.
- Cache popular keys to reduce DB reads and lower latency.

## Example: Minimal API sketch (Java - Spring Boot)

```java
// Spring Boot controller (simplified)
// Maven deps: org.springframework.boot:spring-boot-starter-web
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RestController
public class ShortenerController {
    private final UrlRepository db; // assume injected repository
    private final CacheService cache; // simple cache wrapper

    public ShortenerController(UrlRepository db, CacheService cache) {
        this.db = db;
        this.cache = cache;
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shorten(@RequestBody Map<String,String> body) {
        String url = body.get("url");
        String id = generateId(); // base62 or sequence
        db.insert(id, url);
        return ResponseEntity.ok(Map.of("short", "https://short.example/" + id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> redirect(@PathVariable String id) {
        String cacheKey = "url:" + id;
        String url = cache.get(cacheKey);
        if (url == null) {
            url = db.lookup(id);
            if (url != null) cache.set(cacheKey, url);
        }
        if (url == null) return ResponseEntity.notFound().build();
        return ResponseEntity.status(302).header("Location", url).build();
    }

    private String generateId() {
        return Long.toString(System.currentTimeMillis(), 36); // simple example
    }
}
```
