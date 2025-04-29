package com.example.demo.dto;

import com.example.demo.models.Task;

public record TaskDTO(String title, String description, Long userId) {
    public TaskDTO(Task task){
        this(task.getTitle(), task.getDescription(), task.getUserId());
    }
}
