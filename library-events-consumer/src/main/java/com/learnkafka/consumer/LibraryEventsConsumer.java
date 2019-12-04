package com.learnkafka.consumer;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LibraryEventsConsumer {

    @KafkaListener(topics = {"${spring.kafka.topic}"}
    //,groupId = "abc"
    )
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("Consumer Record is : " + consumerRecord.toString());
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
