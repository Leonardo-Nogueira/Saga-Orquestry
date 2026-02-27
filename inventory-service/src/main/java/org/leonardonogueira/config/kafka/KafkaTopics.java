package org.leonardonogueira.config.kafka;

public class KafkaTopics {

    ///  Kafka GROUP ID
    public static final String GROUP_ID = "${spring.kafka.consumer.group-id}";

    ///  CONSUMER
    public static final String INVENTORY_SUCCESS_TOPIC = "${spring.kafka.topic.inventory-success}";
    public static final String INVENTORY_FAIL_TOPIC = "${spring.kafka.topic.inventory-fail}";
    public static final String ORCHESTRATOR = "${spring.kafka.topic.orchestrator}";

}
