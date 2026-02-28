package org.leonardonogueira.application.orchestrator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.enums.EventSourceEnum;
import org.leonardonogueira.application.enums.EventStatusEnum;
import org.leonardonogueira.application.enums.EventTopicsEnum;
import org.leonardonogueira.config.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static java.awt.event.FocusEvent.Cause.ROLLBACK;
import static java.lang.String.format;
import static org.leonardonogueira.application.enums.EventStatusEnum.FAILED;
import static org.leonardonogueira.application.enums.EventStatusEnum.SUCCESS;
import static org.leonardonogueira.application.orchestrator.OrchestratorSagaHandler.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Component
@AllArgsConstructor
public class SagaExecutionController {

    public EventTopicsEnum getNextEventTopics(Event event) {
        if (isEmpty(event.getSource()) || isEmpty(event.getStatus())) {
            throw new ValidationException("Source and status are required");
        }

        var source = EventSourceEnum.fromString(event.getSource());
        var status = EventStatusEnum.fromString(event.getStatus());

        var topic = OrchestratorSagaHandler.getNextTopic(source, status);

        logCurrentTopic(event, topic);
        return topic;
    }

    private void logCurrentTopic(Event event, EventTopicsEnum topic) {

        var sagaId = createSagaId(event);
        var source = event.getSource();

        switch (event.getStatus()) {
            case SUCCESS -> log.info("### CURRENT SAGA: {} | SUCCESS | NEXT TOPIC {} | {}",
                    source, topic, sagaId);
            case ROLLBACK -> log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK CURRENT SERVICE | NEXT TOPIC {} | {}",
                    source, topic, sagaId);
            case FAILED -> log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK PREVIOUS SERVICE | NEXT TOPIC {} | {}",
                    source, topic, sagaId);
        }
    }

    private String createSagaId(Event event) {
        return format("ORDER ID: %s | TRANSACTION ID %s | EVENT ID %s",
                event.getPayload().getId(), event.getTransactionId(), event.getId());
    }

}
