package org.leonardonogueira.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.domain.Payment;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.dto.ProductOrder;
import org.leonardonogueira.application.enums.PaymentStatusEnum;
import org.leonardonogueira.application.repository.PaymentRepository;
import org.leonardonogueira.config.exception.ValidationException;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class PaymentCreateService {

    private final PaymentRepository paymentRepository;
    private final SagaEventService saga;

    public void createPayment(Event event) {
        try {
            checkDuplicateTransaction(event);
            Payment payment = buildPendingPayment(event);

            validatePaymentAmount(payment.getTotalAmount());
            savePayment(payment);

            updatePaymentStatus(payment, PaymentStatusEnum.SUCCESS);
            saga.handleSuccess(event, payment);

        } catch (Exception e) {
            log.error("Error creating payment for Transaction ID: {}", event.getTransactionId(), e);
            saga.handleRollback(event);
        } finally {
            saga.sendEvent(event);
        }
    }

    private void validatePaymentAmount(double amount) {
        if (amount < 0.1) {
            throw new ValidationException("The minimum amount for payment is 0.1");
        }
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

}
