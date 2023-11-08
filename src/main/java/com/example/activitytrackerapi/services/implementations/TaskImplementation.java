package com.example.activitytrackerapi.services.implementations;

import com.example.activitytrackerapi.config.TaskMapper;
import com.example.activitytrackerapi.dto.TaskDto;
import com.example.activitytrackerapi.enums.TaskStatus;
import com.example.activitytrackerapi.exceptions.ActivityTrackerException;
import com.example.activitytrackerapi.exceptions.TaskException;
import com.example.activitytrackerapi.models.Task;
import com.example.activitytrackerapi.payload.ApiResponse;
import com.example.activitytrackerapi.repositories.TaskRepository;
import com.example.activitytrackerapi.repositories.UserRepository;
import com.example.activitytrackerapi.services.interfaces.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class TaskImplementation implements TaskService {
    private final TaskRepository repository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponse<String>> createTask(
            String title, String description, TaskStatus status, String userId
    ) {
        if(title.isEmpty() || description.isEmpty()) {
            throw new TaskException(
                    title.isEmpty() ? "Task Title cannot be empty." : "Task Description cannot be empty."
            );
        } else {
            AtomicReference<ApiResponse<String>> apiResponse = new AtomicReference<>();
            try {
                userRepository.findById(UUID.fromString(userId)).ifPresentOrElse(user -> {
                    Task task = new Task();
                    task.setCreatedAt(LocalDateTime.now());
                    task.setTitle(title);
                    task.setDescription(description);
                    task.setUser(user);
                    if(status != null) {
                        task.setStatus(status);
                    }
                    repository.save(task);
                    apiResponse.set(new ApiResponse<>(
                            HttpStatus.OK,
                            "Task was successfully created. Go to https://localhost:3000/api/v1/tasks to view all tasks."
                    ));
                }, () -> {
                    throw new ActivityTrackerException("There is no existing user with such userId. Check the userId");
                });
            } catch (IllegalArgumentException e) {
                throw new ActivityTrackerException("UserId is not properly formatted.");
            }
            return new ResponseEntity<>(apiResponse.get(), apiResponse.get().getHttpStatus());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String, List<TaskDto>>>> viewAllTasks(String userId) {
        AtomicReference<ApiResponse<Map<String, List<TaskDto>>>> apiResponse = new AtomicReference<>();
        try {
            userRepository.findById(UUID.fromString(userId)).ifPresentOrElse(user -> {
                Map<String, List<TaskDto>> taskMap = new HashMap<>();
                List<TaskDto> taskDos = user.getTasks().stream().map(TaskMapper.INSTANCE::taskToTaskDto).toList();
                taskMap.put("tasks", taskDos);
                apiResponse.set(new ApiResponse<>(
                        HttpStatus.OK,
                        taskDos.isEmpty() ? "No task made." : "Tasks were successfully fetched.",
                        taskMap
                ));
            }, () -> {
                throw new ActivityTrackerException("There is no existing user with such userId. Check the userId");
            });
        } catch (IllegalArgumentException e) {
            throw new ActivityTrackerException("UserId is not properly formatted.");
        }
        return new ResponseEntity<>(apiResponse.get(), apiResponse.get().getHttpStatus());
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String, TaskDto>>> viewParticularTask(String userId, Long taskId) {
        AtomicReference<ApiResponse<Map<String, TaskDto>>> apiResponse = new AtomicReference<>();
        try {
            userRepository.findById(UUID.fromString(userId)).ifPresentOrElse(user -> repository.findById(taskId).ifPresentOrElse(task -> {
                if(task.getUser().getId().equals(user.getId())) {
                    Map<String, TaskDto> taskMap = new HashMap<>();
                    taskMap.put("tasks", TaskMapper.INSTANCE.taskToTaskDto(task));
                    apiResponse.set(new ApiResponse<>(
                            HttpStatus.OK,
                            "Fetch success.",
                            taskMap
                    ));
                } else {
                    throw new TaskException("Cannot view another user's task.");
                }
            }, () -> {
                throw new TaskException("There is no such task with the given taskId.");
            }), () -> {
                throw new ActivityTrackerException("There is no existing user with such userId. Check the userId");
            });
        } catch (IllegalArgumentException e) {
            throw new ActivityTrackerException("UserId is not properly formatted.");
        }
        return new ResponseEntity<>(apiResponse.get(), apiResponse.get().getHttpStatus());
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String, List<TaskDto>>>> viewTaskByStatus(String userId, TaskStatus status) {
        AtomicReference<ApiResponse<Map<String, List<TaskDto>>>> apiResponse = new AtomicReference<>();
        try {
            userRepository.findById(UUID.fromString(userId)).ifPresentOrElse(user -> {
                List<Task> tasks = repository.findByStatus(status)
                        .stream()
                        .filter(task -> task.getUser().getId().equals(user.getId()))
                        .filter(task -> task.getStatus().equals(status))
                        .toList();
                Map<String, List<TaskDto>> taskMap = new HashMap<>();
                List<TaskDto> taskDos = tasks.stream().map(TaskMapper.INSTANCE::taskToTaskDto).toList();
                taskMap.put("tasks", taskDos);
                apiResponse.set(new ApiResponse<>(
                        HttpStatus.OK,
                        taskDos.isEmpty() ? "No task found with such status for current user" : "Task fetch success",
                        taskMap
                ));
            }, () -> {
                throw new ActivityTrackerException("There is no existing user with such userId. Check the userId");
            });
        } catch (IllegalArgumentException e) {
            throw new ActivityTrackerException("UserId is not properly formatted.");
        }
        return new ResponseEntity<>(apiResponse.get(), apiResponse.get().getHttpStatus());
    }

    @Override
    public ResponseEntity<ApiResponse<String>> editTask(String userId, Long taskId, String title, String description) {
        AtomicReference<ApiResponse<String>> apiResponse = new AtomicReference<>();
        try {
            userRepository.findById(UUID.fromString(userId)).ifPresentOrElse(user -> repository.findById(taskId).ifPresentOrElse(task -> {
                if(task.getUser().getId().equals(user.getId())) {
                    if(title == null && description == null) {
                        throw new TaskException("Cannot update task without any data");
                    } else {
                        if(title != null && !title.isEmpty() && !task.getTitle().equals(title)) {
                            task.setTitle(title);
                        }
                        if(description != null && !description.isEmpty() && !task.getDescription().equals(description)) {
                            task.setDescription(description);
                        }
                        task.setUpdatedAt(LocalDateTime.now());
                        repository.save(task);
                        apiResponse.set(new ApiResponse<>(
                                HttpStatus.OK,
                                "Task was updated successfully"
                        ));
                    }
                } else {
                    throw new TaskException("Cannot edit another user's task.");
                }
            }, () -> {
                throw new TaskException("There is no such task with the given taskId.");
            }), () -> {
                throw new ActivityTrackerException("There is no existing user with such userId. Check the userId");
            });
        } catch (IllegalArgumentException e) {
            throw new ActivityTrackerException("UserId is not properly formatted.");
        }
        return new ResponseEntity<>(apiResponse.get(), apiResponse.get().getHttpStatus());
    }

    @Override
    public ResponseEntity<ApiResponse<String>> markTaskAsPending(String userId, Long taskId) {
        AtomicReference<ApiResponse<String>> apiResponse = new AtomicReference<>();
        try {
            userRepository.findById(UUID.fromString(userId)).ifPresentOrElse(user -> repository.findById(taskId).ifPresentOrElse(task -> {
                if(task.getUser().getId().equals(user.getId())) {
                    if(task.getStatus() != TaskStatus.PENDING) {
                        task.setStatus(TaskStatus.PENDING);
                    }
                    task.setUpdatedAt(LocalDateTime.now());
                    repository.save(task);
                    apiResponse.set(new ApiResponse<>(
                            HttpStatus.OK,
                            "Task was successfully marked pending."
                    ));
                } else {
                    throw new TaskException("Cannot change another user's task status.");
                }
            }, () -> {
                throw new TaskException("There is no such task with the given taskId.");
            }), () -> {
                throw new ActivityTrackerException("There is no existing user with such userId. Check the userId");
            });
        } catch (IllegalArgumentException e) {
            throw new ActivityTrackerException("UserId is not properly formatted.");
        }
        return new ResponseEntity<>(apiResponse.get(), apiResponse.get().getHttpStatus());
    }

    @Override
    public ResponseEntity<ApiResponse<String>> markTaskDone(String userId, Long taskId) {
        AtomicReference<ApiResponse<String>> apiResponse = new AtomicReference<>();
        try {
            userRepository.findById(UUID.fromString(userId)).ifPresentOrElse(user -> repository.findById(taskId).ifPresentOrElse(task -> {
                if(task.getUser().getId().equals(user.getId())) {
                    if(task.getStatus() != TaskStatus.DONE) {
                        task.setStatus(TaskStatus.DONE);
                    }
                    task.setUpdatedAt(LocalDateTime.now());
                    repository.save(task);
                    apiResponse.set(new ApiResponse<>(
                            HttpStatus.OK,
                            "Task was successfully marked done."
                    ));
                } else {
                    throw new TaskException("Cannot change another user's task status.");
                }
            }, () -> {
                throw new TaskException("There is no such task with the given taskId.");
            }), () -> {
                throw new ActivityTrackerException("There is no existing user with such userId. Check the userId");
            });
        } catch (IllegalArgumentException e) {
            throw new ActivityTrackerException("UserId is not properly formatted.");
        }
        return new ResponseEntity<>(apiResponse.get(), apiResponse.get().getHttpStatus());
    }

    @Override
    public ResponseEntity<ApiResponse<String>> deleteTask(String userId, Long taskId) {
        AtomicReference<ApiResponse<String>> apiResponse = new AtomicReference<>();
        try {
            userRepository.findById(UUID.fromString(userId)).ifPresentOrElse(user -> repository.findById(taskId).ifPresentOrElse(task -> {
                if(task.getUser().getId().equals(user.getId())) {
                    repository.deleteById(taskId);
                    apiResponse.set(new ApiResponse<>(
                            HttpStatus.OK,
                            "Task was successfully deleted."
                    ));
                } else {
                    throw new TaskException("Cannot delete another user's task.");
                }
            }, () -> {
                throw new TaskException("There is no such task with the given taskId.");
            }), () -> {
                throw new ActivityTrackerException("There is no existing user with such userId. Check the userId");
            });
        } catch (IllegalArgumentException e) {
            throw new ActivityTrackerException("UserId is not properly formatted.");
        }
        return new ResponseEntity<>(apiResponse.get(), apiResponse.get().getHttpStatus());
    }
}
