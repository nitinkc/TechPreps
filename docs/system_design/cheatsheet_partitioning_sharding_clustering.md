Note: This file is the full write-up for the cheatsheet partitioning sharding clustering problem; see `answers/coding_questions.md` for the consolidated index.
# Cheat-sheet: Partitioning vs Sharding vs Clustering

Quick reference to remember differences, trade-offs, and examples.

What they mean (short):
- Partitioning: Logical subdivision of a table (typically inside a single DB instance) — e.g., PostgreSQL table partitions by date.
- Sharding: Horizontal data distribution across multiple database instances/servers (each holds a shard of the dataset) to scale capacity and throughput.
- Clustering: Grouping of database instances that cooperate for availability and replication (replicas, leader election, gossip). A shard often lives on a cluster of replicas.

When to use each:
- Partitioning: dataset fits on one server but a table is extremely large; want pruning and easier maintenance (fast drops of old partitions).
- Sharding: single-server limits reached for storage or throughput — distribute data across many servers using a shard key.
- Clustering: need high availability and replica-serving reads; use clusters in combination with replication factor and consistency choices.

Advantages / trade-offs:
- Partitioning
  - + Fast pruning, simple maintenance, single-instance ACID
  - - No horizontal scale across machines
- Sharding
  - + Horizontal scale (IO & storage), can isolate hotspots per shard
  - - Complex rebalancing, cross-shard queries harder, may need distributed coordination
- Clustering
  - + HA, failover, read-scaling (replicas)
  - - Operational complexity (monitoring lag, repairs), storage overhead for replicas

Short examples (commands / pseudo):
- Postgres partitioning (range by date):
  - CREATE TABLE events (...) PARTITION BY RANGE (event_date);
  - CREATE TABLE events_2025_10 PARTITION OF events FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');
- MongoDB (shard + replica sets):
  - Each shard = replica set (primary + secondaries); mongos routes by shard key (e.g. hashed userId).
- Cassandra (partition key + ring):
  - CREATE TABLE metrics (device_id text, ts timestamp, value double, PRIMARY KEY ((device_id), ts));
  - Partition key (device_id) determines token ownership in the ring; RF controls replication.

Compact comparison table

| Aspect | Partitioning | Sharding | Clustering |
|:---|:---|:---|:---|
| Scope | Intra-instance table split | Cross-instance data split | Group of nodes providing replicas/HA |
| Purpose | Manageability, pruning | Scale storage & throughput | HA, read-scaling, failover |
| Transactions | Local ACID | May need distributed transactions / eventual consistency | Depends on role (primary/replica) |
| Rebalance cost | Low (metadata operations) | High (reshard & data streaming) | Medium (streaming for node join/leave) |
| Routing | Planner-aware, transparent | Router or client routes by shard key | Cluster-aware driver or coordinator |
| Examples | Postgres partitions | MongoDB/Cassandra sharding | MongoDB replica set, Cassandra ring |

Mnemonic: Partition slices tables; Shard slices the cluster; Cluster groups machines that host replicas.

Tips for interviews
- Always state assumptions (single-node vs multi-node, consistency needed).
- Explain key selection: choose shard key for even distribution and query locality.
- Mention operational concerns: backups, monitoring replication lag, rolling upgrades, rebalancing windows.

## Interview-ready scenarios (practical examples)

Below are three concise, interview-friendly scenarios you can memorize and explain: one for Partitioning, one for Clustering, and one for Sharding.
Each includes concrete SQL/commands, maintenance operations, a short elevator pitch, and a couple of talking points about tradeoffs or edge cases.

### 1) Partitioning — Time-series events (Postgres range partition)

Scenario
- High-volume events table with millions of rows per day. Typical queries are recent time ranges and per-user slices.
- Need fast range queries and cheap removal of old data.

DDL (Postgres)
```
CREATE TABLE events (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  event_type TEXT NOT NULL,
  event_time TIMESTAMPTZ NOT NULL,
  payload JSONB
) PARTITION BY RANGE (event_time);

CREATE TABLE events_2025_10 PARTITION OF events
  FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');

CREATE TABLE events_2025_11 PARTITION OF events
  FOR VALUES FROM ('2025-11-01') TO ('2025-12-01');
```

Example usage (transparent to app)
```
INSERT INTO events (user_id, event_type, event_time, payload)
VALUES (42, 'click', '2025-11-05T12:34:00Z', '{"x":1}');

SELECT * FROM events
WHERE event_time BETWEEN '2025-11-01' AND '2025-11-07'
  AND user_id = 42;
```

