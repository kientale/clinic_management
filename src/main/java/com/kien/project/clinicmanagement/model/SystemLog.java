package com.kien.project.clinicmanagement.model;

import java.time.LocalDateTime;

public class SystemLog {
    private Long id;
    private String userCode;
    private String action;
    private LocalDateTime timestamp;

    public SystemLog() {}

    public SystemLog(String userCode, String action) {
        this.userCode = userCode;
        this.action = action;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
} 