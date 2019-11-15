package com.learnkafka.producer;

import com.learnkafka.consumer.MessageSubscribeConsumer;
import com.learnkafka.domain.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MessageProducerWithCustomSerialization {
    private static final Logger logger = LoggerFactory.getLogger(MessageSubscribeConsumer.class);

    KafkaProducer kafkaProducer;
    String topicName = "test-topic";

    public  MessageProducerWithCustomSerialization(Map<String, String> props){
        kafkaProducer = new KafkaProducer(props);
    }

    public RecordMetadata publishMessageSync(Integer key, Message message){

        ProducerRecord<Integer,Message> producerRecord = new ProducerRecord<Integer, Message>(topicName, key, message);
        RecordMetadata recordMetadata=null;

        try {
            recordMetadata = (RecordMetadata) kafkaProducer.send(producerRecord).get();
            logger.info("recordMetadata : " + recordMetadata);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return recordMetadata;
    }
}
