package org.leonardonogueira.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.leonardonogueira.application.domain.Product;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrder {

    private Product product;
    private int quantity;

}
