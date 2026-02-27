package org.leonardonogueira.config.kafka;

public class KafkaTopics {

    ///  Kafka GROUP ID
    public static final String GROUP_ID = "${spring.kafka.consumer.group-id}";

    ///  CONSUMER
    public static final String PAYMENT_SUCCESS_TOPIC = "${spring.kafka.topic.payment-success}";
    public static final String PAYMENT_FAIL_TOPIC = "${spring.kafka.topic.payment-fail}";

    /// PRODUCER
    public static final String ORCHESTRATOR = "${spring.kafka.topic.orchestrator}";


}
