package org.leonardonogueira.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.leonardonogueira.application.enums.TopicEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;


@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {

    private static final Integer PARTITION_COUNT = 1;
    private static final Integer REPLICA_COUNT = 1;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProperties());
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProperties());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic startSagaTopic() {
        return buildTopic(TopicEnum.START_SAGA.getTopic());
    }

    @Bean
    public NewTopic orquestratorTopic() {
        return buildTopic(TopicEnum.ORQUESTRATOR.getTopic());
    }

    @Bean
    public NewTopic finishSucessTopic() {
        return buildTopic(TopicEnum.FINISH_SUCCESS.getTopic());
    }

    @Bean
    public NewTopic finishFailTopic() {
        return buildTopic(TopicEnum.FINISH_FAIL.getTopic());
    }

    @Bean
    public NewTopic productSuccessTopic() {
        return buildTopic(TopicEnum.PRODUCT_SUCCESS.getTopic());
    }

    @Bean
    public NewTopic productFailTopic() {
        return buildTopic(TopicEnum.PRODUCT_FAIL.getTopic());
    }

    @Bean
    public NewTopic paymentSuccessTopic() {
        return buildTopic(TopicEnum.PAYMENT_SUCCESS.getTopic());
    }

    @Bean
    public NewTopic paymentFailTopic() {
        return buildTopic(TopicEnum.PAYMENT_FAIL.getTopic());
    }

    @Bean
    public NewTopic inventorySuccessTopic() {
        return buildTopic(TopicEnum.INVENTORY_SUCCESS.getTopic());
    }

    @Bean
    public NewTopic inventoryFailTopic() {
        return buildTopic(TopicEnum.INVENTORY_FAIL.getTopic());
    }

    @Bean
    public NewTopic notifyEndingTopic() {
        return buildTopic(TopicEnum.NOTIFY_ENDING.getTopic());
    }


    private NewTopic buildTopic(String topic) {
        return TopicBuilder
                .name(topic)
                .replicas(REPLICA_COUNT)
                .partitions(PARTITION_COUNT)
                .build();
    }

    private Map<String, Object> producerProperties() {
        var props = new HashMap<String, Object>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    private Map<String, Object> consumerProperties() {
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
}
