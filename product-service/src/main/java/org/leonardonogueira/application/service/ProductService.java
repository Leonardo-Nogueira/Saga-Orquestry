package org.leonardonogueira.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.domain.Validation;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.dto.History;
import org.leonardonogueira.application.enums.SagaStatusEnum;
import org.leonardonogueira.application.producer.KafkaProducer;
import org.leonardonogueira.application.repository.ProductRepository;
import org.leonardonogueira.application.repository.ValidationRepository;
import org.leonardonogueira.application.utils.JsonUtils;
import org.leonardonogueira.config.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class ProductService {

    private static final String PRODUCT_SERVICE = "PRODUCT_SERVICE";

    private final ProductRepository productRepository;
    private final ValidationRepository validationRepository;
    private final JsonUtils jsonUtils;
    private final KafkaProducer producer;

    public void validateExistsProduct(Event event) {
        try {
            validateEventData(event);
            checkDuplicateTransaction(event);
            validateProductsInPayload(event);

            createValidation(event, true);
            handleSuccess(event);
        } catch (Exception e) {
            log.error("Validation failed for transaction {}: {}", event.getTransactionId(), e.getMessage());
            handleFail(event, e.getMessage());
        } finally {
            sendCommand(event);
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

    private void handleSuccess(Event event) {
        event.setStatus(SagaStatusEnum.SUCCESS);
        event.setSource(PRODUCT_SERVICE);
        addHistory(event, "Products successfully validated");
    }

    private void handleFail(Event event, String message) {
        event.setStatus(SagaStatusEnum.ROLLBACK);
        event.setSource(PRODUCT_SERVICE);
        addHistory(event, "Fail: ".concat(message));
    }

    public void rollback(Event event) {
        try {
            changeValidationToFail(event);
            event.setStatus(SagaStatusEnum.FAILED);
            event.setSource(PRODUCT_SERVICE);
            addHistory(event, "Rollback executed on product validation");
        } catch (Exception e) {
            log.error("Rollback critical error for transaction {}", event.getTransactionId(), e);
        } finally {
            sendCommand(event);
        }
    }

    private void changeValidationToFail(Event event) {
        validationRepository
                .findByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())
                .ifPresentOrElse(
                        validation -> {
                            validation.setSuccess(false);
                            validationRepository.save(validation);
                        },
                        () -> createValidation(event, false)
                );
    }

    private void addHistory(Event event, String message) {
        var history = History.builder()
                .source(PRODUCT_SERVICE)
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addHistory(history);
    }

    private void validateEventData(Event event) {
        if (isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts())) {
            throw new ValidationException("Product orders list is empty.");
        }
        if (isEmpty(event.getOrderId()) || isEmpty(event.getTransactionId())) {
            throw new ValidationException("Order ID or Transaction ID missing.");
        }
    }

    private void sendCommand(Event event) {
        var payload = jsonUtils.toJson(event);
        producer.sendEvent(payload);
    }

}
