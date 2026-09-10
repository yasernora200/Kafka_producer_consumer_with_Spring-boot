package com.engineer.kafka_consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component 
public class Consumer {

    private static final Logger log = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(topics = "orders", groupId = "orders-group")
    public void consume1(String message) {
        log.info("consumer1 consume the message {} ", message);
    }
    @KafkaListener(topics = "orders", groupId = "orders-group")
    public void consume2(String message) {
        log.info("consumer2 consume the message {} ", message);
    }
    @KafkaListener(topics = "orders", groupId = "orders-group")
    public void consume3(String message) {
        log.info("consumer3 consume the message {} ", message);
    }
    @KafkaListener(topics = "orders", groupId = "orders-group")
    public void consume4(String message) {
        log.info("consumer4 consume the message {} ", message);
    }
}