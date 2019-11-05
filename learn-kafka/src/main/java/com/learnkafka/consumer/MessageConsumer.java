package com.learnkafka.consumer;

import com.learnkafka.producer.MessageProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.lang.reflect.Array;
import java.time.Duration;
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
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, "subscribeconsumer");
        return propsMap;
    }

    public void subscribe(){
        Map<String,String> subscribeProps = propsMap();
        subscribeProps.put(ConsumerConfig.GROUP_ID_CONFIG, "subscribeconsumer");
        KafkaConsumer subscribeConsumer = new KafkaConsumer(subscribeProps);
        subscribeConsumer.subscribe(Arrays.asList("test-topic"));
        try{
            while(true){
                ConsumerRecords<String, String> records =  subscribeConsumer.poll(Duration.ofMillis(100));
                records.forEach((record) -> {
                    System.out.println("Subscribe Consumer record is : "+ record);
                });
            }
        }finally {
            subscribeConsumer.close();
        }
    }

    public void assign(){
        TopicPartition topicPartition  = new TopicPartition(TOPIC, 1);
        Map<String,String> assignProps = propsMap();
        assignProps.put(ConsumerConfig.GROUP_ID_CONFIG, "assignconsumer");
        KafkaConsumer assignConsumer = new KafkaConsumer(assignProps);
        assignConsumer.assign(Arrays.asList(topicPartition));
        try{
            while(true){
                ConsumerRecords<String, String> records =  assignConsumer.poll(Duration.ofMillis(100));
                records.forEach((record) -> {
                    System.out.println("Assign Consumer record is : "+ record);
                });
            }
        }finally {
            assignConsumer.close();
        }

    }
    public static void main(String[] args) {


        new MessageConsumer().subscribe();
        new MessageConsumer().assign();


    }
}
