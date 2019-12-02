

## Kafka Commands

### Print Key and Values

```aidl

./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic library-events \
--property key.deserializer=org.apache.kafka.common.serialization.IntegerDeserializer \
--property value.deserialzer=org.apache.kafka.common.serialization.StringDeserializer \
--property print.key=true \ 

```

./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic library-events \
--property key.deserializer=org.apache.kafka.common.serialization.IntegerDeserializer \
--property value.deserialzer=org.apache.kafka.common.serialization.StringDeserializer \
--property print.key=true \ 
--property print.headers=true