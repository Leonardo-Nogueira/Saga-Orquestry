package org.leonardonogueira.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.domain.Validation;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.producer.KafkaProducer;
import org.leonardonogueira.application.repository.ProductRepository;
import org.leonardonogueira.application.repository.ValidationRepository;
import org.leonardonogueira.application.utils.JsonUtils;
import org.leonardonogueira.config.exception.ValidationException;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class ProductRollbackService {

    private final ValidationRepository validationRepository;
    private final SagaEventService saga;

    private void createValidation(Event event, boolean success) {
        var validation = Validation.builder()
                .orderId(event.getOrderId())
                .transactionId(event.getTransactionId())
                .success(success)
                .build();
        validationRepository.save(validation);
    }

    public void rollback(Event event) {
        try {
            changeValidationToFail(event);
            saga.handleRollback(event);
        } catch (Exception e) {
            log.error("Rollback critical error for transaction {}", event.getTransactionId(), e);
        } finally {
            saga.sendEvent(event);
        }
    }

    private void changeValidationToFail(Event event) {
        validationRepository
                .findByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())
                .ifPresentOrElse(
                        validation -> {
                            validation.setSuccess(false);
                            validationRepository.save(validation);
                        }, () -> createValidation(event, false)
                );
    }
}
