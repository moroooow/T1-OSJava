package com.example.demo.controllers;


import com.example.demo.AbstractContainerBaseTest;
import com.example.demo.dto.TaskDTO;
import com.example.demo.models.Task;
import com.example.demo.repositories.TaskRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    private Long savedTaskId;

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @BeforeEach
    void setupUser(TestInfo testInfo){
        Task task = Task.builder()
                .title("Task 1")
                .userId(1L).
                description("Description 1").
                status("Registered").build();

        Task savedTask = taskRepository.save(task);
        savedTaskId = savedTask.getId();
    }

    @Test
    @DisplayName("Test: (bad request) get task by id ")
    void testTaskNotFound() throws Exception {
        mockMvc.perform(get("/tasks/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Test:get task by id")
    void testRegisterSuccess() throws Exception {
        mockMvc.perform(get("/tasks/" + savedTaskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.status").value("Registered"));
    }

    @Test
    @DisplayName("Test: update task")
    void testRegisterUnsuccessful() throws Exception {
        TaskDTO newTask = TaskDTO.builder()
                        .status("Confirmed")
                        .title("Task 1")
                        .description("Description 1")
                        .userId(1L)
                .build();

        mockMvc.perform(put("/tasks/" + savedTaskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newTask)))
                .andExpect(status().is2xxSuccessful());
    }


    @DisplayName("Test: get array of tasks")
    @Test
    void testGetTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].title", is("Task 1")));
    }
}
