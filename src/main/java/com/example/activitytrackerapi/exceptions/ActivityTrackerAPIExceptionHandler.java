package com.example.activitytrackerapi.exceptions;

import com.example.activitytrackerapi.payload.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ActivityTrackerAPIExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(AuthException.class)
    public ApiResponse<Map<String, String>> handleAuthException(AuthException authException) {
        Map<String, String> map = new HashMap<>();
        map.put("error_state", authException.getClass().getName());
        map.put("error_code", String.valueOf(authException.getStatus().value()));
        return new ApiResponse<>(
                authException.getStatus(),
                authException.getMessage(),
                map
        );
    }

    @ExceptionHandler(TaskException.class)
    public ApiResponse<Map<String, String>> handleTaskException(TaskException taskException) {
        Map<String, String> map = new HashMap<>();
        map.put("error_state", taskException.getClass().getName());
        map.put("error_description", taskException.getLocalizedMessage());
        map.put("error_code", String.valueOf(taskException.getStatus().value()));
        return new ApiResponse<>(
                taskException.getStatus(),
                taskException.getMessage(),
                map
        );
    }

    @ExceptionHandler(ActivityTrackerException.class)
    public ApiResponse<Map<String, String>> handleTaskException(ActivityTrackerException activityTrackerException) {
        Map<String, String> map = new HashMap<>();
        map.put("error_state", activityTrackerException.getClass().getName());
        map.put("error_code", String.valueOf(activityTrackerException.getStatus().value()));
        map.put("error_details", activityTrackerException.getClass().descriptorString());
        return new ApiResponse<>(
                activityTrackerException.getStatus(),
                activityTrackerException.getMessage(),
                map
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        Map<String, String> map = new HashMap<>();
        map.put("error_code", status.toString());
        map.put("request_description", request.getDescription(true));
        map.put("error_details", ex.getBody().toString());
        map.put("header", headers.toString());
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                map
        );
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request
    ) {
        Map<String, String> map = new HashMap<>();
        map.put("error_details", exception.getBody().toString());
        map.put("error_code", status.toString());
        map.put("request_description", request.getDescription(true));
        map.put("header", headers.toString());
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                map
        );
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }
}
