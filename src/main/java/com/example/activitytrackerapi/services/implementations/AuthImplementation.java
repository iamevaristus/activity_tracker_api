package com.example.activitytrackerapi.services.implementations;

import com.example.activitytrackerapi.config.UserMapper;
import com.example.activitytrackerapi.dto.UserDto;
import com.example.activitytrackerapi.exceptions.AuthException;
import com.example.activitytrackerapi.models.User;
import com.example.activitytrackerapi.payload.ApiResponse;
import com.example.activitytrackerapi.repositories.UserRepository;
import com.example.activitytrackerapi.services.interfaces.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class AuthImplementation implements AuthService {
    private final UserRepository repository;

    @Override
    public ResponseEntity<ApiResponse<String>> login(String username, String password, HttpSession session) {
        AtomicReference<ApiResponse<String>> apiResponse = new AtomicReference<>();
        if(repository.usernameExists(username)) {
            repository.checkIfLoginMatches(username, password).ifPresentOrElse(user -> {
                session.setAttribute("current_user", user.getId());
                apiResponse.set(new ApiResponse<>(
                        HttpStatus.OK,
                        "Authentication successful"
                ));
            }, () -> {
                throw new AuthException("Username and password does not match.", HttpStatus.BAD_REQUEST);
            });
        } else {
            throw new AuthException("Username does not exist.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(apiResponse.get(), apiResponse.get().getHttpStatus());
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String, UserDto>>> signup(
            String firstName, String lastName, String username, String password, HttpSession session
    ) {
        if(username == null || password == null || firstName == null || lastName == null
                || username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()
        ) {
            throw new AuthException("Incomplete signup details.", HttpStatus.BAD_REQUEST);
        } else if(repository.usernameExists(username)) {
            throw new AuthException("Username already exists.", HttpStatus.BAD_REQUEST);
        } else {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setCreatedAt(LocalDateTime.now());
            repository.save(user);
            Map<String, UserDto> map = new HashMap<>();
            map.put("user_details", UserMapper.INSTANCE.userToUserDto(user));
            session.setAttribute("current_user", user.getId());
            ApiResponse<Map<String, UserDto>> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Authentication successful",
                    map
            );
            return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
        }
    }
}
