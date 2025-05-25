package com.example.demo.mapper;

import com.example.demo.dto.TaskDTO;
import com.example.demo.models.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public Task toEntity(TaskDTO taskDTO) {
         return Task.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .status(taskDTO.getStatus())
                .userId(taskDTO.getUserId())
                .build();
    }

    public TaskDTO toDto(Task task) {
        return TaskDTO.builder()
                .title(task.getTitle())
                .status(task.getStatus())
                .description(task.getDescription())
                .userId(task.getUserId())
                .build();
    }
}
