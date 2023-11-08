package com.example.activitytrackerapi.services.interfaces;

import com.example.activitytrackerapi.dto.TaskDto;
import com.example.activitytrackerapi.enums.TaskStatus;
import com.example.activitytrackerapi.payload.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface TaskService {
    ResponseEntity<ApiResponse<String>> createTask(String title, String description, TaskStatus status, String userId);
    ResponseEntity<ApiResponse<Map<String, List<TaskDto>>>> viewAllTasks(String userId);
    ResponseEntity<ApiResponse<Map<String, TaskDto>>> viewParticularTask(String userId, Long taskId);
    ResponseEntity<ApiResponse<Map<String, List<TaskDto>>>> viewTaskByStatus(String userId, TaskStatus status);
    ResponseEntity<ApiResponse<String>> editTask(String userId, Long taskId, String title, String description);
    ResponseEntity<ApiResponse<String>> markTaskAsPending(String userId, Long taskId);
    ResponseEntity<ApiResponse<String>> markTaskDone(String userId, Long taskId);
    ResponseEntity<ApiResponse<String>> deleteTask(String userId, Long taskId);
}
