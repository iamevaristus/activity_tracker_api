package com.example.activitytrackerapi.controllers;

import com.example.activitytrackerapi.dto.TaskDto;
import com.example.activitytrackerapi.enums.TaskStatus;
import com.example.activitytrackerapi.exceptions.ActivityTrackerException;
import com.example.activitytrackerapi.exceptions.TaskException;
import com.example.activitytrackerapi.payload.ApiResponse;
import com.example.activitytrackerapi.services.implementations.TaskImplementation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(name = "Task API EndPoint", value = "/task")
public class TaskController {
    private final TaskImplementation taskImplementation;

    public TaskStatus convertToStatus(String status) {
        if (status.toLowerCase().contains("pending")) {
            return TaskStatus.PENDING;
        } else if (status.toLowerCase().contains("done")) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }
    public record TaskRecord(String title, String description, String status) { }

    @GetMapping(name = "View Tasks By Status Or View All Status")
    public ResponseEntity<ApiResponse<Map<String, List<TaskDto>>>> viewTasksByStatus(
            @RequestParam(required = false, name = "status") String status, HttpSession session
    ) {
        if(session.getAttribute("current_user") != null) {
            if(status == null) {
                return taskImplementation.viewAllTasks(String.valueOf(session.getAttribute("current_user")));
            } else {
                return taskImplementation.viewTaskByStatus(
                        String.valueOf(session.getAttribute("current_user")), convertToStatus(status)
                );
            }
        }
        throw new ActivityTrackerException("No current user");
    }

    @GetMapping(name = "View Particular Task", value = "{id}")
    public ResponseEntity<ApiResponse<Map<String, TaskDto>>> viewParticularTasks(
            @PathVariable Long id, HttpSession session
    ) {
        if(session.getAttribute("current_user") != null) {
            if(id == null) {
                throw new TaskException("Task Id cannot be null when searching for a task. Please pass one");
            } else {
                return taskImplementation.viewParticularTask(
                        String.valueOf(session.getAttribute("current_user")), id
                );
            }
        }
        throw new ActivityTrackerException("No current user");
    }

    @PostMapping(name = "Create Task", value = "/create")
    public ResponseEntity<ApiResponse<String>> createTask(@RequestBody TaskRecord task, HttpSession session) {
        if(session.getAttribute("current_user") != null) {
            if(task.title == null || task.description == null) {
                throw new TaskException("Incomplete task details.");
            } else {
                String status = task.status == null ? "Pending" : task.status;
                return taskImplementation.createTask(
                        task.title, task.description, convertToStatus(status),
                        String.valueOf(session.getAttribute("current_user"))
                );
            }
        }
        throw new ActivityTrackerException("No current user");
    }

    @PatchMapping(name = "Edit Task", value = "/edit/{id}")
    public ResponseEntity<ApiResponse<String>> editTask(
            @RequestBody TaskRecord task, @PathVariable Long id, HttpSession session
    ) {
        if(session.getAttribute("current_user") != null) {
            if(task.description == null && task.title == null) {
                throw new TaskException("Task details are needed for edit.");
            }
            if(id == null) {
                throw new TaskException("Task Id is needed for edit");
            }
            return taskImplementation.editTask(
                    String.valueOf(session.getAttribute("current_user")),
                    id, task.title, task.description
            );
        }
        throw new ActivityTrackerException("No current user");
    }

    @PatchMapping(name = "Mark Task Pending", value = "/mark_pending/{id}")
    public ResponseEntity<ApiResponse<String>> pendTask(@PathVariable Long id, HttpSession session) {
        if(session.getAttribute("current_user") != null) {
            if(id == null) {
                throw new TaskException("Task Id is null");
            } else {
                return taskImplementation.markTaskAsPending(
                        String.valueOf(session.getAttribute("current_user")), id
                );
            }
        }
        throw new ActivityTrackerException("No current user");
    }

    @PatchMapping(name = "Mark Task Done", value = "/mark_done/{id}")
    public ResponseEntity<ApiResponse<String>> doneTask(@PathVariable Long id, HttpSession session) {
        if(session.getAttribute("current_user") != null) {
            if(id == null) {
                throw new TaskException("Task Id is null");
            } else {
                return taskImplementation.markTaskDone(
                        String.valueOf(session.getAttribute("current_user")), id
                );
            }
        }
        throw new ActivityTrackerException("No current user");
    }

    @DeleteMapping(name = "Delete Task", value = "/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTask(@PathVariable Long id, HttpSession session) {
        if(session.getAttribute("current_user") != null) {
            if(id == null) {
                throw new TaskException("Task Id is null");
            } else {
                return taskImplementation.deleteTask(
                        String.valueOf(session.getAttribute("current_user")), id
                );
            }
        }
        throw new ActivityTrackerException("No current user");
    }
}
