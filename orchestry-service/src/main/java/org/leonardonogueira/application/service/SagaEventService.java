package org.leonardonogueira.application.service;

import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.dto.History;
import org.leonardonogueira.application.enums.EventStatusEnum;
import org.leonardonogueira.application.enums.EventTopicsEnum;
import org.leonardonogueira.application.producer.KafkaProducer;
import org.leonardonogueira.application.utils.JsonUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static org.leonardonogueira.application.enums.EventSourceEnum.ORCHESTRATOR;
import static org.leonardonogueira.application.enums.EventTopicsEnum.NOTIFY_ENDING;

@Slf4j
@Component
public class SagaEventService {

    private final KafkaProducer producer;
    private final JsonUtils jsonUtils;

    public SagaEventService(KafkaProducer producer, JsonUtils jsonUtils) {
        this.producer = producer;
        this.jsonUtils = jsonUtils;
    }

    public void handleSuccess(Event event, String message) {
        updateEventStatus(event, EventStatusEnum.SUCCESS, message);
    }

    public void handleFail(Event event, String message) {
        updateEventStatus(event, EventStatusEnum.ROLLBACK, "Fail: " + message);
    }

    public void handleRollback(Event event, String message) {
        updateEventStatus(event, EventStatusEnum.FAILED, message);
    }

    public void updateEventStatus(Event event, EventStatusEnum status, String message) {
        event.setStatus(status);
        event.setSource(ORCHESTRATOR);
        addHistory(event, message);
        log.info("Saga Event updated: [Transaction: {} | Source: {} | Status: {}]",
                event.getTransactionId(), ORCHESTRATOR, status);
    }

    private void addHistory(Event event, String message) {
        var history = History.builder()
                .source(ORCHESTRATOR)
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addHistory(history);
    }

    public void sendEvent(Event event, String topic) {
        var payload = jsonUtils.toJson(event);
        producer.sendEvent(payload, topic);
    }

    public void notifyEnding(Event event){
        var payload = jsonUtils.toJson(event);
        producer.sendEvent(payload, NOTIFY_ENDING.getTopic());
    }

    public void sendToProducer(Event event, EventTopicsEnum topic) {
        var payload = jsonUtils.toJson(event);
        producer.sendEvent(payload, topic.getTopic());
    }
}