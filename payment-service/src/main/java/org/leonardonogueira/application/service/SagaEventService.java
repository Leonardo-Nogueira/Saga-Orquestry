package org.leonardonogueira.application.service;

import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.domain.Payment;
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

    private static final String PAYMENT_SERVICE = "PAYMENT_SERVICE";

    private final KafkaProducer producer;
    private final JsonUtils jsonUtils;

    public SagaEventService(KafkaProducer producer, JsonUtils jsonUtils) {
        this.producer = producer;
        this.jsonUtils = jsonUtils;
    }

    public void handleSuccess(Event event, Payment payment) {
        syncEventWithPayment(event, payment);
        updateEventStatus(event, EventStatusEnum.SUCCESS, "Payment successfully updated");
    }

    public void syncEventWithPayment(Event event, Payment payment) {
        event.getPayload().setTotalItems(payment.getTotalItems());
        event.getPayload().setTotalAmount(payment.getTotalAmount());
    }

    public void handleFail(Event event, String message) {
        updateEventStatus(event, EventStatusEnum.ROLLBACK, "Fail: " + message);
    }

    public void handleRollback(Event event) {
        updateEventStatus(event, EventStatusEnum.FAILED, "Rollback executed successfully");
    }

    public void updateEventStatus(Event event, EventStatusEnum status, String message) {
        event.setStatus(status);
        event.setSource(PAYMENT_SERVICE);
        addHistory(event, message);
        log.info("Saga Event updated: [Transaction: {} | Source: {} | Status: {}]",
                event.getTransactionId(), PAYMENT_SERVICE, status);
    }

    private void addHistory(Event event, String message) {
        var history = History.builder()
                .source(PAYMENT_SERVICE)
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