package org.leonardonogueira.config.kafka;

public class KafkaTopics {

    ///  Kafka groupID
    public static final String GROUP_ID = "${spring.kafka.consumer.group-id}";

    ///  CONSUMER
    public static final String PRODUCT_SUCCESS_TOPIC = "${spring.kafka.topic.product-success}";
    public static final String PRODUCT_FAIL_TOPIC = "${spring.kafka.topic.product-fail}";

    /// PRODUCER
    public static final String ORCHESTRATOR = "${spring.kafka.topic.orchestrator}";


}
