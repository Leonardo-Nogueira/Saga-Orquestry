package org.leonardonogueira.application.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.utils.JsonUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class InventoryConsumer {

    private final JsonUtils jsonUtils;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.inventory-success}"
    )
    public void eventInventorySuccessConsumer(String payload){
        log.info("Received inventory success event {} from inventory sucess topic", payload);
        var event = jsonUtils.toEvent(payload);

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.inventory-fail}"
    )
    public void eventInventoryFailConsumer(String payload){
        log.info("Received inventory fail event {} from inventory fail topic", payload);
        var event = jsonUtils.toEvent(payload);

    }
}
