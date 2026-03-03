package org.leonardonogueira.config.kafka;

public class KafkaTopics {

    ///  Kafka groupID
    public static final String GROUP_ID = "${spring.kafka.consumer.group-id}";

    ///  CONSUMER
    public static final String START_SAGA_TOPIC = "${spring.kafka.topic.start-saga}";
    public static final String ORCHESTRATOR_TOPIC = "${spring.kafka.topic.orchestrator}";
    public static final String FINISH_SUCCESS = "${spring.kafka.topic.finish-success}";
    public static final String FINISH_FAIL = "${spring.kafka.topic.finish-fail}";

}
