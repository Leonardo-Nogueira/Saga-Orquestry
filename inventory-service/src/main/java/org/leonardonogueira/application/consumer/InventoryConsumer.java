package org.leonardonogueira.application.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.mapper.SagaMapper;
import org.leonardonogueira.application.service.InventoryService;
import org.leonardonogueira.application.utils.JsonUtils;
import org.leonardonogueira.avro.command.SagaEvent;
import org.leonardonogueira.config.kafka.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class InventoryConsumer {

    private final JsonUtils jsonUtils;
    private final InventoryService inventoryService;
    private final SagaMapper mapper;

    @KafkaListener(groupId = KafkaTopics.GROUP_ID, topics = KafkaTopics.INVENTORY_SUCCESS_TOPIC)
    public void eventInventorySuccessConsumer(SagaEvent payload){
        log.info("Received inventory success event {} from inventory sucess topic", payload);
        var event = mapper.toDomain(payload);
        inventoryService.updateInventory(event);
    }

    @KafkaListener(groupId = KafkaTopics.GROUP_ID, topics = KafkaTopics.INVENTORY_FAIL_TOPIC)
    public void eventInventoryFailConsumer(SagaEvent payload){
        log.info("Received inventory fail event {} from inventory fail topic", payload);
        var event = mapper.toDomain(payload);
        inventoryService.rollbackInventory(event);

    }

}
