package com.learnkafka.consumer;

import com.learnkafka.producer.MessageProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.learnkafka.constants.MessageConstants.TOPIC;

public class MessageConsumer {

    public static Map<String, String> propsMap() {
        Map<String,String> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093");
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return propsMap;
    }

    public void subscribe(){
        KafkaConsumer subscribeConsumer = new KafkaConsumer(propsMap());
        subscribeConsumer.subscribe(Arrays.asList("test-topic"));
    }

    public  void assign(){
        TopicPartition topicPartition  = new TopicPartition(TOPIC, 1);
        KafkaConsumer assignConsumer = new KafkaConsumer(propsMap());
        assignConsumer.assign(Arrays.asList(topicPartition));

    }
    public static void main(String[] args) {





    }
}
