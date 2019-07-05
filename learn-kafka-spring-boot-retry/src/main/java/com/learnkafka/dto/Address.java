package com.learnkafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    private String addressLine1;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}
