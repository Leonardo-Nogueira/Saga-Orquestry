package org.leonardonogueira.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.leonardonogueira.application.enums.EventSourceEnum;
import org.leonardonogueira.application.enums.EventStatusEnum;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    private EventSourceEnum source;
    private EventStatusEnum status;
    private String message;
    private LocalDateTime createdAt;
}
