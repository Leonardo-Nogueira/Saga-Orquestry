package org.leonardonogueira.application.repository;

import org.leonardonogueira.application.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCode(String code);

}
