# Tech — Interview Prep Repository

This repository contains interview prep materials and detailed coding problem write-ups.

Canonical consolidated index for coding problems:

```bash
# remove deprecated file and commit
git add README.md
git rm L2_Technical_Interview_Guide_CLEAN.md
git commit -m "chore(docs): add root README pointing to canonical coding index and remove deprecated CLEAN guide"
```

# guide_sections — L2 Technical Interview Guide (split topics)

Files
- `circuit_breaker.md` — Circuit breaker pattern, states, triggers, and fallbacks.
- `data_ingestion.md` — Cloud ingestion patterns for AWS & GCP (streaming & batch).
- `api_performance_microservices.md` — API design, performance, resiliency, and observability.
- `terraform.md` — Terraform concepts, best practices, and state considerations.
- `microservices_scaling.md` — Scaling patterns, CQRS, autoscaling, and operational concerns.
- `service_discovery.md` — Client/server-side discovery patterns and common implementations.
- `security.md` — AuthN/AuthZ, secrets, transport security, and operational tips.
- `caching.md` — Cache types, patterns, invalidation strategies, and stampede protection.
- `saga_pattern.md` — Distributed transactions: choreography vs orchestration and compensations.
- `database_design.md` — Schema design, indexing, partitioning/sharding, and scaling tips.
- `system_design.md` — Interview approach: requirements, components, trade-offs, and diagrams.
- `error_handling_debugging.md` — Observability, tracing, logs, and debugging checklist.
- `performance_optimization.md` — Query tuning, profiling, caching, and observability.
- `FAQ.md` — Quick answers about the repo layout and usage.

How to use
- Open `L2_Technical_Interview_Guide.md` for the main index linking to these files.
