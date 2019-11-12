package com.learnkafka.consumer;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.learnkafka.constants.MessageConstants.TOPIC;
import static com.learnkafka.consumer.KafkaConsumerProps.propsMap;

public class MessageConsumerWithoutGroup {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumerWithoutGroup.class);

    public void subscribe(){
        Map<String,String> subscribeProps = propsMap();
        subscribeProps.put(ConsumerConfig.GROUP_ID_CONFIG, "assignconsumerwithoutgroup");
        KafkaConsumer subscribeConsumerWithoutGroup = new KafkaConsumer(subscribeProps);

        List<PartitionInfo> partitionInfoList = subscribeConsumerWithoutGroup.partitionsFor(TOPIC);
        List<TopicPartition> topicPartitions = assigningPartitions(partitionInfoList);

        subscribeConsumerWithoutGroup.assign(topicPartitions);
        try{
            while(true){
                ConsumerRecords<String, String> records =  subscribeConsumerWithoutGroup.poll(Duration.ofMillis(100));
                records.forEach((record) -> {
                    logger.info("Subscribe Consumer record in WithoutGroup is : "+ record);
                });
                subscribeConsumerWithoutGroup.commitSync();
            }

        }catch (CommitFailedException e){
            logger.info("CommitFailedException Occurred {}", e);
        }catch (Exception e){
            logger.info("Exception Occurred "+ e);
        }
        finally {
            subscribeConsumerWithoutGroup.close();
        }
    }

    static List<TopicPartition> assigningPartitions(List<PartitionInfo> partitionInfoList){
        List<TopicPartition> topicPartitions = new ArrayList<>();
        if(partitionInfoList!=null){
            partitionInfoList.forEach(partitionInfo -> {
                topicPartitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
            });
        }

        return topicPartitions;
    }


    public static void main(String[] args) {
        new MessageConsumerWithoutGroup().subscribe();
    }
}
