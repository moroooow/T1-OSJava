package com.example.demo.services;

import com.example.demo.dto.KafkaTaskUpdatedDTO;
import com.example.demo.dto.TaskDTO;
import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.kafka.KafkaProducer;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.models.Task;
import com.example.demo.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final KafkaProducer kafkaProducer;
    private final TaskMapper taskMapper;

    @Value("${spring.mail.test.address}")
    private String testAddress;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public void save(TaskDTO taskDTO) {
        taskRepository.save(taskMapper.toEntity(taskDTO));
    }

    public void update(long id, TaskDTO taskDTO) {
        Task task = this.findById(id);
        Task temp = taskMapper.toEntity(taskDTO);
        temp.setId(id);
        taskRepository.save(temp);

        if(!task.getStatus().equals(temp.getStatus())) {
            KafkaTaskUpdatedDTO kafkaDto = new KafkaTaskUpdatedDTO(
                    task.getTitle(),
                    task.getStatus(),
                    temp.getStatus(),
                    testAddress
            );
            kafkaProducer.send(kafkaDto);
        }
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
