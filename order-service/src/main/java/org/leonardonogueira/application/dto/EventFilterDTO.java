package org.leonardonogueira.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDTO {

    private String orderId;
    private String transactionId;

}
