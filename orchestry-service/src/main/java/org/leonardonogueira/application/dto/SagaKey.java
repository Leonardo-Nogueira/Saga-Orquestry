package org.leonardonogueira.application.dto;

import org.leonardonogueira.application.enums.EventSourceEnum;
import org.leonardonogueira.application.enums.EventStatusEnum;

public record SagaKey(EventSourceEnum source, EventStatusEnum status) {}
