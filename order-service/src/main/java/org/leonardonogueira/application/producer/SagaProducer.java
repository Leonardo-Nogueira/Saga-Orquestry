package org.leonardonogueira.application.producer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.start-saga}")
    private String starSagaTopic;

    public void sendEvent(String message) {
        try {
            log.info("Sending message to topic {} with data : {}", starSagaTopic, message);
            kafkaTemplate.send(starSagaTopic, message);
        }catch (Exception e) {
            log.error("Error trying to send message to topic {} with data {}", starSagaTopic, message);
        }
    }

}
