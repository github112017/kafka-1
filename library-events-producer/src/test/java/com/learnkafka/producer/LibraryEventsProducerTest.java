package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.domain.LibraryEventStatusEnum;
import org.apache.kafka.common.errors.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class LibraryEventsProducerTest {

    @Autowired
    LibraryEventsProducer libraryEventsProducer;

    @Value("${spring.kafka.topic}")
    private String topic;

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(libraryEventsProducer, "topic", topic);
    }

    @Test
    void sendMessageWithKey() throws JsonProcessingException, InterruptedException {

        //given
        Book book = new Book().builder()
                .bookId(456)
                .bookAuthor("Dilip")
                .bookName("Kafka Using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(123)
                .eventStatus(LibraryEventStatusEnum.BOOK_ADDED)
                .book(book)
                .build();

        //when
        libraryEventsProducer.sendMessageWithKey(libraryEvent);

        //then
        Thread.sleep(3000);


    }

    @Test
    void sendMessageWithNullKey() throws JsonProcessingException, InterruptedException, ExecutionException {

        //given
        Book book = new Book().builder()
                .bookId(null)
                .bookAuthor("Dilip")
                .bookName("Kafka Using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(123)
                .eventStatus(LibraryEventStatusEnum.BOOK_ADDED)
                .book(book)
                .build();

        //when
        ListenableFuture<SendResult<Integer, String>> listenableFuture = libraryEventsProducer.sendMessageWithKey(libraryEvent);
        SendResult<Integer, String> sendResult =  listenableFuture.get();

        //then
        System.out.println("Send Reult : " + sendResult);
        assertNotNull(sendResult.getRecordMetadata());

    }

    @Test
    void sendMessageWithErrorTopic() {

        //given
        ReflectionTestUtils.setField(libraryEventsProducer, "topic", "sample");

        Book book = new Book().builder()
                .bookId(null)
                .bookAuthor("Dilip")
                .bookName("Kafka Using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(123)
                .eventStatus(LibraryEventStatusEnum.BOOK_ADDED)
                .book(book)
                .build();

        //then
        assertThrows(ExecutionException.class,()-> libraryEventsProducer.sendMessageWithKey(libraryEvent).get());
    }
}
