package com.learnkafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MessageProducer {

    KafkaProducer kafkaProducer;
    String topicName = "test-topic";

    public  MessageProducer(Map<String, String> props){

        kafkaProducer = new KafkaProducer(props);

    }
    public RecordMetadata publishMessae(String key, String message){

        ProducerRecord<String,String> producerRecord = new ProducerRecord<>(topicName, key, message);
        RecordMetadata recordMetadata=null;

        try {
            recordMetadata = (RecordMetadata) kafkaProducer.send(producerRecord).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return recordMetadata;
    }
}
