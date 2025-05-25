package com.example.demo.kafka;


import com.example.demo.dto.KafkaTaskUpdatedDTO;
import com.example.demo.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaTaskConsumer {
    private final EmailService emailService;

    @KafkaListener(
            topics = "${kafka.topics.task_updated}",
            containerFactory = "taskUpdatedKafkaListenerContainerFactory"
    )
    public void listen(List<KafkaTaskUpdatedDTO> dtos, Acknowledgment ack) {
        log.info("я зашел в метод!");
        try{
            for (KafkaTaskUpdatedDTO dto: dtos) {
                log.debug("Start proceeding dtos: {}", dto);
                String emailSubject = "Изменение статуса задачи";
                String emailText = String.format(
                        """
                        Уважаемый пользователь!
                        
                        Статус задачи "%s" был изменён на "%s".
                        
                        Детали задачи:
                        - Старый статус: %s
                        - Новый статус: %s
                        - Дата изменения: %s
                        
                        С уважением,
                        Ваш T1 Task Manager!
                        """,
                        dto.getTitle(),
                        dto.getStatusAfter(),
                        dto.getStatusBefore(),
                        dto.getStatusAfter(),
                        LocalDateTime.now()
                );
                emailService.sendSimpleEmail(dto.getEmail(), emailSubject, emailText);
            }
        } finally {
            ack.acknowledge();
        }
    }

}
