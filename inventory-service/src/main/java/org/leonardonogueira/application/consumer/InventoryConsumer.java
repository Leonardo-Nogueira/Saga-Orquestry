package org.leonardonogueira.application.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.service.InventoryService;
import org.leonardonogueira.application.utils.JsonUtils;
import org.leonardonogueira.config.kafka.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class InventoryConsumer {

    private final JsonUtils jsonUtils;
    private final InventoryService inventoryService;

    @KafkaListener(groupId = KafkaTopics.GROUP_ID, topics = KafkaTopics.INVENTORY_SUCCESS_TOPIC)
    public void eventInventorySuccessConsumer(String payload){
        log.info("Received inventory success event {} from inventory sucess topic", payload);
        var event = jsonUtils.toEvent(payload);
        inventoryService.updateInventory(event);
    }

    @KafkaListener(groupId = KafkaTopics.GROUP_ID, topics = KafkaTopics.INVENTORY_FAIL_TOPIC)
    public void eventInventoryFailConsumer(String payload){
        log.info("Received inventory fail event {} from inventory fail topic", payload);
        var event = jsonUtils.toEvent(payload);
        inventoryService.rollbackInventory(event);

    }
}
