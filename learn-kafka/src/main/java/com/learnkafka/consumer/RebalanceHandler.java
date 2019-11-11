package com.learnkafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class RebalanceHandler implements ConsumerRebalanceListener {
    private static final Logger logger = LoggerFactory.getLogger(RebalanceHandler.class);
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        logger.info("Inside Partitions Revoked");
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        logger.info("Inside Partitions Assigned : " + partitions);
    }
}
