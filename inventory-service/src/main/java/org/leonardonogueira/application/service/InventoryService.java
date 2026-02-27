package org.leonardonogueira.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.domain.Inventory;
import org.leonardonogueira.application.domain.OrderInventory;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.producer.KafkaProducer;
import org.leonardonogueira.application.repository.InventoryOrderRepository;
import org.leonardonogueira.application.repository.InventoryRepository;
import org.leonardonogueira.application.utils.JsonUtils;
import org.leonardonogueira.config.exception.ValidationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class InventoryService {

    private final InventoryOrderRepository inventoryOrderRepository;
    private final InventoryRepository inventoryRepository;
    private final SagaEventService saga;
    private final JsonUtils jsonUtils;
    private final KafkaProducer producer;

    public void updateInventory(Event event) {
        try {
            checkDuplicateInventoryTransaction(event);
            processInventoryUpdate(event);
            saga.handleSuccess(event);
        } catch (Exception e) {
            log.error("Error updating inventory for transaction {}: {}", event.getTransactionId(), e.getMessage());
            saga.handleFail(event, e.getMessage());
        } finally {
            sendEventToKafka(event);
        }
    }

    public void rollbackInventory(Event event) {
        try {
            returnInventoryToPreviousValues(event);
            saga.handleRollback(event);
        }catch (Exception e) {
            log.error("Error rolling back inventory for transaction {}: {}", event.getTransactionId(), e.getMessage());
            saga.handleFail(event, e.getMessage());
        }finally {
            sendEventToKafka(event);
        }
    }

    private void returnInventoryToPreviousValues(Event event) {
         inventoryOrderRepository
         .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
         .forEach(orderInventory -> {

             var inventory = orderInventory.getInventory();
             var product = inventory.getProductCode();
             inventory.setAvailable(orderInventory.getOldQuantity());
             inventoryRepository.save(inventory);

             log.info("Restored inventory for product {} from {} to {}",
                             product, orderInventory.getNewQuantity(), orderInventory.getOldQuantity());

         });
    }

    private void processInventoryUpdate(Event event) {
        event.getPayload().getProducts().forEach(product -> {
            var inventory = findInventoryByCode(product.getProduct().getCode());

            checkInventory(inventory.getAvailable(), product.getQuantity());

            var orderInventory = createOrderInventory(event, product.getQuantity(), inventory);
            inventoryOrderRepository.save(orderInventory);

            int newBalance = inventory.getAvailable() - product.getQuantity();
            inventory.setAvailable(newBalance);
            inventoryRepository.save(inventory);

            log.info("Updated product {} balance. Old: {} | New: {}",
                    product.getProduct().getCode(), orderInventory.getOldQuantity(), newBalance);
        });
        log.info("Inventory updated successfully | Order: {} | Transaction: {}",
                event.getPayload().getId(), event.getTransactionId());
    }

    private void checkInventory(int available, int orderQuantity) {
        log.info("Checking if has amount of {} available in inventory", available);
        if (orderQuantity > available) {
            throw new ValidationException("Stock insufficient. Available: " + available + ", Requested: " + orderQuantity);
        }
    }

    private OrderInventory createOrderInventory(Event event, int orderQuantity, Inventory inventory) {
        return OrderInventory.builder()
                .inventory(inventory)
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .orderQuantity(orderQuantity)
                .oldQuantity(inventory.getAvailable())
                .newQuantity(inventory.getAvailable() - orderQuantity)
                .build();
    }

    private Inventory findInventoryByCode(String code) {
        return inventoryRepository.findByProductCode(code)
                .orElseThrow(() -> new ValidationException("Inventory not found for product code: " + code));
    }

    private void checkDuplicateInventoryTransaction(Event event) {
        if (inventoryOrderRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())) {
            throw new ValidationException("Event already processed for this orderId: {} and transactionId: {}.");
        }
    }

    private void sendEventToKafka(Event event) {
        producer.sendEvent(jsonUtils.toJson(event));
    }
}
