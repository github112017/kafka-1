package com.learnkafka.service;

import com.learnkafka.dto.Delivery;
import com.learnkafka.producer.DeliveryKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeliveryService {

    @Autowired
    DeliveryKafkaProducer deliveryKafkaProducer;

    public Delivery createDelivery(Delivery delivery){

        deliveryKafkaProducer.sendMessage(delivery);
        return delivery;
    }
}
