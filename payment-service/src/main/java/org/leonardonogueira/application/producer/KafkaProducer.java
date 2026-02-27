package org.leonardonogueira.application.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.config.kafka.KafkaTopics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value(KafkaTopics.ORCHESTRATOR)
    private String orchestratorTopic;

    public void sendEvent(String message) {
        try {
            log.info("Sending message to topic {} with data : {}", orchestratorTopic, message);
            kafkaTemplate.send(orchestratorTopic, message);
        }catch (Exception e) {
            log.error("Error trying to send message to topic {} with data {}", orchestratorTopic, message);
        }
    }

}
