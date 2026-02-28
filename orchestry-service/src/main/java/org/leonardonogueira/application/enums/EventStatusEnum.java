package org.leonardonogueira.application.enums;

import org.leonardonogueira.config.exception.ValidationException;

import java.util.Arrays;

public enum EventStatusEnum {

    SUCCESS,
    ROLLBACK,
    FAILED;

    public static EventStatusEnum fromString(String value) {
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Invalid status: " + value));
    }

}
