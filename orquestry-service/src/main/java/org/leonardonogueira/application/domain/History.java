package org.leonardonogueira.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.leonardonogueira.application.enums.EventSourceEnum;
import org.leonardonogueira.application.enums.SagaStatusEnum;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    private EventSourceEnum source;
    private SagaStatusEnum status;
    private String message;
    private LocalDateTime createdAt;
}
