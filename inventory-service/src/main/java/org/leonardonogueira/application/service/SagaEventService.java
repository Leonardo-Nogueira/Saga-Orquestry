package org.leonardonogueira.application.service;

import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.dto.History;
import org.leonardonogueira.application.enums.SagaStatusEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SagaEventService {

    private static final String INVENTORY_SERVICE = "INVENTORY_SERVICE";

    public void handleSuccess(Event event) {
        updateEventStatus(event, SagaStatusEnum.SUCCESS, "Inventory successfully updated");
    }

    public void handleFail(Event event, String message) {
        updateEventStatus(event, SagaStatusEnum.ROLLBACK, "Fail: " + message);
    }

    public void handleRollback(Event event) {
        updateEventStatus(event, SagaStatusEnum.FAILED, "Rollback executed successfully");
    }

    private void updateEventStatus(Event event, SagaStatusEnum status, String message) {
        event.setStatus(status);
        event.setSource(INVENTORY_SERVICE);
        addHistory(event, message);
        log.info("Saga Event updated: [Transaction: {} | Source: {} | Status: {}]",
                event.getTransactionId(), INVENTORY_SERVICE, status);
    }

    private void addHistory(Event event, String message) {
        var history = History.builder()
                .source(INVENTORY_SERVICE)
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addHistory(history);
    }
}