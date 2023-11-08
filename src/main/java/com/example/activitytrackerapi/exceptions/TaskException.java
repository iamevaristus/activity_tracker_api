package com.example.activitytrackerapi.exceptions;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TaskException extends RuntimeException {
    private final HttpStatus status;
    private final Integer statusCode;

    public TaskException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.statusCode = HttpStatus.BAD_REQUEST.value();
    }
}
