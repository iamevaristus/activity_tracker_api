package com.example.activitytrackerapi.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ActivityTrackerException extends RuntimeException {
    private final HttpStatus status;
    private final Integer statusCode;

    public ActivityTrackerException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.statusCode = HttpStatus.BAD_REQUEST.value();
    }

    public ActivityTrackerException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.statusCode = status.value();
    }
}
