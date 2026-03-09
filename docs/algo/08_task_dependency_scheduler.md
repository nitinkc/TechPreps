# 8. Task Dependency Scheduler

## Learning Targets
- Model dependency DAG execution with parallelism.
- Use topological readiness and ExecutorService.
- Track statuses and results with futures.
- Handle failures & cancellation.

## Requirements Recap
1. Execute task only after dependencies succeed.
2. Run independent tasks concurrently.
3. Failure propagation: dependent tasks should not run if any prerequisite failed.
4. Monitoring: query current statuses.
5. Cancellation: stop pending tasks.

Assumptions: Task execution simulated by sleeping executionTimeMs; success if not interrupted.

## Core Idea
Maintain in-degree (remaining dependencies) count. Initially enqueue tasks with in-degree 0. On completion success: decrement dependents’ counts; when a dependent reaches 0 and all its dependencies succeeded, schedule it. On failure: mark dependents blocked/failed.

## Data Structures
- Map<String, Task> tasksById.
- Map<String, Set<String>> dependents adjacency.
- Map<String, Integer> remainingDeps.
- Map<String, TaskResult> results.
- ConcurrentHashMap<String, TaskStatus> statuses.
- ExecutorService (fixed thread pool sized to tasks count or CPU cores).
- CompletableFuture<Map<String, TaskResult>> overallFuture.

## Concurrency Control
Use synchronized blocks or atomic operations when mutating shared maps. A thread-safe queue for ready tasks (e.g., ConcurrentLinkedQueue) or direct submission when dependency count hits zero inside synchronized block.

## Failure Handling
If a task fails, mark status FAILED; for each dependent, mark as FAILED (or SKIPPED conceptually) if any dependency failed; they never schedule.

## Cancellation
Maintain a volatile cancelled flag. cancelAll(): set flag, shutdownNow executor, update pending tasks to FAILED or CANCELLED.

## Complexity
- Build structures: O(n + e) where e total dependency edges.
- Execution: Each task scheduled once ⇒ O(n).
- Space: O(n + e).

## Java Implementation Sketch
```java
public class TaskSchedulerImpl implements TaskScheduler {
    private final Map<String, TaskStatus> statuses = new ConcurrentHashMap<>();
    private final Map<String, TaskResult> results = new ConcurrentHashMap<>();
    private volatile boolean cancelled = false;
    private ExecutorService executor;

    @Override
    public CompletableFuture<Map<String, TaskResult>> executeTasks(List<Task> tasks) {
        Map<String, Task> byId = tasks.stream().collect(Collectors.toMap(Task::getId, t->t));
        Map<String, Set<String>> dependents = new HashMap<>();
        Map<String, Integer> remaining = new HashMap<>();
        for (Task t: tasks) {
            statuses.put(t.getId(), TaskStatus.PENDING);
            remaining.put(t.getId(), t.getDependencies().size());
            for (String dep: t.getDependencies()) {
                dependents.computeIfAbsent(dep, k-> new HashSet<>()).add(t.getId());
            }
        }
        executor = Executors.newFixedThreadPool(Math.min(tasks.size(), Runtime.getRuntime().availableProcessors()));
        CompletableFuture<Map<String, TaskResult>> all = new CompletableFuture<>();
        AtomicInteger completedCount = new AtomicInteger(0);
        int total = tasks.size();

        Runnable maybeComplete = () -> {
            if (completedCount.get() == total && !all.isDone()) {
                executor.shutdown();
                all.complete(Collections.unmodifiableMap(results));
            }
        };

        // Submit initial ready tasks
        for (Task t: tasks) {
            if (remaining.get(t.getId()) == 0) submitTask(t, byId, dependents, remaining, completedCount, maybeComplete);
        }
        // Edge case: cyclic dependencies (no task has 0) -> detect
        if (completedCount.get() == 0 && tasks.stream().allMatch(t -> remaining.get(t.getId()) > 0)) {
            all.completeExceptionally(new IllegalStateException("Cycle detected"));
        }
        return all;
    }

    private void submitTask(Task t,
                             Map<String, Task> byId,
                             Map<String, Set<String>> dependents,
                             Map<String, Integer> remaining,
                             AtomicInteger completedCount,
                             Runnable maybeComplete) {
        if (cancelled) return;
        statuses.put(t.getId(), TaskStatus.RUNNING);
        executor.submit(() -> {
            long start = System.currentTimeMillis();
            boolean success = true; String error = null;
            try {
                Thread.sleep(t.getExecutionTimeMs());
            } catch (InterruptedException e) {
                success = false; error = "Interrupted"; Thread.currentThread().interrupt();
            }
            long dur = System.currentTimeMillis() - start;
            if (!success) statuses.put(t.getId(), TaskStatus.FAILED); else statuses.put(t.getId(), TaskStatus.COMPLETED);
            results.put(t.getId(), new TaskResult(t.getId(), success, error, dur));
            completedCount.incrementAndGet();
            if (success) {
                // Unblock dependents
                for (String dep : dependents.getOrDefault(t.getId(), Collections.emptySet())) {
                    if (statuses.get(dep) == TaskStatus.PENDING) {
                        synchronized (remaining) {
                            remaining.put(dep, remaining.get(dep) - 1);
                            if (remaining.get(dep) == 0) {
                                // Ensure all its dependencies succeeded
                                boolean depsOk = byId.get(dep).getDependencies().stream()
                                        .allMatch(d -> statuses.get(d) == TaskStatus.COMPLETED);
                                if (depsOk) submitTask(byId.get(dep), byId, dependents, remaining, completedCount, maybeComplete);
                                else {
                                    statuses.put(dep, TaskStatus.FAILED);
                                    results.put(dep, new TaskResult(dep, false, "Dependency failed", 0));
                                    completedCount.incrementAndGet();
                                }
                            }
                        }
                    }
                }
            } else {
                // Propagate failure: mark dependents failed
                for (String dep : dependents.getOrDefault(t.getId(), Collections.emptySet())) {
                    if (statuses.get(dep) == TaskStatus.PENDING) {
                        statuses.put(dep, TaskStatus.FAILED);
                        results.put(dep, new TaskResult(dep, false, "Upstream failed", 0));
                        completedCount.incrementAndGet();
                    }
                }
            }
            maybeComplete.run();
        });
    }

    @Override
    public Map<String, TaskStatus> getTaskStatuses() {
        return new HashMap<>(statuses);
    }

    @Override
    public void cancelAll() {
        cancelled = true;
        if (executor != null) executor.shutdownNow();
        statuses.replaceAll((k,v) -> v == TaskStatus.COMPLETED ? v : TaskStatus.FAILED);
    }
}
```

## Improvements
- Replace failure propagation strategy with SKIPPED status.
- Add timeout per task.
- Support retries with exponential backoff.
- Use CompletableFuture per task for composition.

## Summary
The scheduler uses dependency counting and concurrent execution of ready tasks, updating statuses and results; failures block dependents.
