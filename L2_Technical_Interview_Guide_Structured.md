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

**Q: Non-atomic cross-service write (comment service A success even if B down) but near real-time availability?**  
A: Use Outbox pattern + CDC (Debezium) to broker (Kafka). Service B consumes and updates index/cache. Avoid synchronous dependency; latency typically ms-scale.

**Q: Guarantee message available within 1 ms?**  
A: True sub-ms across services is unrealistic except within same process or shared memory; design for low single-digit ms: co-locate in same AZ, minimize serialization (Avro/Protobuf), dedicated topic partitions, fast consumer thread.

**Q: Handling downstream services returning 500 to upstream?**  
A: Triage: identify failing endpoints, activate circuit breaker, check dependency health, gather traces, rollback or hotfix; add synthetic canaries & load shedding.

**Q: Cache failed weeks later—resolution?**  
A: Monitor miss rate, sentinel/cluster failover, warm rebuild (bulk prefetch tasks), fallback logic to DB, repair or flush inconsistent segments, root-cause (OOM/network).

**Q: Global API rate limiter design & UUID role?**  
A: Token Bucket/Leaky Bucket. UUID (userID/apiKey) is identity for quota tracking. Use Redis atomic LUA script; multi-region: local buckets + eventual sync or CRDT counters; expose API: request {identifier, rate, perSeconds} → response {allowed, remaining}.

**Q: Difference between runtime error and internal server error?**  
A: Runtime error = code-level exception. Internal server error = HTTP 500 returned to client (often from uncaught runtime error). Map known exceptions to specific status codes to reduce 500s.

---
## B. Cloud & Data Ingestion

**Q: How to ingest large data in AWS?**  
A: Streaming: Kinesis Streams/Firehose → S3/Redshift; Messaging: SQS/SNS; ETL: Glue; Compute: Lambda/EMR; Storage: S3 as landing zone; Catalog: Glue Data Catalog. Architecture: Producers → Kinesis → Lambda (transform) → S3 → Glue → Warehouse/Analytics.

**Q: Flink pipeline error handling?**  
A: Restart strategies (fixed delay, failure rate), checkpoints/savepoints for state, side outputs for DLQ. Alerting on repeated restarts.

**Q: GCP ingestion equivalent services?**  
A: Pub/Sub (events), Dataflow (Beam batch/stream processing), Cloud Storage (landing), BigQuery (analytics), Dataproc (Spark/Hadoop).

**Q: Terraform & sample usage?**  
A: Declarative IaC, multi-provider, plan/apply diff, modules for reuse, remote state locking. *See original file Terraform section for code.*

**Q: Keeping services up to date across regions?**  
A: Central module/BOM, semantic versioning, dependency scanning (Dependabot), staged regional canaries, observability & SLO gating for rollout.

**Q: AWS vs Azure differences?**  
A: AWS broad maturity & ecosystem; Azure tight enterprise integration (AD, Office, hybrid). Choose based on existing identity stack & governance tooling.

**Q: Restrict traffic to region via load balancer?**  
A: Use geo DNS (Route53 latency/geolocation), WAF geo blocking, IP allowlists, LB listener rules with geolocation metadata.

---
## C. Microservices Architecture & Security

**Q: Improve API performance?**  
A: Caching, query optimization (indexes, projections), async offload (queues), compression, connection pooling, protocol upgrades (HTTP/2/gRPC), resilience patterns, metrics/tracing for p95/p99.

**Q: Secure microservices?**  
A: Edge auth (OAuth2/OIDC), JWT validation, mTLS intra-service, RBAC + Row Level Security, secret rotation (Vault/SSM), centralized auditing.

**Q: Communication methods?**  
A: Synchronous (REST/gRPC) for request/response; asynchronous (Kafka/SQS) for event-driven, decoupling & resilience.

**Q: Eureka usage & annotations?**  
A: `@EnableEurekaServer` for registry; clients auto register, `@LoadBalanced` RestTemplate/WebClient for discovery.

