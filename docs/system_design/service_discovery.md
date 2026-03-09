# Service Discovery (Eureka & Alternatives)

Summary
- Purpose: Allow services to find each other dynamically without hard-coded endpoints.

Patterns
- Client-side discovery: service queries registry and calls target directly (Netflix Eureka).
- Server-side discovery (reverse proxy): client calls a load balancer/proxy (Envoy, API Gateway) which routes to service instances.

Key points
- Registration: instances register/unregister on startup/shutdown and heartbeat.
- Health checks: integrate with discovery to avoid routing to unhealthy instances.
- Config & security: secure the registry with TLS and auth where appropriate.

Alternatives
- Consul, Zookeeper, Kubernetes DNS, AWS Cloud Map, AWS ALB with target groups.

Interview tips
- Explain pros/cons of client-side vs server-side discovery and how health checks affect routing decisions.

## Example: Consul registration (Java client)

```java
// Maven dependency: com.orbitz.consul:consul-client
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;

Consul consul = Consul.builder().build();
Registration service = ImmutableRegistration.builder()
    .id("users-1")
    .name("users")
    .address("10.0.0.5")
    .port(8080)
    .addMeta("version", "v1")
    .build();
consul.agentClient().register(service);

// To deregister:
// consul.agentClient().deregister("users-1");
```

## Example: Kubernetes service discovery (Java Fabric8 client)

```java
// Maven dependency: io.fabric8:kubernetes-client
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.api.model.Service;

try (KubernetesClient client = new DefaultKubernetesClient()) {
    Service svc = client.services().inNamespace("default").withName("users-service").get();
    if (svc != null && svc.getSpec() != null && svc.getSpec().getPorts() != null) {
        String clusterIp = svc.getSpec().getClusterIP();
        int port = svc.getSpec().getPorts().get(0).getPort();
        System.out.println("users-service -> " + clusterIp + ":" + port);
    } else {
        System.out.println("Service not found or no ports exposed");
    }
}
```
