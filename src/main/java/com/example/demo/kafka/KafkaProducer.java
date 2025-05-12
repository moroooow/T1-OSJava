package com.example.demo.kafka;

import com.example.demo.aspects.annotations.BeforeLog;
import com.example.demo.aspects.annotations.ExceptionHandling;
import com.example.demo.dto.KafkaTaskUpdatedDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;


@RequiredArgsConstructor
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, KafkaTaskUpdatedDTO> kafkaTemplate;

    @ExceptionHandling
    @BeforeLog
    public void send(KafkaTaskUpdatedDTO taskDTO) {
        kafkaTemplate.sendDefault(UUID.randomUUID().toString(), taskDTO);
        kafkaTemplate.flush();
    }

    @ExceptionHandling
    public void sendToTopic(String topic, KafkaTaskUpdatedDTO taskDTO) {
        kafkaTemplate.send(topic, taskDTO);
        kafkaTemplate.flush();
    }
}
