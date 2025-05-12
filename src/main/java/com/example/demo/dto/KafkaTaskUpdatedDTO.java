package com.example.demo.dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KafkaTaskUpdatedDTO {
    private String title;
    private String statusBefore;
    private String statusAfter;
    private String email;
}