package com.example.activitytrackerapi.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    PENDING("Pending"),
    DONE("Done"),
    IN_PROGRESS("In progress");

    private final String type;
    TaskStatus(String type) {
        this.type = type;
    }
}
