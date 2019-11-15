package com.learnkafka.producer;


import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;

public class MessageProducerTest {

    static MessageProducer messageProducer;

    @BeforeClass
    public static void intialize(){
        Map<String,String> propsMap = new HashMap<>();
        propsMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        propsMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        propsMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //propsMap.put(ProducerConfig.LINGER_MS_CONFIG, "5000" ); // controls the wait time before the actual producer record is written in to the kafka topic.
        messageProducer = new MessageProducer(propsMap);
    }

    @Test
    public void publishMessage(){
        RecordMetadata recordMetadata = messageProducer.publishMessageSync(null, "123");
        System.out.println(recordMetadata.partition());
        assertNotNull(recordMetadata);

    }

    @Test
    public void publishMessageASync() throws InterruptedException, ExecutionException, TimeoutException {
        Future<RecordMetadata> recordMetadataFuture = messageProducer.publishMessageASync(null, "123");
        RecordMetadata  recordMetadata = recordMetadataFuture.get(3, TimeUnit.SECONDS);
        System.out.println(recordMetadata.partition());
        assertNotNull(recordMetadata);

    }
}