**Q: Application vs Method level security?**  
A: Application-level in SecurityFilterChain (URL patterns); Method-level uses `@PreAuthorize/@PostAuthorize` for domain conditions (ownership, roles).

**Q: Role vs Row Level security?**  
A: Role-based: coarse permission sets. Row-level: restrict data access to rows meeting identity predicate (tenant/user filtering).

**Q: Zero Trust vs certificate-only?**  
A: Certificate-only secures transport; Zero Trust enforces continuous authZ, mutual TLS, least privilege, telemetry & micro-segmentation.

**Q: Dependency Injection example?**  
A: Spring IoC container wires beans; controllers/services declare dependencies via constructor/field injection for decoupling & testability.

**Q: Implement secure authentication in microservices?**  
A: Central IdP issues JWT/OIDC tokens; gateway performs token validation; services verify scopes/roles; rotate keys (JWKS); enforce mTLS for service-to-service.

---
## D. Caching & Data Access

**Q: Caching algorithms & use cases?**  
A: LRU (temporal locality), LFU (frequency skew), FIFO (simple), TTL (expiry). Choose by access pattern & churn.

**Q: Difference cache lookup vs DB search?**  
A: Cache O(1) in-memory; DB involves index traversal + potential disk IO (ms latency).

**Q: When implement a cache?**  
A: High read repetition, expensive computation, stable data. Avoid on low reuse, high invalidation, strong immediate consistency needs.

**Q: Cache write strategies?**  
A: Write-through (sync DB update), Write-behind (async DB persistence), Write-around (write only to DB; load to cache on read miss).

**Q: After modifying cache need DB?**  
A: Depends: write-through updates DB now; write-behind schedules async flush; for derived ephemeral data DB update may not be needed.

**Q: LRU vs LFU choice?**  
A: LRU simpler; LFU better when few keys dominate access. Hybrid (TinyLFU) improves accuracy with low overhead.

---
## E. Databases & Migrations

**Q: SQL vs NoSQL scenarios?**  
A: SQL for ACID multi-row transactions & complex joins; NoSQL for flexible schema, horizontal scale, denormalized aggregates, high write throughput.

**Q: Single deciding factor?**  
A: Need for strict relational integrity across entities.

**Q: Indexing differences (MySQL vs MongoDB)?**  
A: MySQL B+Tree & composite indexes (leftmost prefix). Mongo indexes on fields (including nested/arrays), plus text/geo/hashed.

**Q: Batch limits (Cosmos/Postgres) & `saveAll`?**  
A: Cosmos transactional batch limited by partition key & payload (~100 ops/≤2MB). Postgres limited by statement size/memory. Chunk large lists (recommended batch size: 100–1000 items).

**Q: How indexing impacts performance?**  
A: Speeds reads (fewer scanned rows), costs extra writes & storage; over-indexing harms write throughput.

**Q: Handling database scaling?**  
A: Read replicas, partition/shard by key, caching, connection pooling, schema/denormalization for hot paths, archiving cold data.

**Q: Slow query optimization strategy?**  
A: Obtain EXPLAIN plan, add/selective indexes, reduce projection, eliminate function-wrapped indexed columns, use covering indexes, batch operations, materialized views.

**Q: Database migration production approach?**  
A: Expand (add new schema) → Migrate (backfill/data copy & dual writes) → Contract (remove old) ensuring backward compatibility & rollback path.

**Q: Handling downtime during migration?**  
A: Rolling changes, online backfill, shadow writes, traffic shifting, feature flags.

**Q: Database mutation definition?**  
A: Any state-changing operation (INSERT/UPDATE/DELETE/schema changes).

**Q: Sharding experience?**  
A: Partition data across logical shards; challenges: cross-shard joins replaced by fan-out queries; consistent hashing or range splits; rebalancing complexity.

**Q: Cosmos vs Postgres limits scenario?**  
A: Cosmos requires batching around partition key constraints & RU limits; Postgres limited by I/O and memory, tune connection pool & avoid huge single transactions.

