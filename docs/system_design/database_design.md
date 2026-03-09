# Database Design (Core Concepts)

Summary
- Focus: schema design, indexing, normalization vs denormalization, transactions, and scaling strategies.

Key topics
- Data modeling: choose between normalized (OLTP) and denormalized (analytics/OLAP) models.
- Indexing: B-tree vs hash vs materialized views, covering indexes, and index maintenance costs.
- Consistency: transactions, isolation levels, optimistic vs pessimistic locking.

Scaling & patterns
- Partitioning (intra-instance) for large tables, sharding (cross-instance) for horizontal scale, and replication for HA/read scale.
- Use read-replicas, caching layers, and CQRS where appropriate.

Operational tips
- Monitor slow queries, use EXPLAIN plans, set appropriate vacuum/autovacuum/compaction policies, and size connection pools.

Interview prompts
- How to design a schema for a high-write system, picking a shard key, and techniques to avoid hotspots.

## Example: SQL schema + partitioning + index (Postgres via JDBC)

```java
// Maven dependency: org.postgresql:postgresql
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

String url = "jdbc:postgresql://localhost:5432/mydb";
try (Connection conn = DriverManager.getConnection(url, "user", "pass");
     Statement stmt = conn.createStatement()) {
    // Create partitioned table (Postgres)
    String ddl = "CREATE TABLE IF NOT EXISTS orders ("
        + "id BIGSERIAL PRIMARY KEY,"
        + "user_id BIGINT NOT NULL,"
        + "amount NUMERIC(10,2),"
        + "created_at TIMESTAMPTZ NOT NULL DEFAULT now()"
        + ") PARTITION BY RANGE (created_at);";
    stmt.execute(ddl);

    String pddl = "CREATE TABLE IF NOT EXISTS orders_2025_10 PARTITION OF orders "
        + "FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');";
    stmt.execute(pddl);

    // Create index
    stmt.execute("CREATE INDEX IF NOT EXISTS idx_orders_user_created ON orders (user_id, created_at);");

    // Run EXPLAIN
    String query = "EXPLAIN (ANALYZE, BUFFERS) SELECT * FROM orders WHERE user_id = 42 AND created_at >= '2025-10-01' AND created_at < '2025-11-01';";
    try (ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }
}
```

## Example: Using EXPLAIN via JDBC to find slow queries

```java
try (Connection conn = DriverManager.getConnection(url, "user", "pass");
     Statement stmt = conn.createStatement()) {
    try (ResultSet rs = stmt.executeQuery("EXPLAIN (ANALYZE, BUFFERS, TIMING) SELECT * FROM orders WHERE amount > 1000 ORDER BY created_at DESC LIMIT 100;")) {
        while (rs.next()) System.out.println(rs.getString(1));
    }
}
```
