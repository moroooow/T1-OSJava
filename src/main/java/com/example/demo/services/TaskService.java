package com.example.demo.services;

import com.example.demo.dto.TaskDTO;
import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.models.Task;
import com.example.demo.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public void save(TaskDTO taskDTO) {
        Task task = new Task();
        task.setDescription(taskDTO.description());
        task.setTitle(taskDTO.title());
        task.setUserId(taskDTO.userId());
        taskRepository.save(task);
    }

    public void update(long id, TaskDTO taskDTO) {
        Task task = this.findById(id);
        task.setDescription(taskDTO.description());
        task.setTitle(taskDTO.title());
        task.setUserId(taskDTO.userId());
        taskRepository.save(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