**Q: Partitioning vs Sharding?**  
A: Partitioning is **logical data subdivision** (often within a single database instance) to improve
manageability/performance—e.g., PostgreSQL table partitioned by date; queries still executed locally with planner aware of partitions (pruning). 

[See detailed notes in Database Design](docs/system_design/database_design.md)

Sharding **distributes** data across multiple **independent physical servers/nodes** (distinct database instances) for horizontal scale. 
- Key differences: (1) Scope—partitioning: intra-instance; sharding: cross-instance. (2) Governance—partitioning keeps global ACID semantics (single transaction scope), sharding may require distributed transactions or eventual consistency across shards. (3) Rebalancing—partition moves usually metadata operations; shard rebalancing involves copying data between servers. (4) Query complexity—partitioned queries transparently optimized (partition pruning); sharded queries may fan-out to multiple shards, requiring aggregation. (5) Keys—partition key chosen for data lifecycle or pruning (e.g., date), shard key chosen for cardinality & even load distribution (e.g., userId hash). In NoSQL (MongoDB, Cassandra): term "partition key" (Cassandra) defines data placement on cluster nodes—effectively both partitioning and sharding combined; confusion stems from overloaded terminology. Mnemonic: Partitioning slices a table; Sharding slices the cluster. Choose partitioning for very large single-table management & pruning; choose sharding when a single machine can’t handle total data volume/read+write throughput.

**Q: Clustering?**  
A: Clustering refers to a group of database instances that work together as a single logical system for availability, fault-tolerance, and scale. It often combines replication, membership/coordination, and (optionally) sharding. Key points: 
- Purpose: provide High Availability (failover), read-scaling (replicas), and distribute operational load (backups, repairs).
- Node roles: primary/leader (accepts writes), secondary/followers (replicate data and serve reads), arbiters (vote but store no data), coordinators/config servers (store metadata). Some systems (Cassandra) are peer-to-peer with no single leader.
- Relation to sharding/replication: a shard typically maps to a subset of data; each shard can itself be a cluster (replicated nodes). So sharding = data partitioning across clusters; clustering = the grouped nodes that host each shard and keep replicas in sync.
- Replication factor & consistency: choose replication factor (RF) and consistency level (e.g., Cassandra's QUORUM, MongoDB's primary/secondary read preferences). Higher RF improves durability/availability at extra storage cost; consistency is tunable in many NoSQL systems (trade-offs per CAP theorem).
- Failure & rebalancing: clusters handle node failures via leader election or gossip; rebalancing (adding/removing nodes) may trigger data streaming/rehashing and temporary performance overhead.
- Examples:
  - MongoDB: a cluster = replica sets (for HA) + optional sharding (router + config servers); each shard is a replica set.
  - Cassandra: ring-based peer-to-peer cluster where data is partitioned by token ranges and replicated per RF; cluster membership is managed via gossip.
  - PostgreSQL: clustering often refers to primary-standby replication (e.g., Patroni) or multi-master overlays (less common).
- Operational notes: monitor replication lag, repair/anti-entropy, backups/restores, rolling upgrades, node provisioning, and maintenance windows.

Mnemonic to keep them straight: Partitioning slices a table; Sharding slices the cluster across machines; Clustering groups machines to host replicas and provide availability—shards often live on clusters (i.e., cluster-of-replicas per shard).

+ Q: Concrete technology examples (quick reference)
+ A: Short real-world examples showing how each concept is used in popular systems:

+ Postgres (Partitioning by date)
+ - Use case: very large time-series table (events/logs).
+ - Setup: CREATE TABLE events (...) PARTITION BY RANGE (event_date);
+   CREATE TABLE events_2025_10 PARTITION OF events FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');
+ - Behavior: queries with a date filter are pruned to the relevant partitions; all partitions live in the same DB instance (single server/process). Maintenance (drop old partitions) is metadata fast.
+ - Transactions: single-node ACID semantics; no cross-instance coordination required.

