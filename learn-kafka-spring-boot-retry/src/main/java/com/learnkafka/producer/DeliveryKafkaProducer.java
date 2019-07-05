package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.dto.Delivery;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DeliveryKafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.consumer.topic}")
    private String topic;

    public void sendMessage(Delivery delivery){

        try{
            ProducerRecord<String, String> producerRecord = buildProducerRecord(delivery);

            ListenableFuture<SendResult<String, String>> listenableFuture =  kafkaTemplate.send(producerRecord);

            listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onFailure(Throwable ex) {
                    log.info("onFailure in Publishing the message to Kafka topic : {} ", ex);
                }

                @Override
                public void onSuccess(SendResult<String, String> result) {
                    log.info("Sent message SuccessFulle : {} ", producerRecord.value());
                }
            });

        } catch (Exception e ) {
            log.error("Exception Occurred in Publishing the message to Kafka topic " + e);

        }

    }


    protected ProducerRecord<String,String> buildProducerRecord(Delivery delivery) throws JsonProcessingException {
        List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader("messageType", "outbound".getBytes()));

        ProducerRecord<String, String> producerRecord = new ProducerRecord(topic, null, delivery.getDeliveryId(), delivery.jsonValue(),headers);
        return producerRecord;

    }

}
