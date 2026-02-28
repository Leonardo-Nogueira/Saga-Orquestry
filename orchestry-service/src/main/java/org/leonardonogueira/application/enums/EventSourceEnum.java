package org.leonardonogueira.application.enums;

import org.leonardonogueira.config.exception.ValidationException;

import java.util.Arrays;

public enum EventSourceEnum {
    ORCHESTRATOR,
    PRODUCT_SERVICE,
    PAYMENT_SERVICE,
    INVENTORY_SERVICE;

    public static EventSourceEnum fromString(String value) {
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Invalid source: " + value));
    }
}
