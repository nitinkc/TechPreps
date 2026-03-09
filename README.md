# TechPreps — Interview Preparation Repository

Technical interview preparation: algorithms, system design, and microservices patterns.

## 📚 Documentation Site

**Live Site:** [https://nitinkc.github.io/TechPreps/](https://nitinkc.github.io/TechPreps/)

The docs are rendered using MkDocs Material and hosted on GitHub Pages.

## 🚀 Quick Links

| Topic | Description |
|-------|-------------|
| [Interview Guide](https://nitinkc.github.io/TechPreps/interview_guide/) | Structured Q&A for L2 technical interviews |
| [Coding Questions](https://nitinkc.github.io/TechPreps/algo/coding_questions/) | Canonical index of all coding problems |
| [System Design](https://nitinkc.github.io/TechPreps/system_design/system_design/) | High-level system design patterns |

## 📂 Repository Structure

```
src/main/java/com/interview/
├── algorithms/         # Core algorithm implementations (LRUCache, RateLimiter, etc.)
└── examples/           # Spring Boot microservice demo

docs/
├── interview_guide.md  # L2 Technical Interview Q&A
├── algo/               # Coding problem write-ups
└── system_design/      # System design topics
```

## 🔧 Local Development

### Run Tests
```bash
mvn clean test
mvn -Dtest=LRUCacheTest test   # Run specific test
```

### Serve Docs Locally
```bash
source .venv/bin/activate
pip install -r requirements-docs.txt
mkdocs serve
```

Open [http://localhost:8000](http://localhost:8000) to view the docs locally.

## 📖 Topics Covered

### Algorithms
- LRU Cache, Rate Limiter (token bucket), Banking system (top-K)
- Graph algorithms, Dynamic programming, Backtracking
- Linked lists, Trees, Sliding window

### System Design
- [Circuit Breaker](https://nitinkc.github.io/TechPreps/system_design/circuit_breaker/) — Resilience patterns
- [SAGA Pattern](https://nitinkc.github.io/TechPreps/system_design/saga_pattern/) — Distributed transactions
- [Caching](https://nitinkc.github.io/TechPreps/system_design/caching/) — Strategies & invalidation
- [Database Design](https://nitinkc.github.io/TechPreps/system_design/database_design/) — Partitioning, sharding, indexing
- [Service Discovery](https://nitinkc.github.io/TechPreps/system_design/service_discovery/) — Patterns & implementations
- [Messaging](https://nitinkc.github.io/TechPreps/system_design/messaging_kafka_nats_eventhub/) — Kafka, NATS, Event Hub

## 🛠 Tech Stack

- **Java 11** with Spring Boot 2.7.5
- **Spring Cloud**: Feign clients, Eureka discovery
- **Database**: H2 (in-memory for tests/demos)
- **Messaging**: Kafka for event-driven patterns
- **Docs**: MkDocs Material
