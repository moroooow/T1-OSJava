package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(nullable = false)
    private String status;

    @Override
    public String toString() {
        return "Task{id=" + id + ", description=" + description + ", " +
                "title=" + title + ", userId=" + userId + "}";
    }
}
