package org.leonardonogueira.application.repository;

import org.leonardonogueira.application.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);

    Optional<Payment> findByOrderIdAndTransactionId(String orderId, String transactionId);

}
