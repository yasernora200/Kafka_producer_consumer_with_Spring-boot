package com.engineer.kafka_producer.controller; 

import com.engineer.kafka_producer.service.SendToKafka; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/producer-app")
public class ApiBetweenMeProducer {

    @Autowired
    private SendToKafka publisher;

    @GetMapping("/publish/{message}")
    public ResponseEntity<?> publishMessage(@PathVariable String message) {
        try {
            for(int i=0; i<=10000; i++){
                publisher.sendMessageToTopic(message+" "+i);
            }
            return ResponseEntity.ok("Message published successfully...");

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}