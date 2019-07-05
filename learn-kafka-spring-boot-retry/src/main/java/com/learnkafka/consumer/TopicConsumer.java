package com.learnkafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TopicConsumer {

    @KafkaListener(id = "load-event-processor", topics = {"${spring.kafka.consumer.topic}"})
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {

        String errorRecord = "10"; // This is a error record
        try {
            log.info("offset : {} , partition : {} " ,record.offset(), record.partition());
            log.info("Consumer Record read is : " + record);
            parseRecordHeader(record);
            String readMessage = record.value();
            if (readMessage.equals(errorRecord)) {
                throw new RuntimeException("Poisonous Message");
            }
            log.info("Message is  : " + readMessage);
        } catch (RuntimeException e) {
            acknowledgment.acknowledge();  // commit even when in error so that the poisonous record wont be processed again.
            log.error("RuntimeException is : " + e);
            throw e;
        } catch (Exception e) {
            log.error("Exception is : " + e);
        }
        acknowledgment.acknowledge(); // commits the offset to Kafka
        log.info("Offset Commited");

    }

    private void parseRecordHeader(ConsumerRecord<String, String> record) {
        Headers headers = record.headers();
        for (Header header: headers.toArray()){
            log.info("Key :  {} , Value : {} ", header.key(), new String(header.value())  );
        }
    }
}
