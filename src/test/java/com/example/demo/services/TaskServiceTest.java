package com.example.demo.services;

import com.example.demo.AbstractContainerBaseTest;
import com.example.demo.dto.KafkaTaskUpdatedDTO;
import com.example.demo.dto.TaskDTO;
import com.example.demo.kafka.KafkaProducer;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.models.Task;
import com.example.demo.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest extends AbstractContainerBaseTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private TaskMapper taskMapper;

    @AfterEach
    public void tearDown() {
    }

    public Task setUpTask() {
        return Task.builder()
                .title("Task 1")
                .userId(1L).
                description("Description 1").
                status("Registered").build();
    }

    @Test
    @DisplayName("Test: find task by title")
    public void findByTitle() {
        Task task = setUpTask();
        TaskService taskService = new TaskService(taskRepository, kafkaProducer, taskMapper);

        when(taskRepository.findByTitle(any())).thenReturn(Optional.of(task));

        Task actTask = taskService.findByTitle("Task 1");

        assertNotNull(actTask);
        assertEquals("Task 1", actTask.getTitle());
        assertEquals("Description 1", actTask.getDescription());
        assertEquals("Registered", actTask.getStatus());
        assertEquals(1L, actTask.getUserId());
    }

    @Test
    @DisplayName("Test: find task by title throws EntityNotFoundException when not found")
    public void findByTitle_ThrowsException() {
        TaskService taskService = new TaskService(taskRepository, kafkaProducer, taskMapper);

        when(taskRepository.findByTitle(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.findByTitle("NonExistingTask"));
    }

    @Test
    void testUpdate_WhenStatusChanged_ShouldSendKafkaMessage() {
        long taskId = 1L;
        
        TaskService taskService = new TaskService(taskRepository, kafkaProducer, taskMapper);

        Task oldTask = setUpTask();

        TaskDTO updatedDto = new TaskDTO();
        updatedDto.setStatus("DONE");

        Task updatedTask = new Task();
        updatedTask.setStatus("DONE");

        when(taskRepository.save(any())).thenReturn(updatedTask);
        when(taskMapper.toEntity(updatedDto)).thenReturn(updatedTask);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(oldTask));
        
        taskService.update(taskId, updatedDto);
        
        verify(taskRepository, times(1)).save(any(Task.class));

        ArgumentCaptor<KafkaTaskUpdatedDTO> captor = ArgumentCaptor.forClass(KafkaTaskUpdatedDTO.class);
        verify(kafkaProducer, times(1)).send(captor.capture());

        KafkaTaskUpdatedDTO sentDto = captor.getValue();
        assertEquals("Task 1", sentDto.getTitle());
        assertEquals("Registered", sentDto.getStatusBefore());
        assertEquals("DONE", sentDto.getStatusAfter());
    }

    @Test
    void testUpdate_WhenStatusNotChanged_ShouldNotSendKafkaMessage() {
        long taskId = 1L;

        TaskService taskService = new TaskService(taskRepository, kafkaProducer, taskMapper);
        Task oldTask = new Task();
        oldTask.setId(taskId);
        oldTask.setTitle("Test Task");
        oldTask.setStatus("WAITING");

        TaskDTO updatedDto = new TaskDTO();
        updatedDto.setStatus("WAITING");

        Task updatedTask = new Task();
        updatedTask.setStatus("WAITING");

        when(taskMapper.toEntity(updatedDto)).thenReturn(updatedTask);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(oldTask));

        taskService.update(taskId, updatedDto);

        verify(taskRepository, times(1)).save(any(Task.class));
        verify(kafkaProducer, never()).send(any());
    }

}
