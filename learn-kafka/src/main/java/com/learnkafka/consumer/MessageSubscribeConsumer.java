package com.learnkafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.learnkafka.constants.MessageConstants.TOPIC;
import static com.learnkafka.consumer.KafkaConsumerProps.propsMap;

public class MessageSubscribeConsumer {



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


    public static void main(String[] args) {


        new MessageSubscribeConsumer().subscribe();


    }
}
