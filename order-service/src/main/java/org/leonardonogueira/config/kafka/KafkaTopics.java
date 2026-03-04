package org.leonardonogueira.config.kafka;

public class KafkaTopics {

    /// GROUP ID
    public static final String GROUP_ID = "${spring.kafka.consumer.group-id}";
    /// CONSUMER
    public static final String NOTIFY_ENDING_TOPIC = "${spring.kafka.topic.notify-ending}";
    /// PRODUCER
    public static final String START_SAGA_TOPIC = "${spring.kafka.topic.start-saga}";
}
