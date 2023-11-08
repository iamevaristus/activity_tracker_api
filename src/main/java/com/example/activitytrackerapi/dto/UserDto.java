package com.example.activitytrackerapi.dto;

import lombok.Value;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link com.example.activitytrackerapi.models.User}
 */
public record UserDto(UUID id, String firstName, String lastName, String username) {  }