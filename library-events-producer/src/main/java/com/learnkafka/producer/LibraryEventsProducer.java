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

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class LibraryEventsProducer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    public ListenableFuture<SendResult<Integer, String>> sendMessage(LibraryEvent libraryEvent, String topic) throws JsonProcessingException {
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

    public SendResult<Integer, String> sendMessageSynchronous(LibraryEvent libraryEvent, String topic) throws JsonProcessingException, ExecutionException, InterruptedException {
        String message = objectMapper.writeValueAsString(libraryEvent);
        Integer key = libraryEvent.getLibraryEventId();
        SendResult<Integer, String> sendResult = null;
        try {
            sendResult = kafkaTemplate.send(topic, key, message).get();
        } catch (ExecutionException | InterruptedException ex) {
            log.error("ExecutionException/InterruptedException Sending the Message {} and the exception is : {}", message , ex.getMessage());
            throw ex;
        }catch (Exception ex) {
            log.error("Exception/InterruptedException Sending the Message {} and the exception is : {}", message , ex.getMessage());
            throw ex;

        }
        return sendResult;
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
