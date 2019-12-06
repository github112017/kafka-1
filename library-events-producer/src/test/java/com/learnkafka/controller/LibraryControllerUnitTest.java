package com.learnkafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.domain.LibraryEventStatusEnum;
import com.learnkafka.producer.LibraryEventsProducer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
public class LibraryControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LibraryEventsProducer libraryEventsProducer;

    @Autowired
    LibraryEventController libraryEventController;


    ObjectMapper objectMapper = new ObjectMapper();

    String topic;

    @BeforeEach
    void setTopic() {
        ReflectionTestUtils.setField(libraryEventController, "topic", topic);
    }

    @Test
    void postLibraryEvent() throws Exception {

        //given
        Book book = new Book().builder()
                .bookId(123)
                .bookAuthor("Dilip")
                .bookName("Kafka Using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(123)
                .book(book)
                .build();
        String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventsProducer.sendMessage(libraryEvent, topic)).thenReturn(null);

        //expect
        mockMvc.perform(
                post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


    }

    @Test
    void postLibraryEvent_with_null_EventId() throws Exception {

        //given
        String expectedErrorMessage = "book.bookId - must not be null, libraryEventId - must not be null";
        Book book = new Book().builder()
                .bookId(null)
                .bookAuthor("Dilip")
                .bookName("Kafka Using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();
        String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventsProducer.sendMessage(libraryEvent, topic)).thenReturn(null);

        //expect
        mockMvc.perform(
                post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print())
        .andExpect(content().string(expectedErrorMessage));

    }

    @Test
    void postLibraryEvent_exception() throws Exception {

        //given
        Book book = new Book().builder()
                .bookId(123)
                .bookAuthor("Dilip")
                .bookName("Kafka Using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(123)
                .book(book)
                .build();
        String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventsProducer.sendMessage(libraryEvent, topic)).thenThrow(new RuntimeException("Exception Occurred"));

        //expect
        mockMvc.perform(
                post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());


    }

    @Test
    void updateLibraryEvent() throws Exception {

        //given
        Book book = new Book().builder()
                .bookId(123)
                .bookAuthor("Dilip")
                .bookName("Kafka Using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(123)
                .book(book)
                .build();
        String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventsProducer.sendMessage(libraryEvent, topic)).thenReturn(null);

        //expect
        mockMvc.perform(
                put("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


    }
}
