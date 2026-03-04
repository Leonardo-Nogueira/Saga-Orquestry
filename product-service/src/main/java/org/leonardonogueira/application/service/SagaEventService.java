package org.leonardonogueira.application.service;

import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.dto.History;
import org.leonardonogueira.application.enums.EventStatusEnum;
import org.leonardonogueira.application.producer.KafkaProducer;
import org.leonardonogueira.application.utils.JsonUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SagaEventService {

    private final KafkaProducer producer;
    private final JsonUtils jsonUtils;

    private static final String PRODUCT_SERVICE = "PRODUCT_SERVICE";

    public SagaEventService(KafkaProducer producer, JsonUtils jsonUtils) {
        this.producer = producer;
        this.jsonUtils = jsonUtils;
    }

    public void handleSuccess(Event event) {
        updateEventStatus(event, EventStatusEnum.SUCCESS, "Product successfully updated");
    }

    public void handleFail(Event event, String message) {
        updateEventStatus(event, EventStatusEnum.ROLLBACK, "Fail: " + message);
    }

    public void handleRollback(Event event) {
        updateEventStatus(event, EventStatusEnum.FAILED, "Rollback executed successfully");
    }

    private void updateEventStatus(Event event, EventStatusEnum status, String message) {
        event.setStatus(status);
        event.setSource(PRODUCT_SERVICE);
        addHistory(event, message);
        log.info("Saga Event updated: [Transaction: {} | Source: {} | Status: {}]",
                event.getTransactionId(), PRODUCT_SERVICE, status);
    }

    private void addHistory(Event event, String message) {
        var history = History.builder()
                .source(PRODUCT_SERVICE)
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addHistory(history);
    }

    public void sendEvent(Event event) {
        var payload = jsonUtils.toJson(event);
        producer.sendEvent(payload);
    }
}