+ MongoDB (Sharding + Replica Set)
+ - Use case: large user collection where both dataset size and write throughput exceed a single server.
+ - Setup: each shard is a replica set (primary + secondaries). A mongos router routes queries based on shard key (e.g., userId hashed).
+ - Behavior: data is distributed by shard key across shard replica-sets; reads can be served from secondaries (with read preferences); failover via replica set election.
+ - Operational: resharding moves chunks between shards and streams data; config servers store metadata.

+ Cassandra (Partition Key + Peer-to-Peer Cluster)
+ - Use case: write-heavy telemetry with eventual consistency and predictable latency.
+ - Setup: table defined with a partition key (e.g., device_id) that determines token placement across the ring.
+   CREATE TABLE metrics (device_id text, ts timestamp, value double, PRIMARY KEY ((device_id), ts));
+ - Behavior: partition key determines node(s) that store the data (replication factor applies). No single leader; consistency level chosen per-query (ONE/QUORUM/ALL).
+ - Operational: adding a node requires streaming token ranges; repairs ensure anti-entropy replication.

+ Compact Comparison Table
+ | Aspect | Partitioning | Sharding | Clustering |
+ |---|---:|---:|---:|
+ | Scope | Intra-instance (logical table split) | Cross-instance (data across servers) | Grouping of nodes for HA/replication (hosts shards/replicas) |
+ | Purpose | Manageability, pruning, performance for huge tables | Horizontal scale (data & throughput) | Availability, failover, read-scaling, operational resilience |
+ | Transaction semantics | Local ACID (single instance) | May require distributed transactions or result in eventual consistency | Depends on DB (replicated single primary vs peer-to-peer) |
+ | Rebalance cost | Usually metadata + limited data move | Potential heavy data reshard/streaming between servers | Data streaming for node join/leave; auto-healing/repairs |
+ | Query routing | Transparent, planner-aware (partition pruning) | Router/fan-out (needs shard key) | Cluster-aware driver or coordinator routes requests |
+ | Example tech | PostgreSQL table partitions | MongoDB/Cassandra sharding | MongoDB replica set, Cassandra ring, Postgres primary/standby |

---
## F. Performance & Observability

**Q: Improve API performance & optimize server response?**  
A: Baseline metrics; remove N+1 queries; add caching (Redis/local), efficient pagination, reduce payload size, compression, async work queue, tune connection pools, profile CPU/GC, minimize serialization overhead.

**Q: Percentile (90th) calculation approach?**  
A: Sort list; index = 0.9*(n-1); linear interpolation for fractional index. For streaming large data: dual heap or quantile sketch (t-digest).

**Q: p99 latency tripled with no deployments—debug plan?**  
A: Check resource saturation (CPU, GC pauses), downstream latency (traces), cache hit drop, DB slow queries/plan change, network retransmits; apply targeted mitigation (scale out, ANALYZE, purge hot key thrash).

**Q: Identifying performance bottlenecks?**  
A: Use distributed tracing (span times), breakdown latency (DB vs external), capture thread dumps for contention, monitor event loop/reactor metrics.

**Q: Time complexity fundamentals?**  
A: Hash ops avg O(1); balanced tree O(log n); linear scan O(n); sorting O(n log n); heap ops O(log n); BFS/DFS O(V+E).

**Q: Difference runtime error vs internal server error?**  
A: Runtime exception vs HTTP 500 response to client; proper error mapping reduces 500 frequency.

---
## G. Concurrency & JVM

**Q: Two requests at identical time hitting one instance?**  
A: Separate threads from server pool; ensure thread-safe components; avoid shared mutable state or synchronize appropriately.

**Q: Memory allocation & heap config?**  
A: `-Xms` sets initial heap, `-Xmx` max; TLAB for fast per-thread allocation; GC (G1/ZGC) reclaim. Tune for latency vs throughput using metrics (pause times, promotion rates).

