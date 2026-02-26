package org.leonardonogueira.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.leonardonogueira.application.document.ProductOrder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    private List<ProductOrder> products;
}
