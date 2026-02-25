package org.leonardonogueira.application.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.utils.JsonUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentConsumer {

    private final JsonUtils jsonUtils;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.payment-success}"
    )
    public void eventPaymentSuccessConsumer(String payload){
        log.info("Received payment success event {} from payment sucess topic", payload);
        var event = jsonUtils.toEvent(payload);

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.payment-fail}"
    )
    public void eventPaymentFailConsumer(String payload){
        log.info("Received payment fail event {} from payment fail topic", payload);
        var event = jsonUtils.toEvent(payload);

    }
}
