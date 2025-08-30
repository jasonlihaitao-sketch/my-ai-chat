package com.example.enums;

public enum MessageRole {
    USER(1),
    ASSISTANT(2);

    private final Integer role;

    MessageRole(Integer role) {
        this.role = role;
    }

    public Integer getRole() {
        return role;
    }
}