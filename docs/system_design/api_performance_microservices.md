# API Performance & Microservices

Summary
- Focus: latency, throughput, resiliency, observability for APIs and microservices.

Key topics
- API design: idempotency, pagination, sensible defaults, versioning, and resource-based endpoints.
- Performance: caching (HTTP, CDN, application), compression (gzip/br), batching, rate limiting, and backpressure.
- Resiliency patterns: circuit breaker, bulkhead, retry with jitter, timeouts, and graceful degradation.
- Observability: structured logs, distributed tracing (OpenTelemetry), metrics (Prometheus), and dashboards/alerts.

Operational tips
- Use client-side and server-side timeouts to avoid resource exhaustion.
- Prefer async/batch for expensive operations; expose webhooks or polling for results.
- Apply rate limits and quotas per API key/customer to protect shared resources.

Common interview points
- Trade-offs of sync vs async, how to design an API for high QPS, and how to debug latency spikes with tracing.

## Example: HTTP caching headers (Java HttpClient)

```java
// Java 11+ HttpClient
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

HttpClient client = HttpClient.newHttpClient();
HttpRequest req = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/products/123"))
    .GET()
    .build();

HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
System.out.println("Status: " + resp.statusCode());
resp.headers().map().forEach((k,v) -> System.out.println(k + ": " + v));
// Look for Cache-Control and ETag headers in the response
```

## Example: OpenTelemetry (Java) - simple trace

```java
// Maven deps: io.opentelemetry:opentelemetry-api, io.opentelemetry:opentelemetry-sdk, io.opentelemetry:opentelemetry-exporter-logging
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;

SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
    .addSpanProcessor(SimpleSpanProcessor.create(new LoggingSpanExporter()))
    .build();
OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).buildAndRegisterGlobal();
Tracer tracer = openTelemetry.getTracer("com.example.api");

Span span = tracer.spanBuilder("handleRequest").startSpan();
try (Scope scope = span.makeCurrent()) {
    // instrumented work: make HTTP call, DB query, etc.
    doWork();
} finally {
    span.end();
    tracerProvider.shutdown();
}
```
