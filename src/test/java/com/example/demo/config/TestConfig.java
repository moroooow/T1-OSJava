package com.example.demo.config;

import com.example.demo.services.EmailService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public EmailService emailService() {
        return Mockito.mock(EmailService.class);
    }
}
