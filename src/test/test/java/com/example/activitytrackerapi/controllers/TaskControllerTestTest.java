package com.example.activitytrackerapi.controllers;

import com.example.activitytrackerapi.dto.TaskDto;
import com.example.activitytrackerapi.enums.TaskStatus;
import com.example.activitytrackerapi.exceptions.ActivityTrackerException;
import com.example.activitytrackerapi.services.implementations.TaskImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskImplementation taskImplementation;

    private MockHttpSession session;

    @BeforeEach
    public void setup() {
        session = new MockHttpSession();
        session.setAttribute("current_user", "testUser");
    }

    @Test
    public void testViewTasksByStatusWhenValidStatusAndUserAuthenticatedThenReturnTasks() throws Exception {
        when(taskImplementation.viewTaskByStatus(anyString(), any(TaskStatus.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(MockMvcRequestBuilders.get("/task?status=1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testViewTasksByStatusWhenInvalidStatusAndUserAuthenticatedThenThrowException() throws Exception {
        when(taskImplementation.viewTaskByStatus(anyString(), any(TaskStatus.class)))
                .thenThrow(new ActivityTrackerException("Invalid status"));

        mockMvc.perform(MockMvcRequestBuilders.get("/task?status=invalid")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testViewTasksByStatusWhenUserNotAuthenticatedThenThrowException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/task?status=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
