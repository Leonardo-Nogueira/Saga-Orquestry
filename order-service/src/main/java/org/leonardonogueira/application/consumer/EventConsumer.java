package org.leonardonogueira.application.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.service.EventService;
import org.leonardonogueira.application.utils.JsonUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class EventConsumer {

    private final JsonUtils jsonUtils;
    private final EventService eventService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.notify-ending}"
    )
    public void eventNotifyEndingConsumer(String payload){
        log.info("Received notify ending event {} from notify ending topic", payload);
        var event = jsonUtils.toEvent(payload);
        eventService.notifyEnding(event);
    }

}
