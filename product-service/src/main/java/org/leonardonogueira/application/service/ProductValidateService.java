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
public class ProductValidateService {

    private final ProductRepository productRepository;
    private final ValidationRepository validationRepository;
    private final SagaEventService saga;

    public void validateExistsProduct(Event event) {
        try {
            validateEventData(event);
            checkDuplicateTransaction(event);
            validateProductsInPayload(event);
            createValidation(event, true);
            saga.handleSuccess(event);
        } catch (Exception e) {
            log.error("Validation failed for transaction {}: {}", event.getTransactionId(), e.getMessage());
            saga.handleFail(event, e.getMessage());
        } finally {
            saga.sendEvent(event);
        }
    }

    private void checkDuplicateTransaction(Event event) {
        if (validationRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())) {
            throw new ValidationException("Order already processed for this transaction.");
        }
    }

    private void validateProductsInPayload(Event event) {
        event.getPayload().getProducts().forEach(order -> {
            if (isEmpty(order.getProduct()) || isEmpty(order.getProduct().getCode())) {
                throw new ValidationException("Product data is missing in order.");
            }

            if (!productRepository.existsByCode(order.getProduct().getCode())) {
                throw new ValidationException("Product not found: " + order.getProduct().getCode());
            }
        });
    }

    private void createValidation(Event event, boolean success) {
        var validation = Validation.builder()
                .orderId(event.getOrderId())
                .transactionId(event.getTransactionId())
                .success(success)
                .build();
        validationRepository.save(validation);
    }

    private void validateEventData(Event event) {
        if (isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts())) {
            throw new ValidationException("Product orders list is empty.");
        }
        if (isEmpty(event.getOrderId()) || isEmpty(event.getTransactionId())) {
            throw new ValidationException("Order ID or Transaction ID missing.");
        }
    }
}
