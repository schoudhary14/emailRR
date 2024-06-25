package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.model.EmailData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
    private static final Logger logger = LogManager.getLogger(KafkaService.class);
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void queueRequest(String topicName, EmailData message){
        kafkaTemplate.send(topicName, message);
    }

}
