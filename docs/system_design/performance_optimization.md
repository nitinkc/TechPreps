# Performance Optimization

Summary
- Focus: reduce latency, increase throughput, and make resource usage efficient.

Common techniques
- Query tuning: use EXPLAIN, indexes, limit result sets, avoid N+1 queries.
- Parallelism & batching: process in parallel where safe; batch IO/work to reduce overhead.
- Caching: leverage caches at multiple levels (CDN, app, DB) where appropriate.
- Resource sizing: right-size instances, use autoscaling and spot/preemptible instances for cost savings.

Observability
- Track p50/p95/p99 latencies, queue depths, error rates, and saturation metrics.
- Use profiling tools (CPU/memory/alloc sampling) to find hotspots.

Quick interview notes
- Be ready to explain a latency debugging flow: reproduce → measure → profile → fix → validate.

## Example: Java Flight Recorder (programmatic)

```java
// JDK 11+ includes jdk.jfr APIs
import jdk.jfr.Recording;
import java.nio.file.Path;

Recording recording = new Recording();
recording.setName("profile-session");
recording.start();

try {
    // run the workload you want to profile
    doWork();
} finally {
    recording.stop();
    Path dest = Path.of("profile-session.jfr");
    recording.dump(dest);
    recording.close();
    System.out.println("Wrote JFR file to: " + dest.toAbsolutePath());
}
```

Notes
- Open the resulting `.jfr` file in Java Mission Control (JMC) to analyze CPU, allocations, and latency hotspots.
- You can also start JFR via command-line (jcmd) or JVM flags for continuous profiling in production.

## Example: Simple Java concurrent HTTP load test (HttpClient + ExecutorService)

```java
// Java 11+ HttpClient based simple load generator
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.*;

int totalRequests = 10000;
int concurrency = 50;

HttpClient client = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(10))
    .build();

ExecutorService ex = Executors.newFixedThreadPool(concurrency);
CountDownLatch latch = new CountDownLatch(totalRequests);

for (int i = 0; i < totalRequests; i++) {
    ex.submit(() -> {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.example.com/health"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
            HttpResponse<String> r = client.send(req, HttpResponse.BodyHandlers.ofString());
            // process status or body if needed
        } catch (Exception e) {
            // handle errors (count failures etc.)
        } finally {
            latch.countDown();
        }
    });
}

latch.await();
ex.shutdown();
System.out.println("Load test completed");
```

Notes
- This is a basic generator useful for quick smoke tests; for realistic scenarios use dedicated tools (Gatling, JMeter) or add more sophisticated pacing, metrics aggregation, and error tracking.
