package org.leonardonogueira.application.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.utils.JsonUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrquestratorConsumer {

    private final JsonUtils jsonUtils;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.start-saga}"
    )
    public void eventStartSagaConsumer(String payload){
        log.info("Received start saga  event {} from start saga topic", payload);
        var event = jsonUtils.toEvent(payload);

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.orquestrator}"
    )
    public void eventOrquestratorConsumer(String payload){
        log.info("Received orquestrator event {} from orquestrator topic", payload);
        var event = jsonUtils.toEvent(payload);

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.finish-success}"
    )
    public void eventFinishSuccessConsumer(String payload){
        log.info("Received finish success event {} from finish sucess topic", payload);
        var event = jsonUtils.toEvent(payload);

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.finish-fail}"
    )
    public void eventFinishFailConsumer(String payload){
        log.info("Received finish fail event {} from finish fail topic", payload);
        var event = jsonUtils.toEvent(payload);

    }
}