**Q: Ensuring consistency in multithreading?**  
A: Immutability, atomic operations, locks, concurrent collections, synchronization boundaries, idempotent side effects.

**Q: Java configure min/max heap?**  
A: Yes, `-Xms<size>` and `-Xmx<size>` (e.g., `-Xms512m -Xmx2g`).

---
## H. Reliability & Troubleshooting

**Q: Pipeline green but upstream 503/500—first steps?**  
A: Confirm new container version & readiness; verify service discovery/LB registration; check network/security groups; then inspect traces/log correlation; rollback if SLO breach persists.

**Q: After verifying logs & container still upstream timeouts?**  
A: Internal curl from pod, DNS resolution, mesh/sidecar metrics, connection pool saturation, LB routing rule misconfig.

**Q: Post verification next action (issue unresolved)?**  
A: Compare environment configs, test previous version (canary rollback), escalate with incident ticket & maintain timeline.

**Q: Batch job fails silently—debug steps?**  
A: Examine scheduler logs, enable failure listeners, add heartbeat/progress metrics, DLQ for bad records, check external dependency health, rerun with increased logging.

**Q: Cache drift (DB not in sync) remediation?**  
A: Detect via sampling/version stamps; invalidate compromised segment; rebuild cache from DB; add monitoring to prevent recurrence.

**Q: Rollback readiness?**  
A: Maintain previous artifact & config snapshot; automated rollback pipeline triggered by SLO threshold & error rate.

---
## I. System Design & Large Scale

**Q: Design large distributed system (Facebook-like).**  
A: Components: CDN, global LB, API gateway (auth, rate limit), microservices (user, post, feed, notifications), message bus (Kafka), cache (Redis), search (Elasticsearch), DB (SQL + NoSQL), stream processing (Flink/Beam), analytics warehouse (BigQuery/Redshift).

**Q: Load balancer & restrict traffic to region?**  
A: LB distributes requests (round robin/least connections); region restriction via geo DNS, LB routing rules, WAF geo IP filters.

**Q: Database scaling for growth?**  
A: Vertical first (optimize queries), read replicas, partitioning, sharding, caching, CQRS separation, asynchronous writes.

**Q: Caching strategy for performance?**  
A: Multi-layer (local + distributed), cache-aside for reads, write-through for consistent hot data, TTL management, stampede protection.

**Q: Functional split SQL vs NoSQL?**  
A: ACID: payments/orders/auth; NoSQL: feeds/events/logs; Search: Elasticsearch; Caching: Redis; Documents: S3/Blob storage.

**Q: Rate limiter DB schema & request/response?**  
A: Redis hash per identifier {tokens, last_refill}. Request: {identifier, rate, perSeconds}. Response: {allowed, remaining}.

**Q: Balances/payout + dynamic top-K query?**  
A: Store balances in DB + maintain sorted structure (min-heap size K or tree) for frequent top queries; payouts tracked with version markers; late joiners lack previous versions.

---
## J. Search & Analytics

**Q: Elasticsearch queries & why chosen?**  
A: Full-text relevance, complex boolean queries, aggregations (terms, histogram, metrics), horizontal scale. Typical queries: `match`, `term`, `range`, `bool`, nested, aggregations.

**Q: K most frequent words?**  
A: Normalize (lowercase, strip punctuation), count with HashMap, maintain min-heap of size K or bucket sort; optional stopword filtering.

---
## K. Coding & Algorithms

Consolidated coding problems index: `docs/algo/coding_questions.md`

Use the canonical consolidated index above for one-line problem summaries, short solutions, complexity notes, and links to the detailed write-ups in `docs/algo/` (e.g., `docs/algo/01_shortest_path_weighted_graph.md`). This keeps the structured guide focused on Q/A and avoids duplicating the same coding problem list in multiple docs.

---
## L. Behavioral & Process

**Q: Disagreement with teammate/stakeholder?**  
A: Align on measurable criteria (latency, cost); present pros/cons; data-driven decision; document outcome; retrospective if assumptions fail.

