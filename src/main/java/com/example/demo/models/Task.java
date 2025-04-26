package com.example.demo.models;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "task")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long userId;

    @Override
    public String toString() {
        return "Task{id=" + id + ", description=" + description + ", " +
                "title=" + title + ", userId=" + userId + "}";
    }
}
