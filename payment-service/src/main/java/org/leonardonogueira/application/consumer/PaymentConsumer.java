package org.leonardonogueira.application.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.service.PaymentCreateService;
import org.leonardonogueira.application.service.PaymentRollbackService;
import org.leonardonogueira.application.utils.JsonUtils;
import org.leonardonogueira.config.kafka.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentConsumer {

    private final PaymentCreateService paymentCreateService;
    private final PaymentRollbackService paymentRollbackService;
    private final JsonUtils jsonUtils;

    @KafkaListener(groupId = KafkaTopics.GROUP_ID, topics = KafkaTopics.PAYMENT_SUCCESS_TOPIC)
    public void eventPaymentSuccessConsumer(String payload){
        log.info("Received payment success event {} from payment-success topic", payload);
        var event = jsonUtils.toEvent(payload);
        paymentCreateService.createPayment(event);
    }

    @KafkaListener(groupId = KafkaTopics.GROUP_ID, topics = KafkaTopics.PAYMENT_FAIL_TOPIC)
    public void eventPaymentFailConsumer(String payload){
        log.info("Received payment fail event {} from payment-fail topic", payload);
        var event = jsonUtils.toEvent(payload);
        paymentRollbackService.rollbackPayment(event);
    }
}
