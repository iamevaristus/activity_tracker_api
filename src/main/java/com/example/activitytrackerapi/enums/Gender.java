package com.example.activitytrackerapi.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHERS("Others");

    private final String type;
    Gender(String type) {
        this.type = type;
    }
}
