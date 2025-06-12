package com.example.demo.Kafka;

import com.example.demo.dto.KafkaTaskUpdatedDTO;
import com.example.demo.kafka.KafkaTaskConsumer;
import com.example.demo.services.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class KafkaTaskConsumerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private KafkaTaskConsumer kafkaTaskConsumer;

    @Test
    void testListen_SendsEmailsAndAcknowledges() {
        KafkaTaskUpdatedDTO dto = KafkaTaskUpdatedDTO.builder()
                .email("test@example.com")
                .title("Test Task")
                .statusBefore("В ожидании")
                .statusAfter("Выполнена")
                .build();

        List<KafkaTaskUpdatedDTO> dtoList = List.of(dto);

        kafkaTaskConsumer.listen(dtoList, acknowledgment);

        verify(emailService, times(1)).sendSimpleEmail(
                eq("test@example.com"),
                eq("Изменение статуса задачи"),
                anyString()
        );
        verify(acknowledgment, times(1)).acknowledge();
    }
}