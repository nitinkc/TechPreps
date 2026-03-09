# Error Handling & Debugging

Summary
- Aim: make systems observable and failures informative to enable fast detection and recovery.

Principles
- Fail fast and fail loud for developer-facing errors; graceful degradation for user-facing services.
- Use structured logs with correlation IDs (trace id / request id) to trace flows across services.
- Centralize logs, use distributed tracing (OpenTelemetry), and collect metrics (Prometheus/Grafana).

Debugging checklist
- Reproduce locally with minimal data; add feature flags or debug endpoints if safe.
- Use tracing to find latency and causation; inspect logs and relevant metrics (error rate, p95/p99 latencies).
- Add health endpoints, circuit-breaker alerts, and dashboards for critical paths.

Operational tips
- Retain enough logs for debugging but beware cost; sample traces for high-volume flows.
- Implement structured alerts with runbooks and post-mortems to improve reliability.

## Example: Structured logging with correlation ID (Java, Servlet filter + SLF4J/MDC)

```java
// Maven deps: org.slf4j:slf4j-api, ch.qos.logback:logback-classic
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class CorrelationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(CorrelationFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String traceId = req.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = java.util.UUID.randomUUID().toString();
        }
        MDC.put("trace_id", traceId);
        try {
            logger.info("start request: {} {}", req.getMethod(), req.getRequestURI());
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("error handling request", e);
            throw e;
        } finally {
            logger.info("end request");
            MDC.remove("trace_id");
        }
    }
}
```

## Example: Simple OpenTelemetry trace (Java)

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
Tracer tracer = openTelemetry.getTracer("com.example.app");

Span span = tracer.spanBuilder("processOrder").startSpan();
try (Scope scope = span.makeCurrent()) {
    // do work here; downstream calls can be instrumented
    doWork();
} catch (Exception e) {
    span.recordException(e);
    throw e;
} finally {
    span.end();
    tracerProvider.shutdown();
}
```
