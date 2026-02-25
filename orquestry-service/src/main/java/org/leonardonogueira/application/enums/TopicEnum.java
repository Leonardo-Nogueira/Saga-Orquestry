package org.leonardonogueira.application.enums;

public enum TopicEnum {

    START_SAGA("start-saga"),
    ORQUESTRATOR("orquestrator"),
    FINISH_SUCCESS("finish-success"),
    FINISH_FAIL("finish-fail"),
    PRODUCT_SUCCESS("product-success"),
    PRODUCT_FAIL("product-fail"),
    PAYMENT_SUCCESS("payment-success"),
    PAYMENT_FAIL("payment-fail"),
    INVENTORY_SUCCESS("inventory-success"),
    INVENTORY_FAIL("inventory-fail"),
    NOTIFY_ENDING("notify-ending");

    private final String topic;

    TopicEnum(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return this.topic;
    }

}
