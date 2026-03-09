# Partitioning/Sharding/Clustering — E‑commerce Revision Card

Purpose: short, interview-ready quick-reference with minimal prose and focused examples (orders/users/products).

-- Quick anchors --
- Partition = split a table inside a DB (range/list/hash) for pruning/maintenance.
- Shard = split data across DB instances (app-level or DB native sharding).
- Cluster = replication group for HA and read-scaling (replicas / RF).

-----------------
Postgres (fast recall)

1) Partition by time (orders)
```sql
CREATE TABLE orders (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  amount NUMERIC(10,2) NOT NULL,
  status TEXT,
  created_at TIMESTAMPTZ NOT NULL
) PARTITION BY RANGE (created_at);
-- monthly partition
CREATE TABLE orders_2025_10 PARTITION OF orders
  FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');
```
Drop old month (fast):
```sql
ALTER TABLE orders DETACH PARTITION orders_2020_01;
DROP TABLE orders_2020_01;
```
+ Benefit: fast pruning/archival; query planner prunes partitions to speed range scans.

2) App-level sharding (simple):
- N shards with same schema; app picks shard by user_id.
```pseudo
shard_id = user_id % N
conn = pool.get("shard_" || shard_id)
conn.execute("INSERT INTO orders (...) VALUES (...)")
```
+ Benefit: horizontal scale of storage and IO across machines; predictable routing.

3) Replication (reads to replicas):
```sql
-- primary
CREATE PUBLICATION orders_pub FOR TABLE orders;
-- replica
CREATE SUBSCRIPTION orders_sub CONNECTION 'host=primary ...' PUBLICATION orders_pub;
```
App routing: writes -> primary; reads -> replicas (watch lag).
+ Benefit: high availability and read scalability; replicas absorb read traffic and provide failover.

-----------------
Cassandra (fast recall)

1) Table to fetch recent orders per user (partition + clustering):
```cql
CREATE TABLE orders_by_user (
  user_id text,
  order_time timestamp,
  order_id uuid,
  amount decimal,
  PRIMARY KEY ((user_id), order_time, order_id)
) WITH CLUSTERING ORDER BY (order_time DESC);
```
- Partition key = user_id (data distributed automatically by token ring).
- Avoid huge partitions; if hot user -> add bucket: PRIMARY KEY ((user_id, bucket), order_time,...)
+ Benefit: very fast single-partition reads (recent orders per user); predictable latency for modeled queries.

2) Keyspace replication (RF) and consistency (QUORUM for safety):
```cql
CREATE KEYSPACE ecommerce WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'dc1': '3'
};
```
Use QUORUM for inventory-critical ops; ONE for low-latency non-critical reads.
+ Benefit: multi-dc availability and fault tolerance; tunable consistency vs latency trade-offs.

-----------------
Routing / app patterns (copyable)

- Postgres partitioned + replicas:
```pseudo
if (write) -> connect(primary)
else if (read && stale_ok) -> connect(replica)
```
+ Benefit: split traffic: stable writes, scalable reads while preserving SQL semantics.

- Postgres app-shard:
```pseudo
shard = hash(user_id) % N
conn = pool.get("shard_"+shard)
conn.query(...)
```
+ Benefit: increases aggregate throughput and storage by adding shards; requires routing logic.

- Cassandra (no sharding code):
```pseudo
session.execute("INSERT INTO orders_by_user (...) VALUES (...)", {user_id, order_time, ...})
-- coordinator + replication handled by cluster
```
+ Benefit: zero app-level sharding; cluster transparently balances and replicates data.

-----------------
Concrete scenarios (one-line decisions)
- Small-to-medium OLTP + archival: Postgres partition + replicas.
  + Benefit: ACID + easy archival and reporting.
- Very high write volume, multi-region: Cassandra (partition by user_id) + RF=3.
  + Benefit: write scalability and geo-replication with low operational routing cost.
- Mixed: raw events -> Cassandra/Kafka, ETL -> Postgres for joins/analytics.
  + Benefit: best-of-both — fast ingest and consistent, relational analysis.

-----------------
Quick reshard / migration tips (Postgres)
- Prefer logical replication or Citus for live resharding.
- Steps (high level): create target shard, stream rows (logical replication or copy), cutover writes, drop source.
+ Benefit: minimal downtime migration path; reduces risk during reshards.

-----------------
Memory hooks (3 lines)
- Partition -> prune/drop (fast archival).
- Shard -> need routing, resharding cost.
- Cluster -> copies for HA, tune RF and CL for trade-offs.
+ Benefit: quick mental map of trade-offs to mention in interviews.

----
Revision task: memorize 3 code snippets: Postgres partition DDL, Postgres shard pseudo, Cassandra table CQL.
