package com.example.activitytrackerapi.dto;

import com.example.activitytrackerapi.enums.TaskStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.activitytrackerapi.models.Task}
 */
public record TaskDto(
        LocalDateTime createdAt, LocalDateTime updatedAt, Long id, String title,
        String description, TaskStatus status, LocalDateTime completedAt) {
}