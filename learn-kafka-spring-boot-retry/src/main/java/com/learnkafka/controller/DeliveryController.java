package com.learnkafka.controller;

import com.learnkafka.dto.Delivery;
import com.learnkafka.service.DeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DeliveryController {

    @Autowired
    DeliveryService deliveryService;

    @PostMapping("/v1/delivery")
    public ResponseEntity<?> createDelivery(@RequestBody Delivery delivery){

        return ResponseEntity.ok().body(deliveryService.createDelivery(delivery));

    }
}
