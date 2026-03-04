package org.leonardonogueira.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.dto.History;
import org.leonardonogueira.application.enums.EventTopicsEnum;
import org.leonardonogueira.application.orchestrator.SagaExecutionController;
import org.leonardonogueira.application.producer.KafkaProducer;
import org.leonardonogueira.application.utils.JsonUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.leonardonogueira.application.enums.EventSourceEnum.ORCHESTRATOR;
import static org.leonardonogueira.application.enums.EventStatusEnum.FAILED;
import static org.leonardonogueira.application.enums.EventStatusEnum.SUCCESS;
import static org.leonardonogueira.application.enums.EventTopicsEnum.NOTIFY_ENDING;

@Slf4j
@Service
@AllArgsConstructor
public class OrchestratorService {

    private final SagaExecutionController sagaExecutionController;
    private final SagaEventService saga;

    public void startSaga(Event event){
        log.info("SAGA STARTED!");

        var topic = getNextTopics(event);
        saga.handleSuccess(event, "Saga started!");
        saga.sendEvent(event, topic.getTopic());
    }

    public void continueSaga(Event event){
        log.info("SAGA CONTINUING FOR EVENT {}", event.getId());

        var topic = getNextTopics(event);
        saga.sendToProducer(event, topic);
    }

    public void finishSagaSuccess(Event event){
        log.info("SAGA FINISHED SUCCESSFULLY FOR EVENT {}!", event.getId());

        saga.handleSuccess(event, "Saga finished successfully!");
        saga.notifyEnding(event);
    }

    public void finishSagaFail(Event event){
        log.info("SAGA FINISHED WITH ERRORS FOR EVENT {}!", event.getId());

        saga.handleRollback(event, "Saga finished with errors!");
        saga.notifyEnding(event);
    }

    private EventTopicsEnum getNextTopics(Event event){
        return sagaExecutionController.getNextEventTopics(event);
    }

}
