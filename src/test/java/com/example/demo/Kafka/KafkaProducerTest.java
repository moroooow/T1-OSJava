package com.example.demo.Kafka;

import com.example.demo.AbstractContainerBaseTest;
import com.example.demo.config.TestConfig;
import com.example.demo.dto.KafkaTaskUpdatedDTO;
import com.example.demo.kafka.KafkaProducer;
import com.example.demo.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.*;
        import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import(TestConfig.class)
public class KafkaProducerTest extends AbstractContainerBaseTest {
    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private EmailService emailService;

    @Test
    public void testProducerSendsMessageAndConsumerProcessesIt() throws InterruptedException {
        KafkaTaskUpdatedDTO dto = new KafkaTaskUpdatedDTO();
        dto.setEmail("test@example.com");
        dto.setTitle("Test Task");
        dto.setStatusBefore("OPEN");
        dto.setStatusAfter("DONE");

        kafkaProducer.send(dto);

        Thread.sleep(5000);

        verify(emailService, times(1)).sendSimpleEmail(
                eq("test@example.com"),
                anyString(),
                anyString()
        );
    }
}
