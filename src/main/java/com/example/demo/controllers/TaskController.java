package com.example.demo.controllers;


import com.example.demo.aspects.annotations.BeforeLog;
import com.example.demo.aspects.annotations.ExceptionHandling;
import com.example.demo.aspects.annotations.ExecutionTime;
import com.example.demo.aspects.annotations.ResultHandling;
import com.example.demo.dto.TaskDTO;
import com.example.demo.services.TaskService;
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
    @ExecutionTime
    public List<TaskDTO> getTasks() {
        return taskService.findAll().stream()
                .map(TaskDTO::new).toList();
    }

    @GetMapping("/{id}")
    @ExecutionTime
    @ExceptionHandling
    public TaskDTO getTask(@PathVariable Long id) {
        return new TaskDTO(taskService.findById(id));
    }

    @PostMapping
    @ExecutionTime
    @ResultHandling
    @BeforeLog
    public void createTask(@RequestBody TaskDTO taskDTO) {
        taskService.save(taskDTO);
    }

    @PutMapping("/{id}")
    @ExecutionTime
    @ExceptionHandling
    public void updateTask(@PathVariable long id, @RequestBody TaskDTO taskDTO) {
        taskService.update(id, taskDTO);
    }

    @DeleteMapping("/{id}")
    @ExecutionTime
    public void deleteTask(@PathVariable long id) {
        taskService.delete(id);
    }

}
