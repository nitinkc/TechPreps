# L2 Technical Interview Preparation Guide (Structured Q&A Edition)

---

## Master Category Index

- **A. Resilience & Distributed Patterns**
- **B. Cloud & Data Ingestion**
- **C. Microservices Architecture & Security**
- **D. Caching & Data Access**
- **E. Databases & Migrations**
- **F. Performance & Observability**
- **G. Concurrency & JVM**
- **H. Reliability & Troubleshooting**
- **I. System Design & Large Scale**
- **J. Search & Analytics**
- **K. Coding & Algorithms**
- **L. Behavioral & Process**
- **M. API Design & Governance**
- **N. Miscellaneous / Tooling**

---

## A. Resilience & Distributed Patterns

**Q: What is the Circuit Breaker pattern and its states?**  
A: Protects system from cascading failures by monitoring call success/failure. **States:** Closed (normal), Open (fail fast), Half-Open (trial). Transitions based on failure threshold & time window.

**Q: How to implement Circuit Breaker in Spring?**  
A: Use Resilience4j: 
```java
@CircuitBreaker(name="service", fallbackMethod="fallback")
```
plus config in `application.yml`. Combine with `@Retryable` and metrics (Micrometer).

**Q: Key annotations?**  
A: `@CircuitBreaker`, `@Retryable`, `@Bulkhead`, `@RateLimiter`, `@TimeLimiter`, fallback via `@Recover` or method parameter.

**Q: What is Idempotency? Have you handled it?**  
A: Repeating an operation with the same input yields same end state. **Techniques:** idempotency keys, unique DB constraints, state inspection before update, Outbox pattern for events, storing initial result to return on retries.

**Q: Define SAGA pattern (choreography vs orchestration).**  
A: Distributed transaction via local steps + compensations. **Choreography:** services emit/consume events (loose coupling, harder to reason at scale). **Orchestration:** central coordinator directs flow (easier tracing, single coordination point).

➡️ [Deep dive: Circuit Breaker](system_design/circuit_breaker.md) | [SAGA Pattern](system_design/saga_pattern.md)

---

## B. Cloud & Data Ingestion

**Q: How to ingest large data in AWS?**  
A: Streaming: Kinesis Streams/Firehose → S3/Redshift; Messaging: SQS/SNS; ETL: Glue; Compute: Lambda/EMR; Storage: S3 as landing zone; Catalog: Glue Data Catalog.

**Q: GCP ingestion equivalent services?**  
A: Pub/Sub (events), Dataflow (Beam batch/stream processing), Cloud Storage (landing), BigQuery (analytics), Dataproc (Spark/Hadoop).

**Q: Terraform & sample usage?**  
A: Declarative IaC, multi-provider, plan/apply diff, modules for reuse, remote state locking.

➡️ [Deep dive: Data Ingestion](system_design/data_ingestion.md) | [Terraform](system_design/terraform.md)

---

## C. Microservices Architecture & Security

**Q: Improve API performance?**  
A: Caching, query optimization (indexes, projections), async offload (queues), compression, connection pooling, protocol upgrades (HTTP/2/gRPC), resilience patterns, metrics/tracing for p95/p99.

**Q: Secure microservices?**  
A: Edge auth (OAuth2/OIDC), JWT validation, mTLS intra-service, RBAC + Row Level Security, secret rotation (Vault/SSM), centralized auditing.

**Q: Communication methods?**  
A: Synchronous (REST/gRPC) for request/response; asynchronous (Kafka/SQS) for event-driven, decoupling & resilience.

➡️ [Deep dive: API Performance](system_design/api_performance_microservices.md) | [Security](system_design/security.md) | [Service Discovery](system_design/service_discovery.md)

---

## D. Caching & Data Access

**Q: Caching algorithms & use cases?**  
A: LRU (temporal locality), LFU (frequency skew), FIFO (simple), TTL (expiry). Choose by access pattern & churn.

**Q: When implement a cache?**  
A: High read repetition, expensive computation, stable data. Avoid on low reuse, high invalidation, strong immediate consistency needs.

**Q: Cache write strategies?**  
A: Write-through (sync DB update), Write-behind (async DB persistence), Write-around (write only to DB; load to cache on read miss).

➡️ [Deep dive: Caching](system_design/caching.md)

---

## E. Databases & Migrations

**Q: SQL vs NoSQL scenarios?**  
A: SQL for ACID multi-row transactions & complex joins; NoSQL for flexible schema, horizontal scale, denormalized aggregates, high write throughput.

**Q: Partitioning vs Sharding?**  
A: Partitioning is **logical data subdivision** (often within a single database instance). Sharding **distributes** data across multiple **independent physical servers/nodes**. 

| Aspect | Partitioning | Sharding | Clustering |
|--------|-------------|----------|------------|
| Scope | Intra-instance (logical table split) | Cross-instance (data across servers) | Grouping of nodes for HA/replication |
| Purpose | Manageability, pruning, performance | Horizontal scale (data & throughput) | Availability, failover, read-scaling |
| Transaction semantics | Local ACID (single instance) | May require distributed transactions | Depends on DB |

