# SAGA Pattern (Distributed Transactions)

Summary
- What: A choreography or orchestration-based approach to manage distributed transactions as a sequence of local transactions with compensating actions on failure.

Patterns
- Choreography: services publish events and other services react (no central coordinator).
- Orchestration: a central orchestrator (service) drives the steps and triggers compensating actions on failure.

Key considerations
- Idempotency for all steps and compensating actions.
- Compensation complexity: ensure compensating actions can run in reverse without leaving inconsistent state.
- Monitoring: track saga status, retries, and failed compensations.

Interview points
- When to prefer SAGA over 2PC (two-phase commit), pros/cons of choreography vs orchestration, and how to design compensating transactions.

## Example: Orchestrator (Java pseudo-implementation)

```text
// High-level orchestrator that executes steps and runs compensations on failure
public class SagaOrchestrator {
    private final List<SagaStep> steps;

    public SagaOrchestrator(List<SagaStep> steps) {
        this.steps = steps;
    }

    public void execute(Map<String, Object> state) throws Exception {
        List<SagaStep> executed = new ArrayList<>();
        for (SagaStep step : steps) {
            StepResult res = step.execute(state);
            if (!res.isOk()) {
                // run compensating actions in reverse order
                for (int i = executed.size() - 1; i >= 0; i--) {
                    executed.get(i).compensate(state);
                }
                throw new RuntimeException("Saga failed: " + res.getError());
            }
            executed.add(step);
        }
    }
}

public interface SagaStep {
    StepResult execute(Map<String, Object> state);
    void compensate(Map<String, Object> state);
}
```

## Example: Choreography (event-driven using Kafka - Java sketch)

```text
// Producer publishes OrderCreated
ProducerRecord<String,String> rec = new ProducerRecord<>("orders", orderId, orderJson);
producer.send(rec);

// Consumer for inventory service subscribes to 'orders' topic and reacts
consumer.subscribe(Collections.singletonList("orders"));
while (true) {
  ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(100));
  for (ConsumerRecord<String,String> r : records) {
    // process and publish InventoryReserved or InventoryFailed events
    producer.send(new ProducerRecord<>("inventory", r.key(), reservedEventJson));
  }
}
```
