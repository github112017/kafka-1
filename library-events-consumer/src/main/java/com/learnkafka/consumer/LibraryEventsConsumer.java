package com.learnkafka.consumer;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LibraryEventsConsumer {

    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @KafkaListener(topics = {"${spring.kafka.topic}"}
    //,groupId = "abc"
    )
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws InterruptedException {
        log.info("Consumer Record is : {} " , consumerRecord.toString());
        log.info("Listener Container List {} " ,  kafkaListenerEndpointRegistry.getAllListenerContainers().size());
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(messageListenerContainer -> {
            log.info(messageListenerContainer.toString());
            log.info("Groups id is {} ",  messageListenerContainer.getGroupId());
        });
    }

/*
    public void onMessage(ConsumerRecords<?,?> consumerRecords) {
        log.info("Consumer Record is : " + consumerRecords.count());
        consumerRecords.forEach(consumerRecord -> {
            log.info("Consumer Record is : " + consumerRecord.toString());
        });
    }
*/

}
