package com.example.demo.controllers;


import com.example.demo.aspects.annotations.BeforeLog;
import com.example.demo.aspects.annotations.ExceptionHandling;
import com.example.demo.aspects.annotations.ExecutionTime;
import com.example.demo.aspects.annotations.ResultHandling;
import com.example.demo.dto.TaskDTO;
import com.example.demo.models.Task;
import com.example.demo.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public List<Task> getTasks() {
        return taskService.findAll();
    }

    @GetMapping("/{id}")
    @ExecutionTime
    @ExceptionHandling
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return new ResponseEntity<>(taskService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    @ExecutionTime
    @ResultHandling
    @BeforeLog
    public ResponseEntity<String> createTask(@RequestBody TaskDTO taskDTO) {
        Task task = new Task();
        task.setDescription(taskDTO.description());
        task.setTitle(taskDTO.title());
        task.setUserId(taskDTO.userId());
        taskService.save(task);
        return ResponseEntity.ok("Task created");
    }


    @PutMapping("/{id}")
    @ExecutionTime
    @ExceptionHandling
    public ResponseEntity<String> updateTask(@PathVariable long id, @RequestBody TaskDTO taskDTO) {
        Task task = taskService.findById(id);
        task.setDescription(taskDTO.description());
        task.setTitle(taskDTO.title());
        task.setUserId(taskDTO.userId());
        taskService.save(task);
        return ResponseEntity.ok("Task updated");
    }

    @DeleteMapping("/{id}")
    @ExecutionTime
    public ResponseEntity<String> deleteTask(@PathVariable long id) {
        taskService.delete(id);
        return ResponseEntity.ok("Task deleted");
    }

}
