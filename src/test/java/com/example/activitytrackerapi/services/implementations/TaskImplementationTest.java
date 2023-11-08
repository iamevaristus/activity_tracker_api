package com.example.activitytrackerapi.services.implementations;
import com.example.activitytrackerapi.dto.TaskDto;
import com.example.activitytrackerapi.enums.TaskStatus;
import com.example.activitytrackerapi.exceptions.ActivityTrackerException;
import com.example.activitytrackerapi.exceptions.TaskException;
import com.example.activitytrackerapi.models.Task;
import com.example.activitytrackerapi.models.User;
import com.example.activitytrackerapi.payload.ApiResponse;
import com.example.activitytrackerapi.repositories.TaskRepository;
import com.example.activitytrackerapi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskImplementationTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private TaskImplementation taskImplementation;
    private User user;
    private User user2;
    private Task task;
    private Task task2;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setPassword("password");

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setFirstName("John");
        user2.setLastName("Doe");
        user2.setUsername("johndoe");
        user2.setPassword("password");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(TaskStatus.PENDING);
        task.setUser(user);
        task.setCreatedAt(LocalDateTime.now());

        task2 = new Task();
        task2.setId(1L);
        task2.setTitle("Test Task");
        task2.setDescription("Test Description");
        task2.setStatus(TaskStatus.DONE);
        task2.setUser(user2);
        task2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testCreateTaskWithValidInputs() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        ResponseEntity<ApiResponse<String>> response = taskImplementation.createTask(task.getTitle(), task.getDescription(), task.getStatus(), user.getId().toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task was successfully created. Go to https://localhost:3000/api/v1/tasks to view all tasks.", response.getBody().getMessage());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testViewAllTasksWithValidUserId() {
        user.getTasks().add(task);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse<Map<String, List<TaskDto>>>> response = taskImplementation.viewAllTasks(user.getId().toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tasks were successfully fetched.", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().get("tasks").size());
    }

    @Test
    public void testViewParticularTaskWithValidUserIdAndTaskId() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        ResponseEntity<ApiResponse<Map<String, TaskDto>>> response = taskImplementation.viewParticularTask(user.getId().toString(), task.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetch success.", response.getBody().getMessage());
        assertEquals(task.getTitle(), response.getBody().getData().get("tasks").title());
    }

    @Test
    public void testViewTaskByStatusWithValidUserIdAndTaskStatus() {
        user.getTasks().add(task);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(taskRepository.findByStatus(task.getStatus())).thenReturn(Collections.singletonList(task));

        ResponseEntity<ApiResponse<Map<String, List<TaskDto>>>> response = taskImplementation.viewTaskByStatus(user.getId().toString(), task.getStatus());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task fetch success", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().get("tasks").size());
    }

    @Test
    public void testEditTaskWithValidUserIdTaskIdTitleAndDescription() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        ResponseEntity<ApiResponse<String>> response = taskImplementation.editTask(user.getId().toString(), task.getId(), "New Title", "New Description");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task was updated successfully", response.getBody().getMessage());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testMarkTaskAsPendingWithValidUserIdAndTaskId() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        ResponseEntity<ApiResponse<String>> response = taskImplementation.markTaskAsPending(user.getId().toString(), task.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task was successfully marked pending.", response.getBody().getMessage());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testMarkTaskDoneWithValidUserIdAndTaskId() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        ResponseEntity<ApiResponse<String>> response = taskImplementation.markTaskDone(user.getId().toString(), task.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task was successfully marked done.", response.getBody().getMessage());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testDeleteTaskWithValidUserIdAndTaskId() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        ResponseEntity<ApiResponse<String>> response = taskImplementation.deleteTask(user.getId().toString(), task.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task was successfully deleted.", response.getBody().getMessage());
        verify(taskRepository, times(1)).deleteById(task.getId());
    }

    /// TASK-EXCEPTION => All Task Exceptions

    @Test
    public void testCreateTaskWithEmptyTitleOrEmptyDescriptionThrowsTaskException() {
        assertThrows(TaskException.class, () -> taskImplementation.createTask("", "", null, user.getId().toString()));
    }

    @Test
    public void testActionWithInvalidTaskIdThrowsTaskException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(taskRepository.findById(23L)).thenReturn(Optional.empty());

        assertThrows(TaskException.class, () -> taskImplementation.deleteTask(user.getId().toString(), 23L));
        assertThrows(TaskException.class, () -> taskImplementation.viewParticularTask(user.getId().toString(), 23L));
        assertThrows(TaskException.class, () -> taskImplementation.markTaskAsPending(user.getId().toString(), 23L));
        assertThrows(TaskException.class, () -> taskImplementation.markTaskDone(user.getId().toString(), 23L));
        assertThrows(TaskException.class, () -> taskImplementation.editTask(user.getId().toString(), 23L, "", ""));
    }

    @Test
    public void testEditTaskWithNullTitleAndDescriptionThrowsTaskException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        assertThrows(TaskException.class, () -> taskImplementation.editTask(user.getId().toString(), task.getId(), null, null));
    }

    @Test
    public void testActionOnAnotherUserTaskThrowsTaskException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.findById(task2.getId())).thenReturn(Optional.of(task2));

        assertThrows(TaskException.class, () -> taskImplementation.viewParticularTask(user.getId().toString(), task2.getId()));
        assertThrows(TaskException.class, () -> taskImplementation.markTaskAsPending(user.getId().toString(), task2.getId()));
        taskImplementation.markTaskAsPending(user2.getId().toString(), task2.getId());
        assertThrows(TaskException.class, () -> taskImplementation.editTask(user.getId().toString(), task2.getId(), null, null));
        assertThrows(TaskException.class, () -> taskImplementation.markTaskDone(user.getId().toString(), task2.getId()));
        assertThrows(TaskException.class, () -> taskImplementation.deleteTask(user.getId().toString(), task2.getId()));
    }

    /// DOES NOT EXIST USER ID - ACTIVITY-TRACKER-EXCEPTION => UserId does not exist
    @Test
    public void testCreateTaskWithUnExistingUserIdThrowsActivityTrackerException() {
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.createTask("Title", "Description", TaskStatus.PENDING, UUID.randomUUID().toString()));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.editTask(UUID.randomUUID().toString(), 1L, "Title", "Description"));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.viewAllTasks(UUID.randomUUID().toString()));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.viewTaskByStatus(UUID.randomUUID().toString(), null));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.viewParticularTask(UUID.randomUUID().toString(), 1L));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.markTaskDone(UUID.randomUUID().toString(), 1L));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.markTaskAsPending(UUID.randomUUID().toString(), 1L));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.deleteTask(UUID.randomUUID().toString(), task.getId()));
    }

    /// INVALID USER ID - ACTIVITY-TRACKER-EXCEPTION => UserId not properly formatted
    @Test
    public void testCreateTaskWithInvalidUserIdThrowsActivityTrackerException() {
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.createTask("Title", "Description", TaskStatus.PENDING, "userId"));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.editTask("userId", 1L, "Title", "Description"));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.viewAllTasks("userId"));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.viewTaskByStatus("userId", null));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.viewParticularTask("userId", 1L));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.markTaskDone("userId", 1L));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.markTaskAsPending("userId", 1L));
        assertThrows(ActivityTrackerException.class, () -> taskImplementation.deleteTask("userId", task.getId()));
    }
}