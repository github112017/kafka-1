package com.learnkafka.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.Message;
import com.learnkafka.serializer.CustomMessageSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMessageDeserializer implements Deserializer<Message> {
    private static final Logger logger = LoggerFactory.getLogger(CustomMessageDeserializer.class);

    @Override
    public Message deserialize(String topic, byte[] data) {
        logger.info("Inside Consumer Deserialization");
        ObjectMapper mapper = new ObjectMapper();
        Message message = null;
        try {
            message = mapper.readValue(data, Message.class);
        } catch (Exception e) {

            logger.error("Exception Occurred during deserializing"+e);
        }
        return message;
    }
}
