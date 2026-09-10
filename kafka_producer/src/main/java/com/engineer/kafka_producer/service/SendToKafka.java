package com.engineer.kafka_producer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SendToKafka {

    private static final Logger logger = LoggerFactory.getLogger(SendToKafka.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessageToTopic(String message) {
        var future = kafkaTemplate.send("orders", message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Message sent: [{}] with offset: [{}]", message,
                    result.getRecordMetadata().offset());
            } else {
                logger.error("Failed to send message: {}", ex.getMessage(), ex);
            }
        });
    }
}



