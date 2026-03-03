package org.leonardonogueira.application.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.service.OrchestratorService;
import org.leonardonogueira.application.utils.JsonUtils;
import org.leonardonogueira.config.kafka.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OrchestryConsumer {

    private final JsonUtils jsonUtils;
    private final OrchestratorService service;

    @KafkaListener(groupId = KafkaTopics.GROUP_ID, topics = KafkaTopics.START_SAGA_TOPIC)
    public void consumeStartSagaEvent(String payload) {
        log.info("Receiving event {} from start-saga topic", payload);
        var event = jsonUtils.toEvent(payload);
        service.startSaga(event);
    }

    @KafkaListener(groupId = KafkaTopics.GROUP_ID, topics = KafkaTopics.ORCHESTRATOR_TOPIC)
    public void consumeOrchestratorEvent(String payload) {
        log.info("Receiving event {} from orchestrator topic", payload);
        var event = jsonUtils.toEvent(payload);
        service.continueSaga(event);
    }

    @KafkaListener(groupId = KafkaTopics.GROUP_ID, topics = KafkaTopics.FINISH_SUCCESS)
    public void consumeFinishSagaSuccessEvent(String payload) {
        log.info("Receiving event {} from finish-success topic", payload);
        var event = jsonUtils.toEvent(payload);
        service.finishSagaSuccess(event);
    }

    @KafkaListener(groupId = KafkaTopics.GROUP_ID, topics = KafkaTopics.FINISH_FAIL)
    public void consumeFinishSagaFailEvent(String payload) {
        log.info("Receiving event {} from finish-fail topic", payload);
        var event = jsonUtils.toEvent(payload);
        service.finishSagaFail(event);
    }

}
