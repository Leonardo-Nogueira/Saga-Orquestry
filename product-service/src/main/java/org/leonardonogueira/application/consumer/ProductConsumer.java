package org.leonardonogueira.application.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.service.ProductService;
import org.leonardonogueira.application.utils.JsonUtils;
import org.leonardonogueira.config.kafka.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ProductConsumer {

    private final JsonUtils jsonUtils;
    private final ProductService productService;

    @KafkaListener(groupId = KafkaTopics.GROUP_ID, topics = KafkaTopics.PRODUCT_SUCCESS_TOPIC)
    public void consumeSuccessEvent(String payload) {
        log.info("Receiving success event from orchestrator: {}", payload);
        try {
            var event = jsonUtils.toEvent(payload);
            productService.validateExistsProduct(event);
        } catch (Exception e) {
            log.error("Critical error deserializing or processing success event: {}", payload, e);
        }
    }

    @KafkaListener(groupId = KafkaTopics.GROUP_ID, topics = KafkaTopics.PRODUCT_FAIL_TOPIC)
    public void consumeFailEvent(String payload) {
        log.info("Receiving rollback event from orchestrator: {}", payload);
        try {
            var event = jsonUtils.toEvent(payload);
            productService.rollback(event);
        } catch (Exception e) {
            log.error("Critical error deserializing or processing rollback event: {}", payload, e);
        }
    }
}
