# AWS & GCP Data Ingestion

Summary
- What: Patterns and services for ingesting large-scale data (streaming & batch) into cloud platforms.
- When to use: telemetry, events, ETL pipelines, large file uploads.
- See cross-platform messaging trade-offs in `messaging_kafka_nats_eventhub.md`.

Common services
- AWS: Kinesis Data Streams, Kinesis Data Firehose, S3, Lambda, Glue, Data Pipeline.
- GCP: Pub/Sub, Dataflow, Cloud Storage, Cloud Functions, Dataproc.
- Other stacks: Kafka (self-managed / MSK), NATS (+ JetStream), Azure Event Hubs (Kafka-compatible), Pulsar (not detailed here).

Patterns
- Stream processing: pub/sub → processing (Dataflow/Kinesis) → sink (BigQuery/S3).
- Batch ingestion: ingest files to blob storage, trigger ETL jobs.
- Exactly-once vs at-least-once: use deduplication keys or idempotent processing.

Key considerations
- Backpressure and throttling, schema management (Avro/Protobuf), retries, dead-letter queues.
- Cost vs latency tradeoffs, regional/zone placement, monitoring (CloudWatch/Stackdriver/Azure Monitor).

Quick tips
- Use managed sink services (BigQuery/S3) for analytics workloads.
- Keep ingestion responsibilities minimal; offload heavy transforms to downstream batch/stream jobs.
- Ultra-low latency ephemeral events → NATS; long retention & replay → Kafka/Event Hubs.

## Example: Java Pub/Sub publisher (GCP)

```java
// Maven dependency: com.google.cloud:google-cloud-pubsub
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

TopicName topicName = TopicName.of("my-project", "events-topic");
Publisher publisher = null;
try {
  publisher = Publisher.newBuilder(topicName).build();
  String json = "{\"user_id\":42,\"event\":\"click\"}";
  ByteString data = ByteString.copyFromUtf8(json);
  PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
  publisher.publish(pubsubMessage).get(); // wait for publish
} finally {
  if (publisher != null) publisher.shutdown();
}
```

## Example: Kafka consumer (Java)

```java
// Maven dependency: org.apache.kafka:kafka-clients
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

Properties props = new Properties();
props.put("bootstrap.servers", "kafka:9092");
props.put("group.id", "g1");
props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
consumer.subscribe(Arrays.asList("events"));

while (true) {
  ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
  for (ConsumerRecord<String, String> record : records) {
    System.out.println("Received: " + record.value());
  }
}
```

## Additional: NATS & Event Hubs
- See `messaging_kafka_nats_eventhub.md` for full comparison.
- NATS: ultra-low latency ephemeral + JetStream for persistence.
- Event Hubs: managed Azure ingestion with Kafka compatibility.

### NATS publish (Java)
```java
Connection nc = Nats.connect("nats://localhost:4222");
nc.publish("events.click", "{\"user_id\":42,\"event\":\"click\"}".getBytes());
nc.flush(Duration.ofSeconds(1));
nc.close();
```

### NATS JetStream durable consumer (Java)
```java
Connection nc = Nats.connect();
JetStream js = nc.jetStream();
ConsumerConfiguration cc = ConsumerConfiguration.builder().durable("ingest-worker").build();
PushSubscribeOptions opts = PushSubscribeOptions.builder().configuration(cc).build();
Subscription sub = js.subscribe("events.stream", opts);
Message msg = sub.nextMessage(Duration.ofSeconds(1));
if (msg != null) { System.out.println(new String(msg.getData())); msg.ack(); }
```

### Azure Event Hubs producer (Kafka API)
```java
Properties props = new Properties();
props.put("bootstrap.servers", "YOUR_NAMESPACE.servicebus.windows.net:9093");
props.put("security.protocol", "SASL_SSL");
props.put("sasl.mechanism", "PLAIN");
props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"$ConnectionString\" password=\"<CONNECTION_STRING>\";");
props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
Producer<String,String> producer = new KafkaProducer<>(props);
producer.send(new ProducerRecord<>("myeventhub", "key", "value"));
producer.close();
```

### Azure Event Hubs (AMQP) consumer (Java)
```java
EventHubConsumerClient consumer = new EventHubClientBuilder()
  .connectionString("<CONNECTION_STRING>", "myeventhub")
  .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
  .buildConsumerClient();

for (PartitionEvent event : consumer.receiveFromPartition("0", Duration.ofSeconds(5))) {
  System.out.println(new String(event.getData().getBody()));
}
consumer.close();
```
