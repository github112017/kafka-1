package com.learnkafka.producer;

import com.learnkafka.domain.Message;
import com.learnkafka.serializer.CustomMessageSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class MessageProducerWithCustomSerializationTest {

    static MessageProducerWithCustomSerialization messageProducer;

    @BeforeClass
    public static void intialize(){
        Map<String,String> propsMap = new HashMap<>();
        propsMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        System.out.println("key Serializer " + IntegerSerializer.class.getName());
        System.out.println("value Serializer " + CustomMessageSerializer.class.getName());

        propsMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        propsMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CustomMessageSerializer.class.getName());
        messageProducer = new MessageProducerWithCustomSerialization(propsMap);
    }

    @Test
    public void publishMessage(){
        Message message = new Message(1, "Hello Custom Serializer");
        RecordMetadata recordMetadata = messageProducer.publishMessageSync(message.getId(), message);
        System.out.println(recordMetadata.partition());
        assertNotNull(recordMetadata);

    }
}
