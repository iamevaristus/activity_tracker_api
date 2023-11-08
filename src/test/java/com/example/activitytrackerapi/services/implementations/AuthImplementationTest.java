package com.example.activitytrackerapi.services.implementations;

import com.example.activitytrackerapi.config.UserMapper;
import com.example.activitytrackerapi.dto.UserDto;
import com.example.activitytrackerapi.exceptions.AuthException;
import com.example.activitytrackerapi.models.User;
import com.example.activitytrackerapi.payload.ApiResponse;
import com.example.activitytrackerapi.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthImplementationTest {

    @Mock
    private UserRepository repository;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthImplementation authImplementation;
    private User user;
    private User userNoId;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("test");
        user.setPassword("test");
        user.setFirstName("test");
        user.setLastName("test");

        userNoId = new User();
        userNoId.setUsername("test");
        userNoId.setPassword("test");
        userNoId.setFirstName("test");
        userNoId.setLastName("test");
    }

    @Test
    public void testLoginWhenUsernameAndPasswordAreCorrectThenReturnOk() {
        when(repository.usernameExists(user.getUsername())).thenReturn(true);
        when(repository.checkIfLoginMatches(user.getUsername(), user.getPassword())).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse<String>> response = authImplementation.login(
                user.getUsername(), user.getPassword(), session
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Authentication successful", response.getBody().getMessage());
    }

    @Test
    public void testLoginWhenUsernameDoesNotExistThenThrowAuthException() {
        when(repository.usernameExists("test")).thenReturn(false);

        assertThrows(AuthException.class, () -> authImplementation.login("test", "test", session));
    }

    @Test
    public void testLoginWhenPasswordIsIncorrectThenThrowAuthException() {
        when(repository.usernameExists(user.getUsername())).thenReturn(true);
        when(repository.checkIfLoginMatches(user.getUsername(), "wrong")).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> authImplementation.login(user.getUsername(), "wrong", session));
    }

    @Test
    public void testSignupWhenAllFieldsProvidedAndUsernameIsUniqueThenReturnOk() {
        when(repository.usernameExists(userNoId.getUsername())).thenReturn(false);
        when(repository.save(any(User.class))).thenReturn(userNoId);

        ResponseEntity<ApiResponse<Map<String, UserDto>>> response = authImplementation.signup(
                userNoId.getFirstName(), userNoId.getLastName(), userNoId.getUsername(), userNoId.getPassword(), session
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Authentication successful", response.getBody().getMessage());
        assertEquals(UserMapper.INSTANCE.userToUserDto(userNoId), response.getBody().getData().get("user_details"));
    }

    @Test
    public void testSignupWhenAnyFieldIsMissingThenThrowAuthException() {
        assertThrows(AuthException.class, () -> authImplementation.signup(
                null, "test", "test", "test", session
        ));
        assertThrows(AuthException.class, () -> authImplementation.signup(
                "test", null, "test", "test", session
        ));
        assertThrows(AuthException.class, () -> authImplementation.signup(
                "test", "test", null, "test", session
        ));
        assertThrows(AuthException.class, () -> authImplementation.signup(
                "test", "test", "test", null, session
        ));
    }

    @Test
    public void testSignupWhenUsernameAlreadyExistsThenThrowAuthException() {
        when(repository.usernameExists("test")).thenReturn(true);

        assertThrows(AuthException.class, () -> authImplementation.signup(
                "test", "test", "test", "test", session
        ));
    }
}
