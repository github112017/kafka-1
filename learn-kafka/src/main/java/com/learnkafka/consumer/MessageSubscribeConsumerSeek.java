package com.learnkafka.consumer;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static com.learnkafka.constants.MessageConstants.TOPIC;
import static com.learnkafka.consumer.KafkaConsumerProps.propsMap;

public class MessageSubscribeConsumerSeek {

    private static final Logger logger = LoggerFactory.getLogger(MessageSubscribeConsumerSeek.class);


    public void subscribe(){
        Map<String,String> subscribePropsSeek = propsMap();
        subscribePropsSeek.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        subscribePropsSeek.put(ConsumerConfig.GROUP_ID_CONFIG, "subscribeconsumerseek2");
        KafkaConsumer subscribeConsumerSeek = new KafkaConsumer(subscribePropsSeek);

        subscribeConsumerSeek.subscribe(Arrays.asList(TOPIC));
        Set<TopicPartition>topicPartitions =  subscribeConsumerSeek.assignment(); //gets the assignment

        topicPartitions.forEach((topicPartition -> {
            subscribeConsumerSeek.seekToBeginning(Arrays.asList(topicPartition));
        }));

        try{
            while(true){
                ConsumerRecords<String, String> records =  subscribeConsumerSeek.poll(Duration.ofMillis(100));
                records.forEach((record) -> {
                    logger.info("Subscribe Consumer seek record is : "+ record);
                });
                subscribeConsumerSeek.commitSync();
            }
        }catch (CommitFailedException e){
            logger.info("CommitFailedException Occurred {}", e);
        }catch (Exception e){
            logger.info("Exception Occurred {}", e);
        }
        finally {
            subscribeConsumerSeek.close();
        }
    }


    public static void main(String[] args) {


        new MessageSubscribeConsumerSeek().subscribe();


    }
}
