package org.leonardonogueira.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.leonardonogueira.application.enums.EventSourceEnum;
import org.leonardonogueira.application.enums.SagaStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private String id;
    private String transactionId;
    private String orderId;
    private Order payload;
    private EventSourceEnum source;
    private SagaStatusEnum status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;

}