Maintenance (fast ops)
- Drop old month quickly: `DROP TABLE events_2024_01;` (metadata-only, fast).
- Add next partition ahead of time with `CREATE TABLE ... FOR VALUES ...`.
- Per-partition VACUUM/REINDEX as needed.

Elevator pitch (1–2 lines)
- "Partitioning keeps a logical table on one server but splits it into parts (e.g., months). It speeds up range queries and enables instant drops of old partitions without moving rows."

Talking points / edge cases
- Planner pruning depends on queries — test plans to ensure pruning occurs.
- Global unique constraints across partitions require workarounds.
- Hot partition: recent partition receives all writes; ensure good IO on that partition.

---

### 2) Clustering — Primary + read replicas for HA and read scale (Postgres logical replication)

Scenario
- OLTP system where writes must be strongly consistent, but analytics and reporting queries can tolerate slightly stale data. Need high availability and read scaling.

Primary (setup)
```
-- on primary
CREATE TABLE orders (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  total NUMERIC(10,2),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE PUBLICATION orders_pub FOR TABLE orders;
```

Replica (subscriber)
```
-- on replica, connecting to primary
CREATE SUBSCRIPTION orders_sub
  CONNECTION 'host=primary.example.net port=5432 user=replicator password=... dbname=appdb'
  PUBLICATION orders_pub;
```

How to use in-app
- Route writes to primary only. Route read-only/analytics queries to replicas if slightly stale data is acceptable. Use a proxy or cluster-aware driver to direct reads.
- Monitor replica lag and avoid sending critical reads to lagging replicas.

Maintenance / failover notes
- For automated promotion/failover use tools (Patroni, repmgr, pg_auto_failover).
- Ensure backups and PITR are configured on primary; replicas can be rebuilt from base backups.

Elevator pitch
- "A cluster groups instances (primary + replicas) to provide HA and scale reads. Replication gives copies of data, but replicas may lag—so reads can be stale."

Talking points / edge cases
- Replica lag and eventual consistency for reads from replicas.
- Failover requires leader election and reconfiguration of the application routing.
- Some DDLs and features behave differently under logical replication — test schema changes.

---

### 3) Sharding — Horizontal scale across instances (app-level MySQL example + MongoDB note)

Scenario
- User base and total data exceed a single database's capacity. Most queries are per-user (e.g., profile, orders), so co-locating a user's data on one shard reduces cross-shard work.

Schema (same on every shard)
```
CREATE TABLE users (
  id BIGINT PRIMARY KEY,
  name VARCHAR(255),
  email VARCHAR(255)
);

CREATE TABLE orders (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  amount DECIMAL(10,2),
  created_at DATETIME
);
```

Simple routing (application)
- N = number of shards (e.g., 4). Compute `shard_id = user_id % N` and open connection to `shard_<shard_id>`.
- Inserts/queries for that user go to the computed shard.

Example (pseudo)
```
-- app computes shard_id = 12345 % 4 = 1
-- send SQL to shard_1
INSERT INTO orders (id, user_id, amount, created_at) VALUES (...);
```

Cross-shard queries & resharding
- Global queries (e.g., all orders in date range) require fan-out to all shards and merging results.
- Adding shards requires moving user id ranges or using consistent hashing to minimize moves; this is operationally complex.

MongoDB (built-in sharding) note
```
sh.enableSharding("analyticsDB")
sh.shardCollection("analyticsDB.events", { userId: "hashed" })
```
Each shard is typically a replica set (so sharding and clustering are used together).

Elevator pitch
- "Sharding splits data across independent database instances by a shard key (e.g., user_id). It scales storage and IO horizontally but makes cross-shard queries and rebalancing harder."

Talking points / edge cases
- Choose a shard key with high cardinality and even distribution to avoid hotspots.
- If you frequently join data across entities that use different keys, co-locate related entities on the same shard.
- Rebalancing is expensive; use consistent hashing or a proxy/middleware that supports online re-sharding if needed.

---

## Quick-check mapping to earlier sections
- Partitioning: use when a table is huge but fits one server and you want pruning/maintenance (see Partitioning scenario above).
- Clustering: use for HA and read-scaling (replicas) — cluster often underlies a shard for redundancy.
- Sharding: use to scale beyond single-server capacity; routing and rebalancing are the main operational costs.

