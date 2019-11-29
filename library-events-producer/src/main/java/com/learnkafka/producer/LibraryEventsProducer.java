package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class LibraryEventsProducer {

    @Value("${spring.kafka.topic}")
    private String topic;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    public ListenableFuture<SendResult<Integer, String>> sendMessageWithKey(LibraryEvent libraryEvent) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(libraryEvent);
        Integer key = libraryEvent.getLibraryEventId();
        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(topic, key, message);
            listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
                @Override
                public void onFailure(Throwable ex) {
                    handleFailure(key, message, ex);
                }

                @Override
                public void onSuccess(SendResult<Integer, String> result) {
                    log.info("Message Sent SuccessFully with Key : {} and the message is {}", key, message);
                }
            });
        return listenableFuture;
    }

    public void handleFailure(Integer key, String message, Throwable ex){
        log.error("Error Sending the Message : {} and the exception is {} ", message , ex.getMessage());
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error in OnFailure : {}", throwable.getMessage());
        }
    }
}
