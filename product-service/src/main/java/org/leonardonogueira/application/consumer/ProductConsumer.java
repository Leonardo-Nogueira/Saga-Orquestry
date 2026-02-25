package org.leonardonogueira.application.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.utils.JsonUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ProductConsumer {

    private final JsonUtils jsonUtils;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.product-success}"
    )
    public void eventProductSuccessConsumer(String payload){
        log.info("Received product success event {} from product sucess topic", payload);
        var event = jsonUtils.toEvent(payload);

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.product-fail}"
    )
    public void eventProductFailConsumer(String payload){
        log.info("Received product fail event {} from product fail topic", payload);
        var event = jsonUtils.toEvent(payload);

    }
}
