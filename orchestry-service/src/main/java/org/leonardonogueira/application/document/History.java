package org.leonardonogueira.application.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    private String sourceId;
    private String status;
    private String message;
    private LocalDateTime createdAt;
}
