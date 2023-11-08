package com.example.activitytrackerapi.services.interfaces;

import com.example.activitytrackerapi.dto.UserDto;
import com.example.activitytrackerapi.payload.ApiResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthService {
    ResponseEntity<ApiResponse<String>> login(String username, String password, HttpSession session);
    ResponseEntity<ApiResponse<Map<String, UserDto>>> signup(
            String firstName, String lastName, String username, String password, HttpSession session
    );
}
