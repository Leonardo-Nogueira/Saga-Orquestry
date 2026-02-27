package org.leonardonogueira.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.domain.Payment;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.dto.History;
import org.leonardonogueira.application.dto.ProductOrder;
import org.leonardonogueira.application.enums.PaymentStatusEnum;
import org.leonardonogueira.application.enums.SagaStatusEnum;
import org.leonardonogueira.application.producer.KafkaProducer;
import org.leonardonogueira.application.repository.PaymentRepository;
import org.leonardonogueira.application.utils.JsonUtils;
import org.leonardonogueira.config.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";

    private final PaymentRepository paymentRepository;
    private final JsonUtils jsonUtils;
    private final KafkaProducer producer;

    public void createPayment(Event event) {
        try {
            checkDuplicateTransaction(event);
            Payment payment = buildPendingPayment(event);

            validatePaymentAmount(payment.getTotalAmount());
            savePayment(payment);

            updatePaymentStatus(payment, PaymentStatusEnum.SUCCESS);
            handleSuccess(event, payment);

        } catch (Exception e) {
            log.error("Error creating payment for Transaction ID: {}", event.getTransactionId(), e);
            handleRollback(event, e.getMessage());
        } finally {
            sendEventToKafka(event);
        }
    }

    public void rollbackPayment(Event event) {
        try {
            var payment = paymentRepository
                    .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                            .orElseThrow(() -> new ValidationException("Payment not found for Transaction ID: " + event.getTransactionId()));

            updatePaymentStatus(payment, PaymentStatusEnum.REFUND);
            syncEventWithPayment(event, payment);

            updateEventStatus(event, SagaStatusEnum.FAILED, "Rollback executed for payment ");
        } catch (Exception e) {
            handleFail(event, e.getMessage());
            log.error("Error executing rollback for Transaction ID: {}", event.getTransactionId(), e);
        } finally {
            sendEventToKafka(event);
        }
    }

    private void validatePaymentAmount(double amount) {
        if (amount < 0.1) {
            throw new ValidationException("The minimum amount for payment is 0.1");
        }
    }

    private void handleSuccess(Event event, Payment payment) {
        syncEventWithPayment(event, payment);
        updateEventStatus(event, SagaStatusEnum.SUCCESS, "Payment successfully validated and processed");
    }

    private void handleFail(Event event, String message) {
        updateEventStatus(event, SagaStatusEnum.FAILED, "Fail: " + message);
    }

    private void handleRollback(Event event, String message) {
        updateEventStatus(event, SagaStatusEnum.ROLLBACK, "Fail to realize the payment, rollback was required because: " + message);
    }

    private void updateEventStatus(Event event, SagaStatusEnum status, String message) {
        event.setStatus(status);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, message);
    }

    private void addHistory(Event event, String message) {
        var history = History.builder()
                .source(CURRENT_SOURCE)
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addHistory(history);
    }

    private Payment buildPendingPayment(Event event) {
        return Payment.builder()
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .totalItems(calculateTotalItems(event))
                .totalAmount(calculateTotalAmount(event))
                .status(PaymentStatusEnum.PENDING)
                .build();
    }

    private void updatePaymentStatus(Payment payment, PaymentStatusEnum status) {
        payment.setStatus(status);
        paymentRepository.save(payment);
    }

    private void syncEventWithPayment(Event event, Payment payment) {
        event.getPayload().setTotalItems(payment.getTotalItems());
        event.getPayload().setTotalAmount(payment.getTotalAmount());
    }

    private double calculateTotalAmount(Event event) {
        return event
                .getPayload()
                .getProducts()
                .stream()
                .map(product -> product.getQuantity() * product.getProduct().getUnitValue())
                .reduce(0.0, Double::sum);
    }

    private int calculateTotalItems(Event event) {
        return event
                .getPayload()
                .getProducts()
                .stream()
                .map(ProductOrder::getQuantity)
                .reduce(0, Integer::sum);
    }

    private void checkDuplicateTransaction(Event event) {
        if (paymentRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())) {
            throw new ValidationException("Payment already processed for this transaction.");
        }
    }

    private void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    private void sendEventToKafka(Event event) {
        producer.sendEvent(jsonUtils.toJson(event));
    }

}
