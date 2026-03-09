# Microservices & Scaling

Summary
- Design microservices for single responsibility, independent deployability, and clear APIs.

Scaling patterns
- Horizontal scaling: stateless services behind load balancers; stateful components partitioned or sharded.
- CQRS: separate read/write paths for scalability and simpler models.
- Autoscaling: metrics-driven (CPU, queue length, custom business metrics).

Operational concerns
- CI/CD pipelines, health checks, graceful shutdowns, and versioned APIs.
- Data consistency models: eventual vs strong consistency and implications for UX.

Interview points
- How to scale a stateful service, strategies for session state (sticky sessions vs external session store), and trade-offs of microservices vs monolith.

## Example: Kubernetes Deployment + Horizontal Pod Autoscaler (Java Fabric8 client)

```text
// Maven dependency: io.fabric8:kubernetes-client
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.autoscaling.v1.HorizontalPodAutoscalerBuilder;

try (KubernetesClient client = new DefaultKubernetesClient()) {
  var deployment = new DeploymentBuilder()
      .withNewMetadata().withName("my-service").endMetadata()
      .withNewSpec()
        .withReplicas(2)
        .withNewSelector().addToMatchLabels("app", "my-service").endSelector()
        .withNewTemplate()
          .withNewMetadata().addToLabels("app", "my-service").endMetadata()
          .withNewSpec()
            .addNewContainer()
              .withName("my-service")
              .withImage("my-org/my-service:latest")
              .addNewPort().withContainerPort(8080).endPort()
            .endContainer()
          .endSpec()
        .endTemplate()
      .endSpec()
      .build();

  client.apps().deployments().inNamespace("default").createOrReplace(deployment);

  var hpa = new HorizontalPodAutoscalerBuilder()
      .withNewMetadata().withName("my-service-hpa").endMetadata()
      .withNewSpec()
        .withNewScaleTargetRef().withApiVersion("apps/v1").withKind("Deployment").withName("my-service").endScaleTargetRef()
        .withMinReplicas(2)
        .withMaxReplicas(10)
        .addNewMetric() // simplified metric placeholder
          .withType("Resource")
        .endMetric()
      .endSpec()
      .build();

  client.autoscaling().horizontalPodAutoscalers().inNamespace("default").createOrReplace(hpa);
}
```

## Note: session handling
- Prefer stateless services; if sessions are needed, use external stores (Redis) or JWTs to avoid sticky sessions when scaling.
