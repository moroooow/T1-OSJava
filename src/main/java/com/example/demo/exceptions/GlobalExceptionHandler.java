package com.example.demo.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.spiridonov.http.starter.annotations.HttpExceptionLoggable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@HttpExceptionLoggable
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleTaskNotFound(TaskNotFoundException ex) {
        return new ApiError("TASK_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFound(EntityNotFoundException ex) {
        return new ApiError("ENTITY_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGenericException() {
        return new ApiError("INTERNAL_ERROR", "Unexpected error occurred");
    }
}