➡️ [Deep dive: Database Design](system_design/database_design.md)

---

## F. Performance & Observability

**Q: Improve API performance & optimize server response?**  
A: Baseline metrics; remove N+1 queries; add caching, efficient pagination, reduce payload size, compression, async work queue, tune connection pools.

**Q: Percentile (90th) calculation approach?**  
A: Sort list; index = 0.9*(n-1); linear interpolation for fractional index. For streaming large data: dual heap or quantile sketch (t-digest).

**Q: p99 latency tripled with no deployments—debug plan?**  
A: Check resource saturation (CPU, GC pauses), downstream latency (traces), cache hit drop, DB slow queries/plan change, network retransmits.

➡️ [Deep dive: Performance Optimization](system_design/performance_optimization.md) | [Error Handling & Debugging](system_design/error_handling_debugging.md)

---

## G. Concurrency & JVM

**Q: Two requests at identical time hitting one instance?**  
A: Separate threads from server pool; ensure thread-safe components; avoid shared mutable state or synchronize appropriately.

**Q: Memory allocation & heap config?**  
A: `-Xms` sets initial heap, `-Xmx` max; TLAB for fast per-thread allocation; GC (G1/ZGC) reclaim. Tune for latency vs throughput using metrics.

**Q: Ensuring consistency in multithreading?**  
A: Immutability, atomic operations, locks, concurrent collections, synchronization boundaries, idempotent side effects.

---

## H. Reliability & Troubleshooting

**Q: Pipeline green but upstream 503/500—first steps?**  
A: Confirm new container version & readiness; verify service discovery/LB registration; check network/security groups; then inspect traces/log correlation; rollback if SLO breach persists.

**Q: Batch job fails silently—debug steps?**  
A: Examine scheduler logs, enable failure listeners, add heartbeat/progress metrics, DLQ for bad records, check external dependency health.

➡️ [Deep dive: Error Handling & Debugging](system_design/error_handling_debugging.md)

---

## I. System Design & Large Scale

**Q: Design large distributed system (Facebook-like).**  
A: Components: CDN, global LB, API gateway (auth, rate limit), microservices (user, post, feed, notifications), message bus (Kafka), cache (Redis), search (Elasticsearch), DB (SQL + NoSQL), stream processing (Flink/Beam).

**Q: Database scaling for growth?**  
A: Vertical first (optimize queries), read replicas, partitioning, sharding, caching, CQRS separation, asynchronous writes.

➡️ [Deep dive: System Design](system_design/system_design.md) | [Microservices Scaling](system_design/microservices_scaling.md)

---

## J. Search & Analytics

**Q: Elasticsearch queries & why chosen?**  
A: Full-text relevance, complex boolean queries, aggregations (terms, histogram, metrics), horizontal scale. Typical queries: `match`, `term`, `range`, `bool`, nested, aggregations.

---

## K. Coding & Algorithms

➡️ **[Full Coding Questions Index](algo/coding_questions.md)**

Key topics covered:

- LRU Cache, Rate Limiter, Banking System (top-K)
- Graph algorithms (Dijkstra, BFS, DFS)
- Dynamic programming & Backtracking
- Linked List operations
- Tree traversals
- Sliding window techniques

---

## L. Behavioral & Process

**Q: Disagreement with teammate/stakeholder?**  
A: Align on measurable criteria (latency, cost); present pros/cons; data-driven decision; document outcome; retrospective if assumptions fail.

**Q: Prioritizing multiple urgent issues?**  
A: Triage by user impact & SLO breach; handle high severity first; delegate/parallelize; maintain incident timeline.

---

## M. API Design & Governance

**Q: Explain RESTful API?**  
A: Resource-oriented, stateless, uniform interface (HTTP verbs, URIs), representations (JSON), layered system, cacheable responses.

**Q: Best practices followed?**  
A: Consistent naming, proper status codes, pagination (cursor), filtering, HATEOAS optionally, validation errors structured, idempotent PUT/DELETE, security (auth scopes), rate limiting, tracing IDs.

**Q: Handle API versions & backward compatibility?**  
A: Version in URI (/v1), additive changes only; deprecation headers & usage metrics; maintain old version until usage below cutoff; contract tests.

---

## N. Miscellaneous / Tooling

**Q: Messaging systems used?**  
A: RabbitMQ (work queues, routing), Kafka (streaming, event sourcing, high throughput, retention).

➡️ [Deep dive: Messaging (Kafka, NATS, Event Hub)](system_design/messaging_kafka_nats_eventhub.md)

---

## Revision Strategy Tip

Study order: 

1. Resilience & Security
2. Data & Performance  
3. System Design
4. Coding patterns
5. Behavioral

Use p99 latency triage & SAGA flows as integration topics.

