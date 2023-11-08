package com.example.activitytrackerapi.controllers;

import com.example.activitytrackerapi.dto.UserDto;
import com.example.activitytrackerapi.payload.ApiResponse;
import com.example.activitytrackerapi.services.implementations.AuthImplementation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping(name = "Authentication API EndPoint", value = "/auth")
public class AuthController {
    private final AuthImplementation auth;

    public record AuthRecord(String firstName, String lastName, String username, String password) { }

    @PostMapping(name = "Signup", value = "/create-account")
    public ResponseEntity<ApiResponse<Map<String, UserDto>>> signup(@RequestBody AuthRecord signup, HttpSession session) {
        return auth.signup(signup.firstName(), signup.lastName(), signup.username(), signup.password(), session);
    }

    @PostMapping(name = "Login", value = "/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody AuthRecord login, HttpSession session) {
        return auth.login(login.username, login.password, session);
    }
}
