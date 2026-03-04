package org.leonardonogueira.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.domain.Payment;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.enums.PaymentStatusEnum;
import org.leonardonogueira.application.enums.EventStatusEnum;
import org.leonardonogueira.application.repository.PaymentRepository;
import org.leonardonogueira.config.exception.ValidationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentRollbackService {

    private final PaymentRepository paymentRepository;
    private final SagaEventService saga;

    public void rollbackPayment(Event event) {
        try {
            var payment = paymentRepository
                    .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                            .orElseThrow(() -> new ValidationException("Payment not found for Transaction ID: " + event.getTransactionId()));

            updatePaymentStatus(payment, PaymentStatusEnum.REFUND);
            saga.syncEventWithPayment(event, payment);

            saga.updateEventStatus(event, EventStatusEnum.FAILED, "Rollback executed for payment ");
        } catch (Exception e) {
            saga.handleFail(event, e.getMessage());
            log.error("Error executing rollback for Transaction ID: {}", event.getTransactionId(), e);
        } finally {
            saga.sendEvent(event);
        }
    }

    private void updatePaymentStatus(Payment payment, PaymentStatusEnum status) {
        payment.setStatus(status);
        paymentRepository.save(payment);
    }

}
