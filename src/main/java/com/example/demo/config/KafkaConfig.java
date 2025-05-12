package com.example.demo.config;

import com.example.demo.dto.KafkaTaskUpdatedDTO;
import com.example.demo.kafka.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${kafka.servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group_id}")
    private String groupId;

    @Value("${kafka.consumer.max_poll_records:5}")
    private Integer maxPollRecords;

    @Value("${kafka.topics.task_updated}")
    private String taskUpdatedTopic;

    @Bean
    public ConsumerFactory<String, KafkaTaskUpdatedDTO> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.demo.dto.KafkaTaskUpdatedDTO");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, KafkaTaskUpdatedDTO> taskUpdatedKafkaListenerContainerFactory(ConsumerFactory<String, KafkaTaskUpdatedDTO> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, KafkaTaskUpdatedDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factoryBuilder(factory, consumerFactory);
        return factory;
    }

    private <T> void factoryBuilder(ConcurrentKafkaListenerContainerFactory<String, T> factory, ConsumerFactory<String, T> consumerFactory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler());
    }

    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(1000,3));
        errorHandler.addNotRetryableExceptions(IllegalStateException.class);
        errorHandler.setRetryListeners((rec, ex, deliveryAttempt) ->
                log.error("RetryListeners message= {}, offset = {}, deliveryAttempt = {}",ex.getMessage(), rec.offset(), deliveryAttempt));

        return errorHandler;
    }

    @Bean
    public KafkaProducer producerTask(@Qualifier("task") KafkaTemplate<String,KafkaTaskUpdatedDTO> template){
        template.setDefaultTopic(taskUpdatedTopic);
        return new KafkaProducer(template);
    }

    @Bean
    public ProducerFactory<String, KafkaTaskUpdatedDTO> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        configProps.put(
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                false
        );
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean("task")
    public KafkaTemplate<String, KafkaTaskUpdatedDTO> kafkaTemplate(ProducerFactory<String,KafkaTaskUpdatedDTO> producerTaskFactory){
        return new KafkaTemplate<>(producerTaskFactory);
    }
}
