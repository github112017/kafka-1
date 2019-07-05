package com.learnkafka.dto

import spock.lang.Specification

class DeliverySpec extends Specification{

    def "Delivery JSON Value "(){
        given:
        Delivery delivery = new Delivery();
        delivery.deliveryId= "12345"
        delivery.item = getItem()
        delivery.dropAddresss = getDropAddress()
        delivery.pickUpAddress = getPickUpAddress()

        when:
        String inputJson = delivery.jsonValue();
        println(inputJson)

        then:
        inputJson!=null
    }

    def static getItem(){

        Item item = new Item();
        item.setItemDescription("Apple Ipad")
        item.setItemId(123)
        return item
    }

    def static getPickUpAddress(){

        return new Address().builder()
        .addressLine1("123 ABC Ln")
        .city("Apple Valley")
        .state("MN")
        .postalCode("12345")
        .build();

    }

    def static getDropAddress(){

        return new Address().builder()
                .addressLine1("456 DEF Ln")
                .city("Minneapolis")
                .state("MN")
                .postalCode("55134")
                .build();

    }


}
