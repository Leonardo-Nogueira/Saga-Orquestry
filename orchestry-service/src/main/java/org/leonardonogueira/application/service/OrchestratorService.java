package org.leonardonogueira.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.dto.History;
import org.leonardonogueira.application.enums.EventTopicsEnum;
import org.leonardonogueira.application.orchestrator.SagaExecutionController;
import org.leonardonogueira.application.producer.OrchestryProducer;
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

    private final OrchestryProducer orchestryProducer;
    private final SagaExecutionController  sagaExecutionController;
    private final JsonUtils jsonUtil;

    public void startSaga(Event event){
        event.setSource(ORCHESTRATOR);
        event.setStatus(SUCCESS);
        var topic = getNextTopics(event);
        log.info("SAGA STARTED!");
        addHistory(event, "Saga started!");
        orchestryProducer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
    }

    public void continueSaga(Event event){
        var topic = getNextTopics(event);
        log.info("SAGA CONTINUING FOR EVENT {}", event.getId());
        sendToProducer(event, topic);
    }

    public void finishSagaSuccess(Event event){
        event.setSource(ORCHESTRATOR);
        event.setStatus(SUCCESS);
        log.info("SAGA FINISHED SUCCESSFULLY FOR EVENT {}!", event.getId());
        addHistory(event, "Saga finished successfully!");
        notifyEnding(event);
    }

    public void finishSagaFail(Event event){
        event.setSource(ORCHESTRATOR);
        event.setStatus(FAILED);
        log.info("SAGA FINISHED WITH ERRORS FOR EVENT {}!", event.getId());
        addHistory(event, "Saga finished with errors!");
        notifyEnding(event);
    }

    private void notifyEnding(Event event){
        orchestryProducer.sendEvent(jsonUtil.toJson(event), NOTIFY_ENDING.getTopic());
    }

    private EventTopicsEnum getNextTopics(Event event){
        return sagaExecutionController.getNextEventTopics(event);
    }

    private void sendToProducer(Event event, EventTopicsEnum topic) {
        orchestryProducer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
    }

    private void addHistory(Event event, String message) {
        var history = History.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        event.addHistory(history);
    }

}
