package com.example.activitytrackerapi.repositories;

import com.example.activitytrackerapi.enums.TaskStatus;
import com.example.activitytrackerapi.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select t from Task t where t.status = ?1")
    List<Task> findByStatus(@NonNull TaskStatus status);
}