package com.example.activitytrackerapi.repositories;

import com.example.activitytrackerapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("select u from User u where u.username = ?1 and u.password = ?2")
    Optional<User> checkIfLoginMatches(String username, String password);
    @Query("select (count(u) > 0) from User u where u.username = ?1")
    boolean usernameExists(@NonNull String username);
}