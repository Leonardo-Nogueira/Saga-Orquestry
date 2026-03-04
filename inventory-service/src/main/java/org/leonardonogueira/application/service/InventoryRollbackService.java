package org.leonardonogueira.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.application.repository.InventoryOrderRepository;
import org.leonardonogueira.application.repository.InventoryRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class InventoryRollbackService {

    private final InventoryOrderRepository inventoryOrderRepository;
    private final InventoryRepository inventoryRepository;
    private final SagaEventService saga;

    public void rollbackInventory(Event event) {
        try {
            returnInventoryToPreviousValues(event);
            saga.handleRollback(event);
        }catch (Exception e) {
            log.error("Error rolling back inventory for transaction {}: {}", event.getTransactionId(), e.getMessage());
            saga.handleFail(event, e.getMessage());
        }finally {
            saga.sendEvent(event);
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
}
