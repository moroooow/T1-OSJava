package com.example.demo.controllers;


import com.example.demo.dto.TaskDTO;
import com.example.demo.services.TaskService;
import org.spiridonov.http.starter.annotations.HttpLoggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @HttpLoggable
    public List<TaskDTO> getTasks() {
        return taskService.findAll().stream()
                .map(TaskDTO::new).toList();
    }

    @GetMapping("/{id}")
    @HttpLoggable
    public TaskDTO getTask(@PathVariable Long id) {
        return new TaskDTO(taskService.findById(id));
    }

    @PostMapping
    @HttpLoggable
    public void createTask(@RequestBody TaskDTO taskDTO) {
        taskService.save(taskDTO);
    }

    @PutMapping("/{id}")
    @HttpLoggable
    public void updateTask(@PathVariable long id, @RequestBody TaskDTO taskDTO) {
        taskService.update(id, taskDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable long id) {
        taskService.delete(id);
    }

}
