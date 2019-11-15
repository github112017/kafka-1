package com.learnkafka.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.Message;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMessageSerializer implements Serializer<Message> {

    private static final Logger logger = LoggerFactory.getLogger(CustomMessageSerializer.class);
    private String encoding = "UTF8";
    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public byte[] serialize(String topic, Message data) {
        logger.info("inside serialization logic");
        try {

            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            logger.error("Unable to serialize object {}", data, e);
            return null;
        }
    }
}
