# Tech — Interview Prep Repository

This repository contains interview prep materials and detailed coding problem write-ups.

Canonical consolidated index for coding problems:

- `answers/coding_questions.md` — single source-of-truth: one-line summaries, short solutions, complexity notes, and links to the detailed `answers/*.md` write-ups.

Notes
- `answers/` contains the individual detailed write-ups and implementations.
- `L2_Technical_Interview_Guide_CLEAN.md` is deprecated and can be removed. If you want to delete it from the repository, run the `git rm` command below.

To remove the deprecated cleaned guide (locally) and commit the update:

```bash
# remove deprecated file and commit
git add README.md
git rm L2_Technical_Interview_Guide_CLEAN.md
git commit -m "chore(docs): add root README pointing to canonical coding index and remove deprecated CLEAN guide"
```

If you prefer I perform the git removal and commit for you, confirm and I will run those commands locally in the repository.

# guide_sections — L2 Technical Interview Guide (split topics)

This folder contains focused topic files extracted from the larger L2 guide to make the content easier to read and maintain. Each file is intentionally concise — use the main index `L2_Technical_Interview_Guide.md` to navigate to specific topics.

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
- Edit or add files in this folder for new topics; add a link to the main index if you want it listed.

Want a single compiled guide?
- I can concatenate these files into one markdown or produce a PDF — tell me your preferred order and I’ll generate it.

