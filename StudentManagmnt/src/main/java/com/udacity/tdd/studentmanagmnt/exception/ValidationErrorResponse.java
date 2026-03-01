package com.udacity.tdd.studentmanagmnt.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String error,
        Map<String, String> errors,
        LocalDateTime timestamp,
        String path
    ) {}