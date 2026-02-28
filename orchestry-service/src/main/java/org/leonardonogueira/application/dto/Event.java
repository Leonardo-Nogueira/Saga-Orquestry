package org.leonardonogueira.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.leonardonogueira.application.enums.EventSourceEnum;
import org.leonardonogueira.application.enums.EventStatusEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

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
    private EventStatusEnum status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;

    public void addHistory(History history) {
        if (isEmpty(eventHistory)) {
            eventHistory = new ArrayList<>();
        }
        eventHistory.add(history);
    }

}
