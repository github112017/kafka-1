package com.learnkafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

import static com.learnkafka.constants.MessageConstants.TOPIC;
import static com.learnkafka.consumer.KafkaConsumerProps.propsMap;

public class MessageAssignConsumer {

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
        new MessageAssignConsumer().assign();

    }
}
