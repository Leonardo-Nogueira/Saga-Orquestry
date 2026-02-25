package org.leonardonogueira.application.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.orquestrator}")
    private String orquestratorTopic;

    public void sendEvent(String message) {
        try {
            log.info("Sending message to topic {} with data : {}", orquestratorTopic, message);
            kafkaTemplate.send(orquestratorTopic, message);
        }catch (Exception e) {
            log.error("Error trying to send message to topic {} with data {}", orquestratorTopic, message);
        }
    }

}