**Q: Trade-off decision & impact?**  
A: Example: asynchronous queue for non-critical feature reducing API latency; trade-off eventual consistency for improved responsiveness.

**Q: Prioritizing multiple urgent issues?**  
A: Triage by user impact & SLO breach; handle high severity first; delegate/parallelize; maintain incident timeline.

**Q: Working solo vs Scrum?**  
A: Solo requires self-scoping & proactive status updates; Scrum provides iterative planning & visibility; both demand clear documentation.

**Q: Collaboration with parallel teams?**  
A: Shared RFC/design docs, API contracts, synchronous sync meetings + async updates in channels; contract tests to prevent regressions.

---
## M. API Design & Governance

**Q: Explain RESTful API?**  
A: Resource-oriented, stateless, uniform interface (HTTP verbs, URIs), representations (JSON), layered system, cacheable responses.

**Q: Best practices followed?**  
A: Consistent naming, proper status codes, pagination (cursor), filtering, HATEOAS optionally, validation errors structured, idempotent PUT/DELETE, security (auth scopes), rate limiting, tracing IDs.

**Q: Handle API versions & backward compatibility?**  
A: Version in URI (/v1), additive changes only; deprecation headers & usage metrics; maintain old version until usage below cutoff; contract tests.

**Q: Logging configuration in Spring Boot?**  
A: Use Logback defaults; set log levels in `application.yml`; structured JSON + correlation IDs (trace/span).

**Q: Governance between microservices?**  
A: Central dependency BOM, service catalog, semantic versioning, automated compatibility tests, deprecation workflow, security/vulnerability scans.

**Q: CORS cause & resolution?**  
A: Browser security restricting cross-origin; server sets `Access-Control-Allow-*` headers; Spring global `WebMvcConfigurer#addCorsMappings` or `@CrossOrigin`.

---
## N. Miscellaneous / Tooling

**Q: UUID meaning here?**  
A: Universally unique identifier for distinct rate limit or idempotency key—prevents collisions & enables distributed correlation.

**Q: CSV generation automation?**  
A: Scheduled export microservice: query DB → generate CSV → store (S3) → notify via email/Kafka event; include schema version & timestamp.

**Q: Messaging systems used?**  
A: RabbitMQ (work queues, routing), Kafka (streaming, event sourcing, high throughput, retention).

**Q: Choosing MongoDB?**  
A: Flexible schema, rapid iteration, aggregation pipeline, sharding for scale; trade-offs on multi-document ACID complexity & indexing overhead.

**Q: Sharding experience?**  
A: Partitioning by consistent hash/range; handle rebalancing & global queries via fan-out aggregation; maintain shard map service.

**Q: Frequency of PL/SQL usage?**  
A: (Answer context-specific) Typical daily tasks: write queries, tune execution plans, create indexes, batch ETL scripts.

---
## Cross-Reference

For full code samples & in-depth explanations, open the matching docs:
- Circuit Breaker Implementation → `docs/system_design/circuit_breaker.md`
- Terraform sample → `docs/system_design/terraform.md`
- Caching + LRU code → `docs/system_design/caching.md`
- SAGA orchestration/choreography → `docs/system_design/saga_pattern.md`
- SQL vs NoSQL, indexing details → `docs/system_design/database_design.md`
- System architecture diagram & rate limiter design → `docs/system_design/system_design.md`
- Coding problems solutions → `docs/algo/coding_questions.md`
- Error handling & debugging flows → `docs/system_design/error_handling_debugging.md`

---
## Suggested Next Enhancements

1. Link each question here to exact line anchors in original file.
2. Add a practice checklist (mark mastered vs pending).
3. Include flashcard generation script (optional) extracting Q/A pairs.

---
## Revision Strategy Tip

Study order: (1) Resilience & Security → (2) Data & Performance → (3) System Design → (4) Coding patterns → (5) Behavioral. Use p99 latency triage & SAGA flows as integration topics.
