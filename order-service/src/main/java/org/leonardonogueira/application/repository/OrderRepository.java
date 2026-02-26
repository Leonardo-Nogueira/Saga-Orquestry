package org.leonardonogueira.application.repository;

import org.leonardonogueira.application.document.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
