package org.leonardonogueira.application.repository;

import org.leonardonogueira.application.domain.OrderInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryOrderRepository extends JpaRepository<OrderInventory, Long> {

    boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);

    List<OrderInventory> findByOrderIdAndTransactionId(String orderId, String transactionId);
}
