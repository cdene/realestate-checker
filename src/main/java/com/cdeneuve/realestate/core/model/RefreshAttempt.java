package com.cdeneuve.realestate.core.model;

import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@ToString
public class RefreshAttempt {
    private final HttpStatus status;
    private final LocalDateTime timestamp;

    public RefreshAttempt(HttpStatus status) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
