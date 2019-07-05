# Kafka 

## Setting Up Kafka

-   Start up the Zookeeper.

```youtrack
./zookeeper-server-start.sh ../config/zookeeper.properties
```

-   Start up the Kafka Broker.

```youtrack
./kafka-server-start.sh ../config/server.properties
```

## How to create a topic ?

**my-first-topic:**
```youtrack
./kafka-topics.sh --create --topic my-first-topic -zookeeper localhost:2181 --replication-factor 1 --partitions 4
```

## How to instantiate a Console Producer?

```youtrack
./kafka-console-producer.sh --broker-list localhost:9092 --topic my-first-topic
```

## How to instantiate a Console Consumer?

```youtrack
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic my-first-topic --from-beginning
```

## Producers:

### Configure Kafka Producer

```youtrack
spring:
  profiles:
    active: local
  kafka:
    producer:
      bootstrap-servers: localhost:9092
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

### Produce Message with Custom Headers in ProducerRecord

```youtrack
 protected ProducerRecord<String,String> buildProducerRecord(Delivery delivery) throws JsonProcessingException {
        List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader("messageType", "outbound".getBytes()));

        ProducerRecord<String, String> producerRecord = new ProducerRecord(topic, null, delivery.getDeliveryId(), delivery.jsonValue(),headers);
        return producerRecord;

    }
```

## Consumers:

### Configure Kafka Consumer

```youtrack
spring:
  profiles:
    active: local
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      topic: my-first-topic
      group-id: my-first-topic-group
      auto-offset-reset: latest
      enable-auto-commit: false
      properties:
        session.timeout.ms: 15000
        ssl:
          endpoint:
            identification:
              algorithm:
        value.deserializer: org.apache.kafka.common.serialization.StringDeserializer
```

### CosumerConfig


#### Concurrency 

-   Changing the concurrency level to greater than 1.

```youtrack
 @Bean
    ConcurrentKafkaListenerContainerFactory<Object,Object>
    kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer, ConsumerFactory<Object,Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(containerFactory,  consumerFactory);
        containerFactory.setRetryTemplate(retryTemplate()); // This enables the retry.
        containerFactory.setConcurrency(2);
        return containerFactory;
    }
```

-   Two instances of consumers will get spin up in two different threads.
-   The partitions are distributed between those consumers.

### Consumer Message with Custom Headers in ConsumerRecord

```youtrack
  private void parseRecordHeader(ConsumerRecord<String, String> record) {
        Headers headers = record.headers();
        for (Header header: headers.toArray()){
            log.info("Key :  {} , Value : {} ", header.key(), new String(header.value())  );
        }
    }
```

### Configuring Retry

- Check the **ConsumerConfig** class.This has the complete code for the retry and retry listener code.
