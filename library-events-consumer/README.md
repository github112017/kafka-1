# Spring Boot Consumer

## Mandatory Inputs

```
spring:
  profiles: local
  kafka:
    topic: library-events
    consumer:
      bootstrap-servers:
        - localhost:9092,localhost:9093,localhost:9094
      #enable-auto-commit: true
      key-deserializer: org.apache.kafka.common.serialization.IntegerDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      group-id: library-events-listener-group
```

## HeartBeats

-   Set the logging level to **debug**

```aidl
#logging:
#  level:
#    root: debug
```

- Check for this message **Heartbeat request to coordinator** in the logs which will show you the heartbeat information.

## How Polling Works in Spring Kafka?

### KafkaMessageListenerContainer
-   **KafkaMessageListenerContainer** is one of the important class in Spring Kafka.
    -   This is the class that takes care of polling the Kafka to retrieve the records.
-   This class has a **run()** method that takes care of starting the consumer which in turn invokes the **pollAndInvoke**.

```
run()
| 
pollAndInvoke()
|
processCommmits() (Default Commit is BATCH, meaning once the records are successfully processed then it will take care of commitinng the offset)
|
doPoll() (polliTimeOut is 5000 seconds)
```    

-   The **ContainerProperties** class has all the information about ackMode, pollTimeOut
```aidl
  @Bean
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        //factory.setConcurrency(4);

        log.info("container properties " + factory.getContainerProperties());
        return factory;
    }
```
    