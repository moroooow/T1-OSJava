package com.example.demo.dto;

import com.example.demo.models.Task;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskDTO {

    private String title;
    private String description;
    private String status;
    private Long userId;

    public TaskDTO(Task task){
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.userId = task.getUserId();
    }
}
