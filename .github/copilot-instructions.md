# Copilot Instructions — TechPreps Repository

## Project Overview

This is a **technical interview preparation repository** with two main components:
1. **Java implementations** (`src/`) — Algorithm and microservice examples using Spring Boot
2. **Documentation** (`docs/`) — Markdown write-ups for coding problems and system design topics, served via MkDocs

## Repository Structure

```
src/main/java/com/interview/
├── algorithms/         # Core algorithm implementations (LRUCache, RateLimiter, BankingSystem, etc.)
└── examples/           # Spring Boot microservice demo (controller → service → repository pattern)

docs/
├── algo/               # Numbered coding problem write-ups (01_*.md, 02_*.md, ...)
│   └── coding_questions.md  # ← Canonical index for all coding problems
└── system_design/      # Topic-based system design guides
```

## Build & Test Commands

```bash
# Build and run all tests
mvn clean test

# Run specific test class
mvn -Dtest=LRUCacheTest test

# Generate docs site (requires Python)
```sh
source .venv/bin/activate                                                       
pip install -r requirements-docs.txt
mkdocs serve
```

## Coding Conventions

### Algorithm Implementations (`com.interview.algorithms`)
- Each algorithm class is **self-contained** with inner classes (e.g., `Node` inside `LRUCache`)
- Use **HashMap + custom data structures** pattern for O(1) operations
- Include Javadoc explaining complexity and concurrency considerations
- Test files mirror source: `LRUCache.java` → `LRUCacheTest.java`

### Microservice Examples (`com.interview.examples`)
- Standard Spring Boot layering: `controller/` → `service/` → `repository/`
- Security via `@PreAuthorize` annotations on controller methods
- Async processing via `CompletableFuture.runAsync()` for non-blocking operations
- Kafka integration for event publishing (`kafkaTemplate.send()`)

### Documentation Pattern
When adding new coding problems:
1. Create numbered file in `docs/algo/` (e.g., `17_new_problem.md`)
2. Include sections: Learning Targets, Concept, Data Structures, Complexity, Edge Cases, Implementation
3. **Update `docs/algo/coding_questions.md`** with one-line summary + link (this is the canonical index)

## Key Technologies

- **Java 11** with Spring Boot 2.7.5, Spring Cloud 2021.0.5
- **Spring Cloud**: Feign clients, Eureka discovery
- **Database**: H2 (in-memory for tests/demos)
- **Messaging**: Kafka for event-driven patterns
- **Docs**: MkDocs with markdown files

## Test Structure

Tests use JUnit 5 (`@Test` from `org.junit.jupiter.api.Test`). Keep tests focused:
```java
@Test
public void testPutGetEvict() {
    LRUCache cache = new LRUCache(2);
    cache.put(1, 1);
    // ... assertions
}
```

## Common Interview Topics Covered

- **Algorithms**: LRU Cache, Rate Limiter (token bucket), Banking system (top-K), Percentile calculation
- **System Design**: Circuit breaker, Saga pattern, CQRS, Service discovery, Caching strategies
- **Patterns**: Outbox pattern, Idempotency, Choreography vs Orchestration
