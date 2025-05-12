package com.example.demo.dto;

import com.example.demo.models.Task;
import lombok.Builder;

@Builder
public record TaskDTO(String title, String description, String status, Long userId) {
    public TaskDTO(Task task){
        this(task.getTitle(), task.getDescription(),task.getStatus(),task.getUserId());
    }
}
