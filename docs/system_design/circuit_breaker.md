# Circuit Breaker Pattern

Summary
- What: A resiliency pattern to stop cascading failures by "opening" calls to a failing service and periodically probing it.
- When to use: Upstream calls to unreliable services, slow-downs under load, or third-party APIs.

Quick notes
- States: Closed, Open, Half-Open.
- Tripping triggers: error rate threshold, consecutive failures, or slow response threshold.
- Recovery: exponential backoff and probe requests in Half-Open.

Key considerations
- Metrics to track: error rate, latency, success rate during half-open probes.
- Fallbacks: return cached/cheap defaults, circuit-level fallback responses.
- Monitoring & alerts for prolonged open state.

Further reading / examples
- Implementations: Netflix Hystrix (deprecated), resilience4j (Java), Polly (.NET).

## Example: Java (resilience4j)

```java
// Gradle dependency: io.github.resilience4j:resilience4j-circuitbreaker
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
CircuitBreaker circuitBreaker = registry.circuitBreaker("backendService");

String result = circuitBreaker.executeSupplier(() -> {
    // call the backend service here
    return backend.call();
});
System.out.println("result=" + result);
```

## Example: Simple Circuit Breaker state machine (Java)

```java
public class SimpleCircuitBreaker {
    enum State { CLOSED, OPEN, HALF_OPEN }

    private State state = State.CLOSED;
    private int failureCount = 0;
    private final int threshold = 3;
    private long retryAfterMs = 0;

    public synchronized String call(Supplier<String> remoteCall) {
        long now = System.currentTimeMillis();
        if (state == State.OPEN && now < retryAfterMs) {
            return fallback();
        }

        try {
            String res = remoteCall.get();
            // success
            failureCount = 0;
            if (state == State.HALF_OPEN) state = State.CLOSED;
            return res;
        } catch (Exception ex) {
            failureCount++;
            if (failureCount >= threshold) {
                state = State.OPEN;
                retryAfterMs = System.currentTimeMillis() + backoffMs();
            }
            throw ex;
        }
    }

    private String fallback() {
        return "service-unavailable"; // simple fallback
    }

    private long backoffMs() {
        return 1000 * 60; // 1 minute backoff (example)
    }
}
```
