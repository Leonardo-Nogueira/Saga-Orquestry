package org.leonardonogueira.application.repository;

import org.leonardonogueira.application.domain.Validation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ValidationRepository extends JpaRepository<Validation, Long> {

    boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);

    Optional<Validation> findByOrderIdAndTransactionId(String orderId, String transactionId);

}
